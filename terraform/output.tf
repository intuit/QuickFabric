output "quickfabric_url" {
  value = "\n\tQuickFabric will be accessible through the following URL. Please wait for few minutes for the docker containers to come up.\n\n http://${module.qf.server_ip}"
}

output "aws_account_id" {
  value = data.aws_caller_identity.current.account_id
}

output "bastion_ip" {
  value = module.bastion.server_ip
}

output "api_gw_credentials" {
   value = local.api_creds
}
