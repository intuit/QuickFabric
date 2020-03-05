variable "env" {
  type    = string
  default = "dev"
}
variable "deploy" {

  type = map(object({
    env        = string,
    vpc_id     = string,
    subnet_ids = list(string),
    region     = string,
  plugins = list(string) }))
  default = {}
}


variable "emr_version" {
  type    = string
  default = "emr-5.26.0"
}

variable "MYSQL_PASSWORD" { 
	type = string
	default = "" 
}

variable "AES_SECRET_KEY" { 
	type = string 
	default = ""
}

variable "master_sg_group" {
  description = "Security group rules"
  type = map(list(object({
    protocol    = string
    from_port   = string
    to_port     = string
    security_groups = list(string)
  })))

  default = {
    "inbound" : [
      {
        "protocol" : "tcp",
        "from_port" : "22",
        "to_port" : "22",
        "security_groups" : []
      }
    ]
  }

}

variable "master_sg" {
  description = "Security group rules"
  type = map(list(object({
    protocol    = string
    from_port   = string
    to_port     = string
    cidr_blocks = list(string)
  })))

  default = {
    "inbound" : [
      {
        "protocol" : "tcp",
        "from_port" : "22",
        "to_port" : "22",
        "cidr_blocks" : []
      },
      {
        "protocol" : "tcp",
        "from_port" : "80",
        "to_port" : "80",
        "cidr_blocks" : []
      },
      {
        "protocol" : "tcp",
        "from_port" : "8080",
        "to_port" : "8080",
        "cidr_blocks" : []
      }
    ],
    "outbound" : [
      {
        "protocol" : "-1"
        "from_port" : "0"
        "to_port" : "0"
        "cidr_blocks" : ["0.0.0.0/0"]
      }
    ]
  }

}

variable "region" {
  description = "AWS region to deploy QuickFabric"
  type        = string
  default     = "us-west-2"
}

variable "account_ids" {
  description = "Parent and Child account ID's"
  type = object({
    parent_account_id = string
  child_account_ids = list(string) })
}

variable "public_subnet_id" {
  description = "Subnet ID with Public/Ingress access"
  type        = map(list(string))
  default     = {}
}

variable "private_subnet_id" {
  description = "Subnet ID with Private/Egress access"
  type        = map(list(string))
  default     = {}
}

variable "tags" {
  description = "QuickFabric Reosurce Tagging"
  type        = map
  default = {
    "app" : "quickfabric"
    "env" : "prd"
    "team" : "CloudOps"
  }

}

variable "key_pair" {
  description = "Valid AWS Key Pair"
  default     = ""
}

variable "instance_type" {
  description = "Bastion server instance type"
  default     = "t2.xlarge"
}

variable "public_key_path" {
  description = "My public ssh key"
  default     = "~/.ssh/id_rsa.pub"
}

variable "quickfabric_log_bucket" {
  description = "S3 bucket where quickfabric will write the logs."
  default     = "quickfabric-emr-data"
}

variable "www_domain_name" { default = "qf" }

variable "quickfabric_zone_name" {
  description = "Quickfabric Domain Name"
  type        = string
  default     = "quickfabric.intuit.com"
}

variable "hosted_zone_name_exists" {
  description = "Flag to determine whether the Zone needs to be created or it already exists."
  type        = bool
  default     = false
}

variable "cidr_admin_whitelist" {
  description = "CIDR ranges permitted to communicate with administrative endpoints"
  type        = list
  default     = []
}

variable "ami_ids" {
  description = "AMI id to use while spinning up the bastion servers. Default is Ubuntu base image"
  default     = {}
  type        = map
}
variable "docker_compose_version" {
  description = "Docker COmpose version"
  type        = string
  default     = "1.24.0"
}
variable "bastion_sg" {
  description = "Security group of the already existing bastion server."
  type = string
  default = ""
}
