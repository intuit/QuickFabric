
locals {
  common_tags = {
    Name = "quickfabric-dns-zone"
  }
}

data "aws_route53_zone" "quickfabric" {
  count = var.hosted_zone_name_exists ? 1 : 0
  name  = var.zone_name
}


# Creates a Route53 zone record to access EMR cluster operations
resource "aws_route53_zone" "quickfabric_zone" {

  count         = var.hosted_zone_name_exists == false ? 1 : 0
  name          = var.zone_name
  force_destroy = true

  tags = merge(local.common_tags, var.tags)
}

# Creates a Route53 zone record to access EMR cluster operations
resource "aws_route53_record" "quickfabric_record" {

  zone_id = var.hosted_zone_name_exists ? data.aws_route53_zone.quickfabric[0].zone_id : aws_route53_zone.quickfabric_zone[0].zone_id
  name    = var.www_domain_name
  type    = "A"
  ttl     = "3600"
  records = ["127.0.0.1"]

}

