variable "container_name" {}
variable "data_store" {}
variable "command" { default = [] }
variable "working_dir" { default = "/" }

variable "image" {}
variable "attach" { default = false }
variable "rm" { default = false }
variable "must_run" { default = false }
variable "mounts" { default = {} }

variable "env" { default = [] }
variable "network" { default = {} }
variable "ports" { default = {} }
