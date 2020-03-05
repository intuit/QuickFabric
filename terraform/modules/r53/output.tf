output "r53_hosted_zone" {

  value = var.hosted_zone_name_exists ? data.aws_route53_zone.quickfabric[0].name : aws_route53_zone.quickfabric_zone[0].name
}
