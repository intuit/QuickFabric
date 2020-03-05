output "subnet_id" {

  value = length(var.subnet_id) > 0 ? var.subnet_id : aws_subnet.main[*].id
}

output "subnet_az" {
  value = length(var.subnet_id) > 0 ? data.aws_subnet.subnet[0].availability_zone : aws_subnet.main[0].availability_zone
}
