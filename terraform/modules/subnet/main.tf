locals {
  common_tags = {
    Name = "quickfabric-${var.name}"
  }
}

# Get the subnet details including VPC id.
data "aws_subnet" "subnet" {

  count = length(var.subnet_id) > 0 ? 1 : 0

  id = var.subnet_id[0]
}

data "aws_availability_zones" "available" {}

# Creates subnet if no Subnets are provided
resource "aws_subnet" "main" {
  # Mentioning the availability region since the ebs volume lifecycle is related to this
  availability_zone       = data.aws_availability_zones.available.names[0]
  count                   = length(var.subnet_id) > 0 ? 0 : length(var.subnet_cidr_block)
  vpc_id                  = var.vpc_id
  cidr_block              = element(var.subnet_cidr_block, count.index)
  map_public_ip_on_launch = true
  tags                    = merge(local.common_tags, var.tags)
}
