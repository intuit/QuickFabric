# Specify the compatible terraform version
terraform {
  required_version = ">= 0.12.0"
}


# Creates a VPC
module "vpc" {
  source = "./modules/vpc"

  public_subnet_id  = lookup(var.public_subnet_id, data.aws_caller_identity.current.account_id, [])
  private_subnet_id = lookup(var.private_subnet_id, data.aws_caller_identity.current.account_id, [])
  cidr_block        = "192.168.0.0/16"

  tags = var.tags
}

# Creates a Public Subnet
module "public_subnet" {
  source = "./modules/subnet"

  name              = "public"
  subnet_id         = lookup(var.public_subnet_id, data.aws_caller_identity.current.account_id, [])
  vpc_id            = module.vpc.vpc_id
  subnet_cidr_block = ["192.168.1.0/24", "192.168.2.0/24"]

  tags = var.tags
}

# Creates a Private Subnet
module "private_subnet" {
  source = "./modules/subnet"

  name              = "private"
  subnet_id         = lookup(var.private_subnet_id, data.aws_caller_identity.current.account_id, [])
  vpc_id            = module.vpc.vpc_id
  subnet_cidr_block = ["192.168.3.0/24", "192.168.5.0/24", "192.168.6.0/24"]

  tags = var.tags
}

# Creates a InternetGateway and attaches the public subnets
module "igw" {
  source = "./modules/igw"


  input_subnet_id  = lookup(var.public_subnet_id, data.aws_caller_identity.current.account_id, [])
  public_subnet_id = module.public_subnet.subnet_id
  vpc_id           = module.vpc.vpc_id

  tags = var.tags
}

# Creates a NATGateway and attaches the private subnets
module "nat_gw" {
  source = "./modules/nat"

  input_subnet_id   = lookup(var.private_subnet_id, data.aws_caller_identity.current.account_id, [])
  public_subnet_id  = module.public_subnet.subnet_id
  private_subnet_id = module.private_subnet.subnet_id
  vpc_id            = module.vpc.vpc_id
  mod_depends_on    = module.igw

  tags = var.tags
}

# Creates a Route53 Hosted Zone
module "r53" {
  source = "./modules/r53"

  hosted_zone_name_exists = var.hosted_zone_name_exists
  zone_name               = var.quickfabric_zone_name
  www_domain_name         = var.www_domain_name

  tags = var.tags

}

# Creates Security Groups for quickfabric.
module "sg" {
  source = "./modules/sg"

  vpc_id               = module.vpc.vpc_id
  sg_name              = "Master security Group"
  whitelist            = var.master_sg
  whitelist_sg         = var.master_sg_group
  security_groups      = list(module.bastion_sg.sg_id)
  cidr_admin_whitelist = concat(var.cidr_admin_whitelist, list("${module.bastion.private_ip}/32"))


  tags = var.tags
}

# Uploads a new keypair
resource "aws_key_pair" "keypair" {
  count = var.key_pair == "" ? 1 : 0

  key_name   = "quickfabric-deployer-key"
  public_key = file(var.public_key_path)
}

# Creates Security Groups for quickfabric.
module "bastion_sg" {
  source = "./modules/sg"

  vpc_id  = module.vpc.vpc_id
  sg_name = "Bastion security Group"
  whitelist = {
    "inbound" : [
      {
        "protocol" : "tcp",
        "from_port" : "22",
        "to_port" : "22",
        "cidr_blocks" : var.cidr_admin_whitelist
      }
    ],
    "outbound" : [
      {
        "protocol" : "-1"
        "from_port" : "0"
        "to_port" : "0"
        "cidr_blocks" : ["0.0.0.0/0"]
      }
    ]

  }

  tags = var.tags
}

# Provisions the EC2 instances as Bastion/Jump Server
module "bastion" {
  source = "./modules/ec2"

  provision = var.bastion_sg == "" ? true : false

  region        = var.region
  server_name   = "quickfabric-bastion-server"
  instance_type = var.instance_type
  subnet_id     = module.public_subnet.subnet_id
  vpc_id        = module.vpc.vpc_id

  ami_id               = coalesce(lookup(var.ami_ids, data.aws_caller_identity.current.account_id, ""), data.aws_ami.ubuntu.id)
  cidr_admin_whitelist = var.cidr_admin_whitelist
  key_pair             = var.key_pair == "" && length(aws_key_pair.keypair) > 0 ? aws_key_pair.keypair[0].key_name : var.key_pair
  sg_ids               = [module.bastion_sg.sg_id]

