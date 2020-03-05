
locals {
  common_tags = {
    Name = "quickfabric-security-group"
  }
}

data "external" "getip" {
  program = ["bash", "./modules/sg/get_ip.sh"]
}

# Security group rules for QuickFabric Resources
resource "aws_security_group" "whitelist" {
  vpc_id      = var.vpc_id
  name        = var.sg_name
  description = "QuickFabric SecurityGroup"

  # Whitelisting inbound from security group
  dynamic "ingress" {
    iterator = rule
    for_each = var.whitelist_sg.inbound

    content {
      protocol    = rule.value.protocol
      from_port   = rule.value.from_port
      to_port     = rule.value.to_port
      security_groups = var.security_groups
    }
  }
  # Whitelisting inbound from ip addresses
  dynamic "ingress" {
    iterator = rule
    for_each = var.whitelist.inbound

    content {
      protocol    = rule.value.protocol
      from_port   = rule.value.from_port
      to_port     = rule.value.to_port
      cidr_blocks = setunion(rule.value.cidr_blocks, lookup(data.external.getip.result, "result", null) == null ? [] : list(lookup(data.external.getip.result, "result")), var.cidr_admin_whitelist)
    }
  }

  # Whitelisting outbound access to ip addresses
  dynamic "egress" {
    iterator = rule
    for_each = var.whitelist.outbound

    content {
      protocol    = rule.value.protocol
      from_port   = rule.value.from_port
      to_port     = rule.value.to_port
      cidr_blocks = rule.value.cidr_blocks
    }
  }

  tags = merge(local.common_tags, var.tags)
}

