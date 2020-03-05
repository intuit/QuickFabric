variable "region" {}
variable "provision" { default = true }
variable "user_data" { default = "" }
variable "server_name" {}
variable "sg_ids" { type = list }
variable "device_mount_path" { default = "/dev/sdh" }
variable "ebs" { type = bool }
variable "instance_type" {}
variable "subnet_id" {}
variable "ami_id" {}
variable "vpc_id" {}
variable "cidr_admin_whitelist" { default = [] }
variable "key_pair" {}
variable "tags" {}
