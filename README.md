# QuickFabric
---                                                                       
[![TravisCI](https://travis-ci.com/intuit/QuickFabric.svg?token=3PcHaPf7DR43hcZdadiE&branch=master)]

![QuickFabric Architecture diagram](/Frontend/src/assets/help/EMR_Graph.png)  


![QuickFabric Architecture diagram](/Frontend/src/assets/help/ViewWorkflow.png)


## Objective:

A one-stop shop for all management and monitoring of Amazon Elastic Map Reduce 
(EMR) clusters across different AWS accounts and purposes.

## Benefits: 

1. EMR Orchestration across AWS accounts 
    * Cluster creation (provisioning) with bootstrap actions
    * Cluster termination
    * AMI rotation/restacking (one-click, or automatically on a schedule)
    * Add custom steps to execute on the cluster
    * DNS flip to move clusters in and out of production
    * Cluster Clone: Create a cluster using another as a starting point
    * Optionally receive email notifications about all cluster actions performed
2. EMR Observability
    * Cost history of individual clusters or grouped by cluster type, AWS account,
  and business segment
    * Running application details, including progress and resource utilization
    * Resource utilization history, including memory, CPU cores, and active nodes
    * Completed applications history, both succeeded and failed
3. Expert Advice
    * Job tuning advice produced using integration with Dr. Elephant tool from LinkedIn
    * Scheduling advice produced based on the current usage to improve load balancing
4. Access Control
    * Grant users privileges for only clusters belonging to the AWS account and business segments relevant to them
    * Manage privileges to perform only read access or only certain actions
5. JIRA/ServiceNow Integration
    * Require approval for all production changes to EMRs enforced by QuickFabric application
    * Document ticket information for production changes
6. Subscription-Based Email Reporting
    * View key metrics without having to navigate to QuickFabric application
    * Subscribe only to reports that contain information relevant to you

---
## QuickFabric Architecture diagram


![QuickFabric Architecture diagram](/Frontend/src/assets/help/architecture.png)

# To start using QuickFabric

## In Local Environment (UAT)

NOTE: Local setup is just to see the look and feel and to understand how the product works.
In order to have full functionality with orchestration and observability of real EMR clusters, see the section below for setup on AWS.

### Requirements

- [Terraform](https://www.terraform.io/downloads.html) 0.12+
- [Docker](https://www.docker.com/products/docker-desktop) 19.08+
- [Java](https://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html) 1.8+
- [mvn](https://maven.apache.org/download.cgi) 3.6+
- [npm](https://nodejs.org/en/download/) 6.11+


### Pre-requisites

1. Clone GitHub repo 
   ```
   git clone https://github.com/intuit/QuickFabric.git
   ```
   
### Deployment in Local

Quickfabric has a single phase for local. It deploys the React Frontend, Java Middleware, Mysql database in the local system.

#### Parent Account Deployment: 

1. Clone the GitHub repo in your local computer if not done already.
   ```bash 
   git clone https://github.com/intuit/QuickFabric.git 
   ```
   
2. `secrets.tfvars` is the input file where the Mysql and Encryption credentials are stored. This file is by default a part of `.gitignore` file.
     ```bash
   cp QuickFabric/terraform_local/secrets.tfvars.example QuickFabric/terraform_local/secrets.tfvars
   ```
   
3. Initialize Terraform. It will initialize all terraform modules/plugins.
   go to `QuickFabric/terraform_local/` directory and run below command
   ```bash
   cd QuickFabric/terraform_local/
   terraform init
   ```

   ```bash 
    Initializing modules...
        - db in modules/container
        - emr in modules/container
        - frontend in modules/container
        - frontend_container in modules/container
        - middleware in modules/container
        - scheduler in modules/container

        Initializing the backend...
        
        * provider.docker: version = "~> 2.7"
        * provider.null: version = "~> 2.1"

    Terraform has been successfully initialized!
    ```


4. Run planner command under `QuickFabric/terraform_local` directory.

   ```bash
   terraform plan  -var-file=secrets.tfvars
   ```

   ```bash
   This command will generate a preview of all the actions which terraform is going to execute.
      Expected Output: This command will be giving output something like below
            Plan: 14 to add, 0 to change, 0 to destroy.
            ------------------------------------------------------------------------
   ```
            
5. Run actual Apply command under `QuickFabric/terraform_local` directory to deploy all the resources into the AWS parent account. 
This step may take `5-10` mins.

   ```bash
   terraform apply -var-file=secrets.tfvars
   ```

    The output will look like below

   ```bash
    Expected output: It will ask for approval like below
        Do you want to perform these actions?
         Terraform will perform the actions described above.
        Only 'yes' will be accepted to approve.
        Enter a value:       
   ```
    Please type "yes" and enter
    It provides the next steps to perform

   ```bash
    Apply complete! Resources: 14 added, 0 changed, 0 destroyed.

    Outputs:

    quickfabric_url = http://127.0.0.1/
   ```
6. Wait for a few minutes before proceeding further for the application to come online. 

7. Verify the readiness of the system by accessing  UI: http://127.0.0.1/.

Quickfabric default Credentials: default credentials are  **"user@company.com/intuit"**

8. Once UAT deployment is complete, proceed with the child AWS account deployment mentioned below.

**_Note:_** If you want to build React Frontend or Java middleware individually without using terraform instructions for building are provided here:
[React Instructions](./Middleware/README.md)
[Java Instructions](./Frontend/README.md)

#### Caution : 
Quickfabic will save all the terraform state files inside `QuickFabic/terraform/terraform.tfstate.d/` directory. Make sure that you save all the terraform state files in a safe place (in git or S3 location) as it will be needed next time when you want to deploy/update QuickFabric again in some accounts.

 
### Getting Started

1. Login to the application with user `user@company.com` and password `intuit`.

2. Explore! Note that certain observability features (such as currently running
applications) and all cluster actions will not work properly as they depend upon serverless deployment in order to work.


## AWS (Production)

### Requirements

- [Terraform](https://www.terraform.io/downloads.html) 0.12+
- [Python](https://www.python.org/downloads) 3.7+
- [Docker](https://www.docker.com/products/docker-desktop) 19.08+

### Pre-requisites 

1. Clone GitHub repo 
   ``` 
   git clone https://github.com/intuit/QuickFabric.git
   ```
2. An AWS user with Administrator/Power user access.

   Refer the below AWS documentation to create a user and generate Access Keys.
   
   https://docs.aws.amazon.com/IAM/latest/UserGuide/id_users_create.html
   https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_access-keys.html
   
   
### AWS Account Deployment

QuickFabric has two phases of deployments. Parent account deployment which deploys the React Frontend, Java Middleware, Mysql database, necessary lambda applications and other related resources in parent AWS account and Child accounts deployments that deploys the lambda functions only. 

1. Clone the GitHub repo in your local computer if not done already.
   ```bash 
   git clone https://github.com/intuit/QuickFabric.git 
   ```
   
2. `input.tfvars` is the configuration file for the deployment. Use the example files to create an `input.tfvars` file.

    Copy the example configuration file and modify the parameters. Refer [Configuration] (#Configuring Input.tfvars file) section above.
   
   if the user opts to use a basic configuration file then run below command.
   
   ```bash
   cp QuickFabric/terraform/input.tfvars.basic.example QuickFabric/terraform/input.tfvars
   ```
   **or**
   
   if the user opts to use advance configuration file then run below command
   
   ```bash
   cp QuickFabric/terraform/input.tfvars.advanced.example QuickFabric/terraform/input.tfvars
   ```

3. `secrets.tfvars` is the input file where the Mysql and Encryption credentials are stored. This file is by default a part of `.gitignore` file.
     ```bash
   cp QuickFabric/terraform/secrets.tfvars.example QuickFabric/terraform/secrets.tfvars
   ```
   
4. Initialize Terraform. It will initialize all terraform modules/plugins.
   go to `QuickFabric/terraform/` directory and run below command
   ```bash
   cd QuickFabric/terraform/
   terraform init
   ```

   ``` 
   Expected Output: It will create .terraform directory in QuickFabric/terraform/  location and command output should look like below
		Initializing modules...
        - bastion in modules/ec2
        - bastion_sg in modules/sg
        - igw in modules/igw
        - nat_gw in modules/nat
        - private_subnet in modules/subnet
        - public_subnet in modules/subnet
        - qf in modules/ec2
        - qf_s3_uploads in modules/s3_uploads
        - r53 in modules/r53
        - s3 in modules/s3
        - sg in modules/sg
        - vpc in modules/vpc

        Initializing the backend...

        * provider.archive: version = "~> 1.3"
        * provider.aws: version = "~> 2.51"
        * provider.external: version = "~> 1.2"
        * provider.local: version = "~> 1.4"
        * provider.null: version = "~> 2.1"
        * provider.template: version = "~> 2.1"

        Terraform has been successfully initialized!
    ```

5. Update the root/power user access credentials.

    Store the AWS Access and Secret Key in the Credentials file (~/.aws/credentials) and export the profile.
    ```bash
        [quickfabric_deploy]
        aws_access_key_id= awsaccesskey
        aws_secret_access_key= awssecretkey
    ```
    ```bash
        export AWS_PROFILE="quickfabric_sample_profile" 
    ```
    **_Note:_** Please replace the above sample profile with your own before running the export command.

    **Or**  
    
    export the keys as environment variables.
    ```bash
        export AWS_ACCESS_KEY_ID="awsaccesskey"
        export AWS_SECRET_ACCESS_KEY="awssecretkey"
    ```
    **Depending up on the accounts that the changes are intented for, one should chnage the AWS credentials acccordingly. Terrafrom will make the changes only to the AWS account for which the credentials currently loaded**
    
    Refer to the below AWS documentation to create user credentials.
    * [Creating an IAM User in Your AWS Account](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_users_create.html)
    * [Managing Access Keys for IAM Users](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_access-keys.html)
    
6. Run planner command under `QuickFabric/terraform` directory.

   ```bash
   python3 terraform_wrapper.py plan -var-file=input.tfvars -var-file=secrets.tfvars
   ```

   ```
   This command will generate a preview of all the actions which terraform is going to execute.
      Expected Output: This command will be giving output something like below
            Plan: 46 to add, 0 to change, 0 to destroy.
            ------------------------------------------------------------------------
   ```
            
7. Run actual Apply command under `QuickFabric/terraform` directory to deploy all the resources into the AWS parent account. 
This step may take `5-10` mins.

   ```bash
   python3 terraform_wrapper.py apply -var-file=input.tfvars -var-file=secrets.tfvars
   ```

    The output will look like below

   ```
    Expected output: It will ask for approval like below
        Do you want to perform these actions?
         Terraform will perform the actions described above.
        Only 'yes' will be accepted to approve.
        Enter a value:       
   ```
    Please type "yes" and enter
    It provides the next steps to perform

   ```
   Apply complete! Resources: 46 added(in case of basic configuration) 45 added(in case of advanced configuration), 0 changed, 0 destroyed.

   Outputs:

   Apply complete! Resources: 46 added, 0 changed, 0 destroyed.

    Outputs:

    bastion_ip = xx.xx.xx.xx
    quickfabric_url = http://xx.xx.xx.xx
    ```
       
8. Wait for a few minutes before proceeding further for the application to come online. 

9. Verify the readiness of the system by accessing  UI: http://<quickfabric_url>/.

Quickfabric default Credentials: default credentials are  **"user@company.com/intuit"**

# Configuring Input.tfvars file

QuickFabric comes with 2 flavors of input configuration file. User can choose one of the below configurations at a time to setup

## Flavor 1. Basic configuration  "input.tfvars.basic.example" 
The `input.tfvars.basic.example` file (terraform input variables) is the configuration file of QuickFabric deployment.It accepts the following parameters.

1. `account_ids`:  Provide one parent AWS account ID and zero or more comma-separated child accounts IDs. In the parent account both UI (EC2 instance, R53 entry) and the backend services will be created. In the child accounts only the backend services (Lambda, bastion, VPC, subnets and security groups) will be created.

Example : 
>   
     1. if you don't have any child accounts yet then use below example with child accounts array as empty.
           account_ids = {
                "parent_account_id" : "1234xxxxxxx",
                "child_account_ids" : []
               
     2. if you have child accounts info then use example 
           account_ids = {
                "parent_account_id" : "1234xxxxxxx",
                "child_account_ids" : ["4567xxxxxxx", "8901xxxxxxx" , "4583xxxxxxx"]
            } 
     3. If you are using local system for Frontend installation, leave the parent account id as empty string.
           account_ids = {
                "parent_account_id" : "",
                "child_account_ids" : ["4567xxxxxxx", "8901xxxxxxx" , "4583xxxxxxx"]
            }      
            
            
2. `region` : AWS Region to deploy QuickFabric

 Example : 
 >  
      region = "us-west-2"


**Note : In basic configuration Quickfabric will create other required resources like VPC , private/public subnets , S3 bucket, bastion host, security groups etc automatically.**


## Flavor 2. Advance configuration  "input.tfvars.advanced.example" 

The `input.tfvars` file (terraform input variables) is the configuration file of Quickfabric deployment. It accepts the following parameters.

1. `account_ids`:  Provide one parent AWS account ID and zero or more comma-separated child accounts IDs. In the parent account both UI (EC2 instance, R53 entry) and the backend services will be created. In the child accounts only the backend services (Lambda, bastion, VPC, subnets and security groups) will be created.

Example : 
>   
       1. if you don't have any child accounts yet then use below example with child accounts array as empty.
           account_ids = {
                "parent_account_id" : "1234xxxxxxx",
                "child_account_ids" : []
             }  
       2. if you have child accounts info then use example 
           account_ids = {
                "parent_account_id" : "1234xxxxxxx",
                "child_account_ids" : ["4567xxxxxxx", "8901xxxxxxx" , "4583xxxxxxx"]
            }  
        3. If you are using local system for Frontend installation, leave the parent account id as empty string.
           account_ids = {
                "parent_account_id" : "",
                "child_account_ids" : ["4567xxxxxxx", "8901xxxxxxx" , "4583xxxxxxx"]
            }  
       
Note: 12 digit AWS Account number without '-'(hyphen).

Parent account definition : 
> Parent AWS account is the main account where QuickFabric Frontend resources will be deployed. This account will have the following resources post QuickFbaric setup completion.
> Lambda, EC2 instance ( It will have docker containers with Database and supporting applications), Security groups, Bastion host, Output S3 bucket and few IAM roles.

Child accounts definition: 
> Zero or more other AWS accounts where the user wants launch and manage EMR clusters through Quickfabric. These child accounts will have Lambda, Security groups, Bastion host, Output S3 bucket and few IAM roles. . Leave it as an empty list([]) if there are no child accounts.

2. `key_pair` : **< optional >** if empty then QuickFabric will pickup user’s default id_rsa , otherwise provide AWS key_pair file name without .pem extension.

    Refer the following AWS documentation to create a new Keypair.
    https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-key-pairs.html

Example : 
> key_pair = "" (in case user wants to use his/her default id_rsa.pub key)

or
> key_pair = abc (user should have this pem file to login to EC2 instance for troubleshooting purpose)

3. `region`: AWS region where QuickFabric will be deployed.

Example : 
>region = "us-west-2"

4. `bastion_security_group`: **< optional >** If a bastion host already exists, provide the security group ID to be whitelisted in the EC2 instance. If provided, deployment will skip creating new bastion host. Leave it as empty string to create a new bastion host.

Example:
>bastion_security_group = "sg-abc"
5. `cidr_admin_whitelist`:  Accepts lists of CIDR in order to access QuickFabric UI and servers. This will have your public IP address or your organization’s Public IP address ranges.

Use the following URL to get the public IP address of a system.
   ```bash 
   curl http://checkip.amazonaws.com
   ``` 

Access to QuickFabric application will be restricted and only these IP ranges will be whitelisted.

Example :
>cidr_admin_whitelist = [
                        "x.x.x.x/32",
                        "x.x.x.x/32"
                    ]


6. `quickfabric_zone_name` :  Provide route53 valid existing zone. This zone is required to access QuickFbaric UI using a domain name. Incase of new hosted zone to be created, set `hosted_zone_name_exists` to `false`.

Example : 
> quickfabric_zone_name="quickfabric.intuit.com"


7. `hosted_zone_name_exists` : **(Default is false)** Does not create a new hosted zone when set to `true`, Incase of new hosted zone to be created, set to `false`.

Example : 
> hosted_zone_name_exists=false


8. `www_domain_name` : Provide appropriate name to create "A" record for QuickFabric UI.

Example :
> www_domain_name="qf"
QuickFabric UI will be accessible via this url `http://<www_domain_name>.<quickfabric_zone_name>`  = `http://qf.quickfabric.intuit.com` (DNS will not work until your Route53 hosted zone is resolvable by public DNS.)

9. `public_subnet_id`: Required if Private subnet is provided. Otherwise, QuickFabric deployment will create a VPC and Public/Private subnet. EC2 instance will be provisioned under this public subnet so that it can be accessible through Internet. Provide one subnet id in a map of list.

Example :
> public_subnet_id={"aws_account_id_1" : ["subnet-abc"],
                    "aws_account_id_2" : ["subnet-cef"],
                    "aws_account_id_3" : []              # Deployment will create VPC, Private/Public subnet                                       for this account
}

If left empty, QuickFabric deployment will create a VPC and Public/Private subnet.

10. `private_subnet_id`: Required if Public subnet is already provided. Otherwise, QuickFabric deployment will create a VPC and Public/Private subnet. EMR's will be deployed under private subnet. Provide one or more subnet id in a map of list.

    Refer the below AWS document for more info.
> https://aws.amazon.com/premiumsupport/knowledge-center/internet-access-lambda-function/

Example :
>> private_subnet_id={"aws_account_id_1" : ["subnet-abc"],
                    "aws_account_id_2" : ["subnet-cef"],
                    "aws_account_id_3" : []              # Deployment will create VPC, Private/Public subnet                                       for this account
}

If left empty, QuickFabric deployment will create a VPC and Public/Private subnet.

11. `tags`: **< optional >** Parameter to add the tag into all the QuickFabric resources to keep track.

Example : 
>tags = {
                "app" : "quickfabric"
                "env" : "prd"
                "team" : "CloudOps"
                "costCenter" : "CloudEngg"
    }


#### Caution : 
Quickfabic will save all the terraform state files inside `QuickFabic/terraform/terraform.tfstate.d/` directory. Make sure that you save all the terraform state files in a safe place (in git or S3 location) as it will be needed next time when you want to deploy/update QuickFabric again in some accounts.

### Using your own RDS

By default, QuickFabric deployment with terraform will create a new docker container that has a new RDS instance. If you already have an existing running RDS instance that you want to use instead of a new one created by terraform during the standard deployment, perform the following steps.

1. Rename `docker-compose-rdsversion.yml` to `docker-compose.yml`. Save the 
existing `docker-compose.yml` under a different name if you intend to do a 
traditional deployment elsewhere later on.

  ```bash
  mv docker-compose.yml docker-compose-standard.yml #optional
  mv docker-compose-rdsversion.yml docker-compose.yml
  ```
  
2. Set the Spring Datasource URL attribute in `application.properties` to the
proper URL and port for your RDS instance. Do this both the [EMR properties file](./Middleware/emr/src/main/resources/application.properties) and the [schedulers properties file](./Middleware/schedulers/src/main/resources/application.properties).
applications. Optionally update the serverTimezone query parameter to your
preferred time zone. The files can be found in

```
spring.datasource.url=jdbc:mysql://<rds_instance_url>:<rds_instance_port>/quickfabric?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=America/Los_Angeles&useLegacyDatetimeCode=false
```

3. Execute `quickfabric_bootstrap.sql` script against your RDS instance. This 
script will create the QuickFabric schema with all tables and populate them
with the necessary starter data.

4. Follow the [standard deployment process](#aws-account-deployment). 
 

## Congratulations as deployment is completed, now we just need to onboard newly setup AWS account into QuickFabric UI by following the below steps...

### Getting Started

1. Login to the application with user `user@company.com` and password `intuit`.
2. Many of QuickFabric's core features include emails sent from the application. The email address from which these emails are sent needs
to be verified with Amazon Simple Email Service. Config `from_email_address` is used to store this verified email address. Follow the following
documentation from AWS for instructions on how to do this:
    * [Verifying an Email Address (Procedure)](https://docs.aws.amazon.com/ses/latest/DeveloperGuide/verify-email-addresses-procedure.html)
3. Configure Application under Administration->Config Management->Application Configs
    * Schedulers
        * `from_email_address` specifies the email address from whom users will receive emails
        via QuicFabric
        * `daily_report_scheduler`, `weekly_report_scheduler`, `monthly_report_scheduler`
        control whether a report containing details about all active 
        clusters will be sent to the comma-separated addresses specified in
        `report_recipients`.
        * `subscription_reports_scheduler` controls whether users who have subscribed
        to reports about EMR clusters relevant to them will receive those reports.
        * `segment_reports_scheduler` controls whether the business owner of each 
        segment receives a daily report about each of the clusters in that segment.
        * `rds_cleanup_scheduler` controls whether information (metrics, metadata, 
          cost, etc.) about clusters terminated over 6 months ago should be retained in the database.
    * Notifications: `create_cluster_notifications`, `rotate_ami_notifications`, `terminate_cluster_notifications`,`dns_flip_notifications` control whether email notifications will be sent to the comma-separated addresses specified in `notification_recipients` for each of the corresponding actions.

    **_Note:_** We have left a pluggable code available for SSO implementation as each company may have its own SSO implementation. Please complete SSO implementation in doLoginSSORedirect and getLoginRoleSSO method [EMR login Service](./Middleware/commons/src/main/java/com/intuit/quickfabric/commons/service/LoginService.java).

4. Onboard an account
    1. Navigate to Administration->Account Onboarding and begin filling in details of the AWS account you want to add to QuickFabric.
    2. Add business segments (e.g. marketing) that fall under the account. Note that existing segments with the same name can be used and shared with other accounts, so if others already exist that you need, select them from the dropdown.
    3. Select which test suites you want to run on your EMR clusters upon creation.
    Currently, QuickFabric supports tests for Autoscaling and number of Bootstrap actions.
    4. Provide critical account configurations. The account onboarding wizard
    includes the following:
        * `gateway_api_url`: This is the URL
        for the serverless backend of QuickFabric where all cluster action HTTP requests 
        are sent to for a particular account. It can be retrieved by going at 
        `AWS Console-> API Gateway->dev-emr-cluster-ops->Dashboard`. Sample API Url:
        ```
        https://abc.execute-api.us-xxxx-x.amazonaws.com/prod/
        ```
        * `gateway_api_key`: Generated when you set up serverless backend. This is 
        sent along with each request to the serverless backend of quickfabric 
        as a security measure. It can be retrieved at 
        `AWS Console-> API Gateway->dev-emr-cluster-ops->API Keys->api_gw_key`. Sample API Key:
        ```
        Ne9y8iZYJzkryqliS720Ne9y8iZYJzkryqliS720
        ```
        * `jira_enabled_account` and `servicenow_enabled_account` control whether
        this account requires a valid request ticket for QuickFabric to accept a
        cluster action performed for this account. Select _at most_ one of these two. Note that if you select one of these options, more configuration information will be required for this functionality to work (see below).
            * `jira_url` and `snow_url` contains the full URL with path for the API call
            to be made for validation. Examples are 
            `https://jira.yourcompanydomain.com` and 
            `https://yourcompanysubdomain.service-now.com`
            * `jira_user` and `snow_user` are the usernames which will be making the API 
            call for ticket validation.
            * `jira_password` and `snow_password` are the passwords for Basic Authentication 
            to use with the relevant API. The password will be encrypted.
            * `jira_projects` contains a list of comma-separated JIRA projects that
            the ticket may be part of to be validated
    5. Create a New User which will have access to perform the selected actions into that account. Once created, you will need to reset the password of this newly created user under `Administration->Config Management->User Management-> Reset Password`. 
    **_Note_:** You can still use the default user `user@company.com` for performing actions on this account if you skip this step as it is a superadmin. 
    6. Configure Account under `Administration->Config Management->Account Configs`
        * Test cluster auto-termination 
            * `test_cluster_auto_termination` controls whether test clusters 
            (clusters containing "test" in the name) should be auto-terminated.
            * `test_cluster_ttl` specifies the amount of time in hours test clusters
            should persist before being terminated if auto-termination is enabled.
        * `notification_recipients` controls to whom notifications about cluster 
            actions for the cluster on this account will be sent.

## More questions about how to use QuickFabric?

We have a pretty handy help page. Navigate to `/help` on your running QuickFabric
application to find more details on what QuickFabric is and how to start using it.

## Cleanup QuickFabric resources: 

1. Update root/power user access credentials.

    Store the AWS Access and Secret Key in the Credentials file (~/.aws/credentials) and export the profile.
    ```bash
        [quickfabric_deploy]
        aws_access_key_id= awsaccesskey
        aws_secret_access_key= awssecretkey
    ```
    ```bash
        export AWS_PROFILE="quickfabric_sample_profile" 
    ```
    **_Note:_** Please replace the above sample profile with your own before running the export command.

    **Or** export the keys as environment variables.
    ```bash
        export AWS_ACCESS_KEY_ID="awsaccesskey"
        export AWS_SECRET_ACCESS_KEY="awssecretkey"
    ```
    
    Refer the below AWS documentation to create user credentials.
    https://docs.aws.amazon.com/IAM/latest/UserGuide/id_users_create.html
    https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_access-keys.html
    

2. Run below command for destroying all the resources.
go to `QuickFabric/terraform` directory and execute the below command.

   ```bash
   cd QuickFabric/terraform/
   python3 terraform_wrapper.py destroy -var-file=input.tfvars -var-file=secrets.tfvars
   ```
  The output will look like below

  ```bash
      Plan: 0 to add, 0 to change, 46 to destroy.

      Do you really want to destroy all resources in workspace "5xxxxxxxx9"?
          Terraform will destroy all your managed infrastructure, as shown above.
          There is no undo. Only 'yes' will be accepted to confirm.

          Enter a value:
  ```

  Type "yes" and enter to proceed.

     ```bash
     destroy complete! Resources: 0 added, 0 changed, 46 destroyed
     ```

  **_Note:_**   

         1) QuickFabric takes around ~30+ mins to destroy all the resources in the parent account 
         2) QuickFabric takes around ~20+ mins to destroy all the resources in each child account 
         3) if destroy options fails because of a timeout , then please rerun destroy command again.
          
         Go through below link to get more info about AWS resource destroy process/duration etc
         https://aws.amazon.com/blogs/compute/update-issue-affecting-hashicorp-terraform-resource-deletions-after-the-vpc-improvements-to-aws-lambda/
           

## Troubleshooting Tips
* Unable to access `quickfabric_url` after terraform setup is done.
  * You need to wait few minutes as docker is running in the background to finish setting up the containers
* Project is not up, is my docker setup complete?
  * You can check docker setup logs here to see if the setup is still going on or if there are any errors during the setup
  ```
  /var/log/cloud-init-output.log
  ```



**_Limitation:_**
* Quickfabric supports one region per account (Multiple region support is coming soon...).
