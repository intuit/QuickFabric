# EMR serverless automation framework
Steps to setup EMR serverless automation framework for a new AWS account ([what is serverless?](https://serverless.com))

## Pre-requisites needed for serverless framework
Use a __dockerized terraform__ image to create:
- Cross-account-assume-role-iam-policy for IBP
- Create DynamoDB for EMR registry
__Note__: EMR registry is common across accounts & needs be created only in master account, in our case 'SBSEG Analytics Production'.

1. Clone the repo to your local machine
a. __Note__: Git LFS setup to facilitate faster cloning & fetching of large files inside Git. 'Large files' here refer to the Lambda layer zip files.
b. How to set up git-lfs for a repo: [git-lfs](https://github.com/git-lfs/git-lfs))

2. Fetch AWS credentials using eiamCli and update your aws profile (Reference: [eiamCLI](https://github.intuit.com/EIAM/eiamCLI))
  ```
  ➤ $ eiamCli login
  Username:av1
  Password:
  Validating user login credentials...If credentials are valid, you should get a request for MFA automatically on your
  registered mobile device.
  Authentication successful. You can now use other commands 'getAWSTempCredentials' or 'getAWSTempSSHCert' without user
  credentials/MFA for next 10 hours.

  Note: 'getKeys.sh' script in the backend calls "eiamCli getAWSTempCredentials" per AWS account ID
  ➤ $ ~/getKeys.sh analytics
  Getting keys for the ANALYTICS PROD Account...
  Your AWS Temporary keys were successfully written in /Users/av1/.aws/credentials file.
  ```

3. Goto ```terraform``` directory  
  a. Pull terraform docker image from Intuit Jfrog artifactory
  ```
  docker pull docker.intuit.com/data/datacatalog/quickdataplatform/service/terraform:version1.0
  ```
  b. __Initialize__
  ```
  docker run --rm -it -v $(pwd):$(pwd) -v ${HOME}/.aws:/root/.aws -w $(pwd) docker.intuit.com/data/datacatalog/quickdataplatform/service/terraform:version1.0 init
  ```
  c. __Validate__ terraform files
  ```
  docker run --rm -it -v $(pwd):$(pwd) -v ${HOME}/.aws:/root/.aws -w $(pwd) docker.intuit.com/data/datacatalog/quickdataplatform/service/terraform:version1.0 validate
  ```
  d. __Generate plan__ which will allow you to preview changes before you actually apply the changes  
  i. __If you are running for the first time on a new AWS account__  
  ```
  docker run --rm -it -v $(pwd):$(pwd) -v ${HOME}/.aws:/root/.aws -w $(pwd) docker.intuit.com/data/datacatalo/quickdataplatform/service/terraform:version1.0 plan -var env=dev -var account_id=559166881778 -var aws_profile=sbg-data-processing-dev -state=statefiles/terraform.tfstate.dev.5591-6688-1778
  ```  
  ii. __If you are looking to update on an existing AWS account__ (target specific resource you want to update)
  ```
  docker run --rm -it -v $(pwd):$(pwd) -v ${HOME}/.aws:/root/.aws -w $(pwd) docker.intuit.com/data/datacatalog/quickdataplatform/service/terraform:version1.0 plan -target=aws_iam_policy.ibp-policy -var env=dev -var account_id=559166881778 -var aws_profile=sbg-data-processing-dev -var sts_externalId=XXXX -state=statefiles/terraform.tfstate.dev.5591-6688-1778
  
  ```  

4. __Apply__ changes  
  a. Generate sts externalId (Example: Use [UUID Generator](https://www.uuidgenerator.net/))  
    i. Generated externalId needs to be added to IBP credentials (https://build.intuit.com/quickdata/credentials) with ID as 'extId_$accountId' i.e. extId_559166881778 & corresponding secret value  
    ii. And then, reference it in Jenkinsfile. E.g. https://github.intuit.com/SBSEG-EMR/emr-setup/blob/master/jenkins/files/Jenkinsfile_sls_deploy#L37  
  b. __Note__: To create cert for API g/w via ACM, use Negenie automation: [AWS SSL Cert Manager](https://wiki.intuit.com/pages/viewpage.action?spaceKey=DCA&title=AWS+SSL+Cert+Manager)  
  c. Terraform tfstate file is written as ```terraform.tfstate.${env}.${aws_accountId}```. These state files are __encrypted__ using [git-crypt](https://github.com/AGWA/git-crypt) and stored on git to mask API g/w key & STS externalId. To decrypt locally for usage contact <abhishek_v@intuit.com> for crypt key and then, run in your $GIT_HOME:
  ```
  brew install git-crypt
  git-crypt unlock ~/crypt.key
  ```
  __Caveats__: Below error would be seen while running docker command if the above step if not performed:
  ```diff
  - Error: Error loading state: Decoding state file version failed: invalid character '\x00' looking for beginning of value
  ```
  If there are already local changes in your repo:
  ```diff
  [av1@networkreserve3: /export/home/av1/repos/emr-setup/terraform] [av1-master|✚ 1]
  ➤ $ git-crypt unlock ~/crypt.key
  - Error: Working directory not clean.
  - Please commit your changes or 'git stash' them before running 'git-crypt unlock'.
  ```
  To fix:
  ```
  ➤ $ git stash
  ➤ $ git-crypt unlock ~/crypt.key
  ➤ $ git stash pop
  ```
  - __For a new account__  
  ```
  docker run --rm -it -v $(pwd):$(pwd) -v ${HOME}/.aws:/root/.aws -w $(pwd) docker.intuit.com/data/datacatalog/quickdataplatform/service/terraform:version1.0 apply -var env=dev -var account_id=559166881778 -var aws_profile=sbg-data-processing-dev -var sts_externalId=XXXX -state=statefiles/terraform.tfstate.dev.5591-6688-1778
  ```  
  - __For an existing AWS account__  (target specific resource you want to update)
  ```
  docker run --rm -it -v $(pwd):$(pwd) -v ${HOME}/.aws:/root/.aws -w $(pwd) docker.intuit.com/data/datacatalo/quickdataplatform/service/terraform:version1.0 apply -target=aws_dynamodb_table.emr_metadata_table -var env=dev -var account_id=559166881778 -var aws_profile=sbg-data-processing-dev -state=statefiles/terraform.tfstate.dev.5591-6688-1778
  ```  

5. To enable/disable creation of certain resources in terraform, set variables to 0(__disable__) or 1(__enable__) during runs. E.g.  
  ```
  - var ibp_iam_role=1
  - var api_gw_iam_role=1
  - var ssm_param=0
  ```  

6. To destroy terraform resources (__EXERCISE WITH CAUTION__)
```
docker run --rm -it -v $(pwd):$(pwd) -v ${HOME}/.aws:/root/.aws -w $(pwd) docker.intuit.com/data/datacatalog/quickdataplatform/service/terraform:version1.0 destroy -var aws_profile=sbg-data-processing-dev -state=statefiles/terraform.tfstate.dev.5591-6688-1778
```  
__Note__: DynamoDB has lifecycle.prevent_destroy set to true. To avoid this error and continue with the plan, either disable lifecycle.prevent_destroy or adjust the scope of the plan using the -target flag for specific resources  

7. Trigger IBP job to package & deploy artifacts via serverless framework (triggered automatically whenever a change is detected in git or can be triggered manually via Jenkins)
  - https://build.intuit.com/quickdata/view/EMR-Package-Resources/job/emr-serverless-deploy

8. Push generated API key via serverless into SSM param store  
  __Note__: Applicable only for a new AWS account (__DO NOT run for an existing account__)  
  ```
  docker run --rm -it -v $(pwd):$(pwd) -v ${HOME}/.aws:/root/.aws -w $(pwd) docker.intuit.com/data/datacatalog/quickdataplatform/service/terraform:version1.0 apply -var env=dev -var aws_profile=sbg-data-processing-dev -var sts_externalId=XXXX -var emr_api_key=XXXX -var ssm_param=1 -state=statefiles/terraform.tfstate.dev.5591-6688-1778
  ```  

9. Additionally, the API url/token needs to be pushed to IDPS
a. Push key/value to your IDPS endpoint using [stash](https://wiki.intuit.com/display/IISKM/Stash)
__Note__: Key names should be: 'registry/api_url' and 'registry/api_token' (this gets referenced in IDPS util: https://github.intuit.com/SBSEG-EMR/emr-setup/blob/master/lambda_functions/registry/libs/idps.py)
b. Next, add your IDPS endpoint & policyId configs here: https://github.intuit.com/SBSEG-EMR/emr-setup/tree/master/lambda_functions/registry/templates
Example: https://github.intuit.com/SBSEG-EMR/emr-setup/blob/master/lambda_functions/registry/templates/559166881778_idps.conf
c. For reference, __IDPS Endpoints__ for QDP are:
- Prod: qdp-production-9vumsb.pd.idps.a.intuit.com  
- Pre-Prod: qdp-pre-production-bxgwox.pd.idps.a.intuit.com

10. API g/w resource policy needs to be whitelisted with IAM __RoleId__ associated with Lambda functions in account to allow the Lambda functions across accounts to access the APIs
a. How to fetch RoleId?
```
➤ $ aws iam get-role --role-name emr-launch-cluster-prod-us-west-2-lambdaRole --region us-west-2 --profile sbg-data-processing-prd
{
    "Role": {
        "AssumeRolePolicyDocument": {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Action": "sts:AssumeRole",
                    "Effect": "Allow",
                    "Principal": {
                        "Service": "lambda.amazonaws.com"
                    }
                }
            ]
        },
        "MaxSessionDuration": 3600,
        "RoleId": "AROA45OSJBU5JW6QT4FKM",
        "CreateDate": "2019-04-05T10:57:40Z",
        "RoleName": "emr-launch-cluster-prod-us-west-2-lambdaRole",
        "Path": "/",
        "Arn": "arn:aws:iam::887888612666:role/emr-launch-cluster-prod-us-west-2-lambdaRole"
    }
}
```
b. Where to add?
- https://github.intuit.com/SBSEG-EMR/emr-setup/blob/master/serverless/common/whitelist_ips.yml#L71

11. Allow lambda functions to access 'EMR' product under service catalog by adding the role created by serverless framework. Example: Add IAM role 'emr-launch-cluster-prod-us-west-2-lambdaRole' to 'EMR' product (under 'processing-sbgayt-prd' portfolio) in service catalog
  - __Note__: This step has to be executed manually via AWS console - Currently, it's not supported by serverless framework, terraform or AWS CLI for automation  

12. For pushing cloudwatch & API g/w logs into Splunk, work with OIL team to onboard the logs.
- Example: https://jira.intuit.com/browse/OILP-317
- Additionally, ensure OIL team provides a log subscription URL per account and add it here: https://github.intuit.com/SBSEG-EMR/emr-setup/blob/master/serverless/emr_serverless.yml#L50

13. Create EMR cluster
  - __SBSEG Analytics Production__ account: https://build.intuit.com/quickdata/view/EMR-Create-Cluster/job/exploratory-emr-serverless-create-cluster
  - __SBG Data Processing Platform__ account: https://build.intuit.com/quickdata/view/EMR-Create-Cluster/job/scheduled-emr-serverless-create-cluster-prd/ (Scheduled Kerbirized)

14. Create EMR gateway node
  - https://build.intuit.com/quickdata/view/EMR-Create-Cluster/job/emr-serverless-create-gateway  

15. EMR cluster operations
  - Terminate cluster: https://build.intuit.com/quickdata/view/EMR-Operations/job/exploratory-emr-cluster-ops
  - Add custom step: https://build.intuit.com/quickdata/view/EMR-Operations/job/exploratory-emr-cluster-custom-add-step

## Wiki
https://wiki.intuit.com/display/QDFT/EMR+Cluster+Creation+CI+Framework
