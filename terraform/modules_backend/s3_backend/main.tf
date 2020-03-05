# Creates the config file in local
resource "local_file" "backend_tf_file" {
  content  = data.template_file.backend_tf[0].rendered
  filename = "./backend.tf"
}

# Identify the Caller account details
data "aws_caller_identity" "current" {}

# Create user data file using templates.
data "template_file" "backend_tf" {
  count    = fileexists("./modules/s3_backend/backend.tpl") ? 1 : 0
  template = file("./modules/s3_backend/backend.tpl")
  vars = {
    REGION      = var.region
    BUCKET_NAME = aws_s3_bucket.state_bucket.bucket
  }
}

# Creates S3 bucket for terrafrom state
resource "aws_s3_bucket" "state_bucket" {
  bucket = "${var.tf_s3_backend}-${data.aws_caller_identity.current.account_id}"
  acl    = "private"

  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        sse_algorithm = "AES256"
      }
    }
  }

  versioning {
    enabled = true
  }

  lifecycle {
    prevent_destroy = true
  }
}