  ebs = false

  tags = var.tags
}


# Creates S3 bucket for quickfabric.
module "s3" {
  source = "./modules/s3"

  bucket_name = "${var.quickfabric_log_bucket}-${data.aws_caller_identity.current.account_id}-${var.region}"
  directories = ["artifacts/", "logs/"]

  tags = var.tags
}


# Loads the existing yaml file and add the new account
locals {
  existing_yaml_content = fileexists("../Backend/serverless-deploy/deploy.yml") ? yamldecode(file("../Backend/serverless-deploy/deploy.yml")) : yamldecode("{}")
  deploy = {
    "${data.aws_caller_identity.current.account_id}" = {
      env        = var.env,
      vpc_id     = module.vpc.vpc_id,
      subnet_ids = module.private_subnet.subnet_id,
      region     = var.region,
      plugins    = ["emr-autoscaling", "emr-testsuites"]
    }
  }
  updated_yaml_content = merge(local.existing_yaml_content, local.deploy)
}

# Creates the config file in local deploy.yml
resource "local_file" "deploy_yml" {
  content  = yamlencode(local.updated_yaml_content)
  filename = "../Backend/serverless-deploy/deploy.yml"
}

# Create user data file using templates.
data "template_file" "metadata" {
  template = file("./templates/serverless/output.conf.tpl")

  vars = {
    vpc_id             = module.vpc.vpc_id
    public_subnet_ids  = jsonencode(module.public_subnet.subnet_id)
    private_subnet_ids = jsonencode(module.private_subnet.subnet_id)
    key_pair           = var.key_pair == "" ? aws_key_pair.keypair[0].key_name : var.key_pair
    bucket             = module.s3.bucket_name
    emr_version        = var.emr_version
    logs_path          = "logging/"
    region             = var.region
    env                = var.env
    r53_hosted_zone    = module.r53.r53_hosted_zone
  }
}

# Creates the config file in local emr-metadata.json
resource "local_file" "metadata" {
  content              = data.template_file.metadata.rendered
  filename             = "../Backend/emr-cluster-ops/src/conf/${data.aws_caller_identity.current.account_id}/emr-metadata.json"
  directory_permission = "0755"
}

# Creates the config file in local emr-config.json
resource "local_file" "emr_config" {
  content              = file("./templates/serverless/emr-config.json")
  filename             = "../Backend/emr-cluster-ops/src/conf/${data.aws_caller_identity.current.account_id}/emr-nonkerb-config.json"
  directory_permission = "0755"
}


data "archive_file" "backend" {
  output_path = "/tmp/backend.zip"
  type        = "zip"
  source_dir  = "../Backend"
}

locals {

  local_serverless_docker_path = "../Backend/docker/serverless-deploy"
  python_command_deploy        = "python3 /serverless/Backend/serverless-deploy/quickfabric_setup.py deploy"
  python_command_remove        = "python3 /serverless/Backend/serverless-deploy/quickfabric_setup.py remove"
}

resource "null_resource" "serverless_container" {
  provisioner "local-exec" {
    command     = "docker build -t serverless_qf ."
    working_dir = local.local_serverless_docker_path
  }
  provisioner "local-exec" {
    when    = destroy
    command = "docker rmi -f $(docker images -f reference=serverless_qf -q)"
  }
}

data "external" "get_env_variables" {
  program = ["python", "./scripts/env.py"]
}

# Run severless deployment
resource "null_resource" "run_serverless" {
  triggers = {
    config_files = data.archive_file.backend.output_md5
  }

  provisioner "local-exec" {
    command     = "docker run --rm -t -v /tmp:/tmp -v ${path.cwd}/../:/serverless -v ${lookup(data.external.get_env_variables.result, "HOME")}/.aws:/root/.aws  -e AWS_PROFILE=${lookup(data.external.get_env_variables.result, "AWS_PROFILE")} -e AWS_ACCESS_KEY_ID=${lookup(data.external.get_env_variables.result, "AWS_ACCESS_KEY_ID")} -e AWS_SECRET_ACCESS_KEY=${lookup(data.external.get_env_variables.result, "AWS_SECRET_ACCESS_KEY")} -e AWS_SESSION_TOKEN=${lookup(data.external.get_env_variables.result, "AWS_SESSION_TOKEN")}  serverless_qf \"${local.python_command_deploy}\""
    working_dir = "../Backend/serverless-deploy/"
    environment = {
      depends_deploy_yaml         = local_file.deploy_yml.filename
      depends_emr_metadata        = local_file.metadata.filename
      depends_emr_comfig          = local_file.emr_config.filename
      depends_serverless_qf_image = null_resource.serverless_container.id
    }
  }
}

