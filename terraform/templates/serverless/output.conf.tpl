{
 "vpc_id": "${vpc_id}",
 "public_subnet_ids": ${public_subnet_ids},
 "private_subnet_ids": ${private_subnet_ids},
 "emr_version": "${emr_version}",
 "keypair": "${key_pair}", 
 "artifacts_bucket": "s3://${bucket}",
 "emr_s3_log_path": "s3://${bucket}/${logs_path}",
 "region": "${region}",
 "r53_hosted_zone": "${r53_hosted_zone}",
 "bootstrap_actions": [
      {
        "bootstrapScript": "scripts/install-gradle-bootstrap.sh",
        "bootstrapName": "Install Gradle"
      }
  ],
  "steps": [
        {
          "Name": "InstallDrElephant",
          "OnBoot": true,
          "Type": "shell",
          "ActionOnFailure": "CONTINUE",
          "Script": "scripts/install-dr-elephant.sh",
          "Jar": "s3://elasticmapreduce/libs/script-runner/script-runner.jar",
          "Args": []
        }],
 "env" : "${env}"
}

