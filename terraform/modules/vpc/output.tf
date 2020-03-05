output "vpc_id" {

  value = length(var.public_subnet_id) == 0 && length(var.private_subnet_id) == 0 && length(aws_vpc.main) > 0 ? aws_vpc.main[0].id : length(data.aws_subnet.subnet) > 0 ? data.aws_subnet.subnet[0].vpc_id : ""
}
