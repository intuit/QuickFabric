variable "sg_name" {}
variable "vpc_id" {}
variable "whitelist" {}
variable "whitelist_sg" { default = { "inbound" : [] } }
variable "tags" {}
variable "cidr_admin_whitelist" { default = [] }
variable "security_groups" { default = [] }

