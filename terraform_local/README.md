# Quickfabric Local Installation
---

# To start using Quickfabric Locally

Requirements
------------

- [Terraform](https://www.terraform.io/downloads.html) 0.12+
- [Docker](https://docs.docker.com/install/) 19.03+

# Configuring secrets.tfvars file
The `secrets.tfvars` file (terraform input variables) is the configuration file of costBuddy. It accepts the following parameters.

1. `MYSQL_PASSWORD`:  Provide a password to setup the admin user while installing MySql.

Example : 
>MYSQL_PASSWORD = "strongpassword"
            

2. `AES_SECRET_KEY` : Provide a password to setup the application encrytion.

Example : 
> AES_SECRET_KEY = “strongpassword”

NB: Add this file to gitignore to avoid pushing secrets to git repo.

# Deployment

## Parent Account Deployment: 

1. Clone the GitHub repo in your local computer if not done already.
   ```bash 
   git clone https://github.intuit.com/SBSEG-quickdata/QuickFabric.git 
   ```
   
2. Copy the example configuration file and modify the parameters. Refer [Configuration] (#Configuring Input.tfvars file) section above.
   ```bash
   cp costBuddy/terraform/secrets.tfvars.example costBuddy/terraform/secrets.tfvars
   ```
   
3. Initialize Terraform. It will initialize all terraform modules/plugins.
   go to `quickfabric/terraform_local` directory and run below command
   ```bash
   cd quickfabric/terraform_local
   terraform init
   ```

    ```bash 
    Initializing modules...

    Initializing the backend...

    Initializing provider plugins...

    The following providers do not have any version constraints in configuration,
    so the latest version was installed.

    To prevent automatic upgrades to new major versions that may contain breaking
    changes, it is recommended to add version = "..." constraints to the
    corresponding provider blocks in configuration, with the constraint strings
    suggested below.

    * provider.docker: version = "~> 2.6"
    * provider.null: version = "~> 2.1"

    Terraform has been successfully initialized!

    You may now begin working with Terraform. Try running "terraform plan" to see
    any changes that are required for your infrastructure. All Terraform commands
    should now work.

    If you ever set or change modules or backend configuration for Terraform,
    rerun this command to reinitialize your working directory. If you forget, other
    commands will detect it and remind you to do so if necessary.
    ```


6. Run planner command under `costBuddy/terraform` directory.

   ```bash
   python3 terraform_wrapper.py plan -var-file=input.tfvars
   ```

   ```bash
   This command will generate a preview of all the actions which terraform is going to execute.
      Expected Output: This command will be giving output something like below
            Plan: 36 to add, 0 to change, 0 to destroy.
            ------------------------------------------------------------------------
   ```

7. Run actual Apply command under `quickfabric/terraform_local` directory to deploy all the resources.

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
    Please type “yes” and enter
    It provides the next steps to perform

   ```bash
   Apply complete! Resources: 13 added, 0 changed, 0 destroyed.

   Outputs:

  quickfabric_url = http://127.0.0.1/

   ```

# Troubleshooting Guide