data "external" "api" {
  program = ["echo", "{\"file_path\":\"/tmp/apigateway-output.json\", \"depends_on\" : \"${null_resource.run_serverless.id}\" }"]
}

locals {
  api_creds = fileexists(lookup(data.external.api.result, "file_path")) ? jsondecode(file(lookup(data.external.api.result, "file_path"))) : jsondecode("None")
}


resource "null_resource" "remove_serverless" {
  provisioner "local-exec" {
    when        = destroy
    command     = "docker run --rm -t -v /tmp:/tmp -v ${path.cwd}/../:/serverless -v ${lookup(data.external.get_env_variables.result, "HOME")}/.aws:/root/.aws  -e AWS_PROFILE=${lookup(data.external.get_env_variables.result, "AWS_PROFILE")} -e AWS_ACCESS_KEY_ID=${lookup(data.external.get_env_variables.result, "AWS_ACCESS_KEY_ID")} -e AWS_SECRET_ACCESS_KEY=${lookup(data.external.get_env_variables.result, "AWS_SECRET_ACCESS_KEY")} -e AWS_SESSION_TOKEN=${lookup(data.external.get_env_variables.result, "AWS_SESSION_TOKEN")}  serverless_qf \"${local.python_command_remove}\""
    working_dir = "../Backend/serverless-deploy/"
    environment = {
      depends_deploy_yaml         = local_file.deploy_yml.filename
      depends_emr_metadata        = local_file.metadata.filename
      depends_emr_comfig          = local_file.emr_config.filename
      depends_serverless_qf_image = null_resource.serverless_container.id
    }
  }
}

# Fetch the latest Ubuntu image from Canonical Ubuntu Account
data "aws_ami" "ubuntu" {
  most_recent = true

  # Canonical Ubnuntu distribution
  owners = ["099720109477"]

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-bionic-18.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}


# EMR Service role, policy
data "aws_iam_policy_document" "emr_assume_role_policy" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["elasticmapreduce.amazonaws.com"]
    }
    actions = ["sts:AssumeRole"]
  }
}

resource "aws_iam_role" "emr_service_role" {
  name               = "emr-service-role-quickfabric"
  assume_role_policy = data.aws_iam_policy_document.emr_assume_role_policy.json
}

resource "aws_iam_role_policy" "emr_service_role_policy" {
  name   = "emr_service_role_policy"
  role   = aws_iam_role.emr_service_role.id
  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect" : "Allow",
            "Action" : ["s3:*"],
            "Resource" : ["*"]
        }
    ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "emr_service_role" {
  role       = aws_iam_role.emr_service_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonElasticMapReduceRole"
}


# EMR IAM resources for EC2 and Autoscaling ss
#
data "aws_iam_policy_document" "ec2_assume_role" {
  statement {
    effect = "Allow"
    principals {
      type = "Service"
      identifiers = ["ec2.amazonaws.com",
      "application-autoscaling.amazonaws.com"]
    }
    actions = ["sts:AssumeRole"]
  }
}

resource "aws_iam_role" "emr_ec2_instance_profile_role" {
  name               = "emr-ec2-profile-quickfabric"
  assume_role_policy = data.aws_iam_policy_document.ec2_assume_role.json
}

resource "aws_iam_role_policy" "emr_ec2_role_policy" {
  name   = "emr_role_policy"
  role   = aws_iam_role.emr_ec2_instance_profile_role.id
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect" : "Allow",
      "Action" : ["s3:*"],
      "Resource" : ["*"]
  },
  {
      "Effect" : "Allow",
      "Action" :
            [
                "s3:List*",
                "s3:Head*",
                "s3:Get*",
                "iam:GetRole",
                "cloudformation:ListExports"
            ],
     "Resource" : ["*"]
  }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "emr_ec2_instance_profile" {
  role       = aws_iam_role.emr_ec2_instance_profile_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonElasticMapReduceforEC2Role"
}

resource "aws_iam_role_policy_attachment" "emr_ec2_instance_profile_autoscaling" {
  role       = aws_iam_role.emr_ec2_instance_profile_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonElasticMapReduceforAutoScalingRole"
}
resource "aws_iam_instance_profile" "instance_profile" {

  name = "emr-ec2-profile-quickfabric"
  role = aws_iam_role.emr_ec2_instance_profile_role.name
}

