# Specify the compatible terraform version
terraform {
  required_version = ">= 0.12.0"
}

# S3 upload paths for QuickFabric and serverless
locals {

  db_artifacts_path         = "artifacts/DB_artifacts.zip"
  docker_artifacts_path     = "artifacts/docker-compose.yml"
  frontend_artifacts_path   = "artifacts/frontend_artifacts.zip"
  middleware_artifacts_path = "artifacts/middleware_artifacts.zip"
}

# Identify the Caller account details
data "aws_caller_identity" "current" {}

# Identify if running for a Parent account as per input.tfvars
locals {
  parent = data.aws_caller_identity.current.account_id == lookup(var.account_ids, "parent_account_id") ? true : false
}


# Provisions the EC2 instance for QuickFabric Server
module "qf" {
  source = "./modules/ec2"

  provision     = local.parent
  region        = var.region
  server_name   = "quickfabric-qf-server"
  instance_type = var.instance_type
  subnet_id     = module.public_subnet.subnet_id
  vpc_id        = module.vpc.vpc_id
  user_data     = local.parent && length(data.template_file.qf_user_data) > 0 ? data.template_file.qf_user_data[0].rendered : ""

  ami_id               = coalesce(lookup(var.ami_ids, data.aws_caller_identity.current.account_id, ""), data.aws_ami.ubuntu.id)
  cidr_admin_whitelist = var.cidr_admin_whitelist
  key_pair             = var.key_pair == "" && length(aws_key_pair.keypair) > 0 ? aws_key_pair.keypair[0].key_name : var.key_pair
  sg_ids               = [module.sg.sg_id]

  ebs = true

  tags = var.tags
}

# Archive QuickFabric application source
data "archive_file" "db_archive" {
  count       = local.parent ? 1 : 0
  output_path = "/tmp/db_artifacts.zip"
  type        = "zip"
  source_dir  = "../DB"

}
# Archive QuickFabric application source
data "archive_file" "docker_archive" {
  count       = local.parent ? 1 : 0
  output_path = "/tmp/docker-compose.zip"
  type        = "zip"
  source_file = "../docker-compose.yml"

}

# Archive QuickFabric application source
data "archive_file" "middleware_archive" {
  count = local.parent ? 1 : 0

  output_path = "/tmp/middleware_artifacts.zip"
  type        = "zip"
  source_dir  = "../Middleware"

}

# Archive QuickFabric application source
data "archive_file" "frontend_archive" {
  count = local.parent ? 1 : 0

  output_path = "/tmp/frontend_artifacts.zip"
  type        = "zip"
  source_dir  = "../Frontend"

}

# Create user data file using templates.
data "template_file" "qf_user_data" {
  count = local.parent ? 1 : 0

  template = file("./templates/qf/userdata.tpl")

  vars = {
    MYSQL_PASSWORD            = var.MYSQL_PASSWORD
    AES_SECRET_KEY            = var.AES_SECRET_KEY
    docker_compose_version    = var.docker_compose_version
    md5_db                    = data.archive_file.db_archive[0].output_md5
    md5_docker                = data.archive_file.docker_archive[0].output_md5
    md5_frontend              = data.archive_file.frontend_archive[0].output_md5
    md5_middleware            = data.archive_file.middleware_archive[0].output_md5
    bucket_name               = module.s3.bucket_name
    db_artifacts_path         = local.db_artifacts_path
    docker_artifacts_path     = local.docker_artifacts_path
    middleware_artifacts_path = local.middleware_artifacts_path
    frontend_artifacts_path   = local.frontend_artifacts_path

    db_artifacts_zip         = split("/", local.db_artifacts_path)[1]
    docker_artifacts_zip     = split("/", local.docker_artifacts_path)[1]
    frontend_artifacts_zip   = split("/", local.frontend_artifacts_path)[1]
    middleware_artifacts_zip = split("/", local.middleware_artifacts_path)[1]


  }
}

# Uploads the files and contents to S3 location
module "qf_s3_uploads" {
  source = "./modules/s3_uploads"

  provision   = local.parent
  bucket_name = module.s3.bucket_name
  file_uploads = {
    "${local.db_artifacts_path}" : local.parent && length(data.archive_file.db_archive) > 0 ? data.archive_file.db_archive[0] : null,
    "${local.frontend_artifacts_path}" : local.parent && length(data.archive_file.frontend_archive) > 0 ? data.archive_file.frontend_archive[0] : null,
    "${local.docker_artifacts_path}" : local.parent && length(data.archive_file.docker_archive) > 0 ? data.archive_file.docker_archive[0] : null,
    "${local.middleware_artifacts_path}" : local.parent && length(data.archive_file.middleware_archive) > 0 ? data.archive_file.middleware_archive[0] : null
  }

  tags = var.tags
}

