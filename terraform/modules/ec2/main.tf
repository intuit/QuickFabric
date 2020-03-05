locals {
  common_tags = {
    Name = var.server_name
  }
}

# Get the subnet details including VPC id.
data "aws_subnet" "instance_subnet" {
  count = var.provision ? 1 : 0
  id    = var.subnet_id[0]
}

# Creates a Bastion/Jump EC2 instance.
resource "aws_instance" "qf_server" {

  count = var.provision ? 1 : 0

  ami                    = var.ami_id
  availability_zone      = data.aws_subnet.instance_subnet[0].availability_zone
  instance_type          = var.instance_type
  subnet_id              = var.subnet_id[0]
  key_name               = var.key_pair
  vpc_security_group_ids = var.sg_ids
  iam_instance_profile   = aws_iam_instance_profile.instance_profile[0].name

  user_data_base64 = base64gzip(var.user_data)

  root_block_device {
    volume_type = "gp2"
    volume_size = 50

  }
  tags = merge(local.common_tags, var.tags)
}

# Creates a Elastic IP address
resource "aws_eip" "eip" {

  count = var.provision ? 1 : 0

  vpc  = true
  tags = merge(local.common_tags, var.tags)
}

# Associates the Elastic IP address to the Prometheus instance
resource "aws_eip_association" "eip_assoc" {

  count = var.provision ? 1 : 0

  instance_id   = aws_instance.qf_server[0].id
  allocation_id = aws_eip.eip[0].id

}


# Creates a EBS volume to store persistant data.
resource "aws_ebs_volume" "persistant-disk" {
  count             = var.ebs && var.provision ? 1 : 0
  availability_zone = aws_instance.qf_server[0].availability_zone
  size              = "75"

  tags = merge(local.common_tags, var.tags)
}

# Attaches the EBS volume to the Prometheus server
resource "aws_volume_attachment" "attach-persistant-disk" {
  count        = var.ebs && var.provision ? 1 : 0
  force_detach = true
  device_name  = var.device_mount_path
  volume_id    = aws_ebs_volume.persistant-disk[0].id
  instance_id  = aws_instance.qf_server[0].id

}

# Creates an instance profile for the server
resource "aws_iam_instance_profile" "instance_profile" {

  count = var.provision ? 1 : 0

  name = "${var.server_name}_instance_profile"
  role = aws_iam_role.iam_for_instance[0].name
}

# Creates an IAM role for instance profile
resource "aws_iam_role" "iam_for_instance" {

  count = var.provision ? 1 : 0

  name                  = "quickfabric_${var.server_name}_instance_role"
  assume_role_policy    = file("./modules/ec2/files/instance_role.txt")
  force_detach_policies = true

  tags = merge(local.common_tags, var.tags)
}

# Creates an IAM policy for instance profile
resource "aws_iam_role_policy" "instance_policy" {

  count = var.provision ? 1 : 0

  name   = "quickfabric_${var.server_name}_instance_policy"
  role   = aws_iam_role.iam_for_instance[0].id
  policy = file("./modules/ec2/files/instance_policy.txt")

}

