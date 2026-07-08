# EMR serverless automation framework
Steps to setup EMR serverless automation framework for a new AWS account ([what is serverless?](https://serverless.com))

## Pre-requisites needed for serverless framework
Use a __dockerized terraform__ image to create:
- Cross-account-assume-role-iam-policy for IBP
- Create DynamoDB for EMR registry
__Note__: EMR registry is common across accounts & needs be created only in master account.

1. Clone the repo to your local machine
a. __Note__: Git LFS setup to facilitate faster cloning & fetching of large files inside Git. 'Large files' here refer to the Lambda layer zip files.
b. How to set up git-lfs for a repo: [git-lfs](https://github.com/git-lfs/git-lfs))

2. Configure AWS credentials for your target account. Use your organization's standard method for obtaining temporary credentials (e.g. `aws configure`, `aws sso login`, or your internal credential helper).
  ```
  $ aws configure --profile <your-profile>
  AWS Access Key ID: <your-access-key>
  AWS Secret Access Key: <your-secret-key>
  Default region name: us-west-2
  Default output format: json
  ```

  Note: The `getKeys.sh` script in the backend can be adapted to fetch temporary credentials per AWS account ID using your organization's credential tooling.

3. Goto ```terraform``` directory  
  a. Pull terraform docker image from your container registry
  ```
  docker pull <your-registry>/terraform:version1.0
  ```
  b. __Initialize__
  ```
  docker run --rm -it -v $(pwd):$(pwd) -v ${HOME}/.aws:/root/.aws -w $(pwd) <your-registry>/terraform:version1.0 init
  ```
  c. __Validate__ terraform files
  ```
  docker run --rm -it -v $(pwd):$(pwd) -v ${HOME}/.aws:/root/.aws -w $(pwd) <your-registry>/terraform:version1.0 validate
  ```
  d. __Generate plan__ which will allow you to preview changes before you actually apply the changes  
  i. __If you are running for the first time on a new AWS account__  
  ```
  docker run --rm -it -v $(pwd):$(pwd) -v ${HOME}/.aws:/root/.aws -w $(pwd) <your-registry>/terraform:version1.0 plan -var env=dev -var account_id=<ACCOUNT_ID> -var aws_profile=<AWS_PROFILE> -state=statefiles/terraform.tfstate.dev.<ACCOUNT_ID>
  ```  
  ii. __If you are looking to update on an existing AWS account__ (target specific resource you want to update)
  ```
  docker run --rm -it -v $(pwd):$(pwd) -v ${HOME}/.aws:/root/.aws -w $(pwd) <your-registry>/terraform:version1.0 plan -target=aws_iam_policy.ibp-policy -var env=dev -var account_id=<ACCOUNT_ID> -var aws_profile=<AWS_PROFILE> -var sts_externalId=XXXX -state=statefiles/terraform.tfstate.dev.<ACCOUNT_ID>
  
  ```  

4. __Apply__ changes  
  a. Generate sts externalId (Example: Use [UUID Generator](https://www.uuidgenerator.net/))  
    i. Generated externalId needs to be added to your credentials store with ID as 'extId_$accountId' i.e. extId_<ACCOUNT_ID> & corresponding secret value  
    ii. And then, reference it in your CI/CD pipeline configuration  
  b. __Note__: To create cert for API g/w via ACM, use your organization's SSL certificate automation tooling  
  c. Terraform tfstate file is written as ```terraform.tfstate.${env}.${aws_accountId}```. These state files are __encrypted__ using [git-crypt](https://github.com/AGWA/git-crypt) and stored on git to mask API g/w key & STS externalId. To decrypt locally for usage, obtain the git-crypt key from your team lead and then run in your $GIT_HOME:
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
  docker run --rm -it -v $(pwd):$(pwd) -v ${HOME}/.aws:/root/.aws -w $(pwd) <your-registry>/terraform:version1.0 apply -var env=dev -var account_id=<ACCOUNT_ID> -var aws_profile=<AWS_PROFILE> -var sts_externalId=XXXX -state=statefiles/terraform.tfstate.dev.<ACCOUNT_ID>
  ```  
  - __For an existing AWS account__  (target specific resource you want to update)
  ```
  docker run --rm -it -v $(pwd):$(pwd) -v ${HOME}/.aws:/root/.aws -w $(pwd) <your-registry>/terraform:version1.0 apply -target=aws_dynamodb_table.emr_metadata_table -var env=dev -var account_id=<ACCOUNT_ID> -var aws_profile=<AWS_PROFILE> -state=statefiles/terraform.tfstate.dev.<ACCOUNT_ID>
  ```  

5. To enable/disable creation of certain resources in terraform, set variables to 0(__disable__) or 1(__enable__) during runs. E.g.  
  ```
  - var ibp_iam_role=1
  - var api_gw_iam_role=1
  - var ssm_param=0
  ```  

6. To destroy terraform resources (__EXERCISE WITH CAUTION__)
```
docker run --rm -it -v $(pwd):$(pwd) -v ${HOME}/.aws:/root/.aws -w $(pwd) <your-registry>/terraform:version1.0 destroy -var aws_profile=<AWS_PROFILE> -state=statefiles/terraform.tfstate.dev.<ACCOUNT_ID>
```  
__Note__: DynamoDB has lifecycle.prevent_destroy set to true. To avoid this error and continue with the plan, either disable lifecycle.prevent_destroy or adjust the scope of the plan using the -target flag for specific resources  

7. Trigger your CI/CD pipeline to package & deploy artifacts via serverless framework (triggered automatically whenever a change is detected in git or can be triggered manually)

8. Push generated API key via serverless into SSM param store  
  __Note__: Applicable only for a new AWS account (__DO NOT run for an existing account__)  
  ```
  docker run --rm -it -v $(pwd):$(pwd) -v ${HOME}/.aws:/root/.aws -w $(pwd) <your-registry>/terraform:version1.0 apply -var env=dev -var aws_profile=<AWS_PROFILE> -var sts_externalId=XXXX -var emr_api_key=XXXX -var ssm_param=1 -state=statefiles/terraform.tfstate.dev.<ACCOUNT_ID>
  ```  

9. Additionally, the API url/token needs to be pushed to your secrets management service
a. Push key/value to your secrets endpoint using your organization's secret management tooling
__Note__: Key names should be: 'registry/api_url' and 'registry/api_token'
b. Next, add your secrets endpoint & policyId configs in your registry templates directory
Example: `<ACCOUNT_ID>_idps.conf`
c. Configure the appropriate __secrets endpoints__ for your environment (prod and pre-prod)

10. API g/w resource policy needs to be whitelisted with IAM __RoleId__ associated with Lambda functions in account to allow the Lambda functions across accounts to access the APIs
a. How to fetch RoleId?
```
$ aws iam get-role --role-name <LAMBDA_ROLE_NAME> --region us-west-2 --profile <AWS_PROFILE>
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
        "RoleId": "<ROLE_ID>",
        "CreateDate": "2019-04-05T10:57:40Z",
        "RoleName": "<LAMBDA_ROLE_NAME>",
        "Path": "/",
        "Arn": "arn:aws:iam::<ACCOUNT_ID>:role/<LAMBDA_ROLE_NAME>"
    }
}
```
b. Add the RoleId to your API gateway whitelist configuration

11. Allow lambda functions to access 'EMR' product under service catalog by adding the role created by serverless framework. Example: Add IAM role '<LAMBDA_ROLE_NAME>' to 'EMR' product in service catalog
  - __Note__: This step has to be executed manually via AWS console - Currently, it's not supported by serverless framework, terraform or AWS CLI for automation  

12. For pushing cloudwatch & API g/w logs into your log aggregation platform, work with your observability team to onboard the logs.
- Ensure your observability team provides a log subscription URL per account and add it to your serverless configuration

13. Create EMR cluster via your CI/CD pipeline

14. Create EMR gateway node via your CI/CD pipeline

15. EMR cluster operations
  - Terminate cluster via your CI/CD pipeline
  - Add custom step via your CI/CD pipeline
