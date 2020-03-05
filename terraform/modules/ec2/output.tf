output "server_ip" {

  value = var.provision && length(aws_eip.eip) > 0 ? aws_eip.eip[0].public_ip : ""
}
output "private_ip" {

  value = var.provision && length(aws_instance.qf_server) > 0 ? aws_instance.qf_server[0].private_ip : ""
}
