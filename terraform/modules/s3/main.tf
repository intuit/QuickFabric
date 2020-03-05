locals {
  common_tags = {
    Name = "quickfabric-s3-bucket"
  }
}

# Creates S3 bucket for quickfabric logs
resource "aws_s3_bucket" "out_bucket" {

  bucket        = var.bucket_name
  acl           = "private"
  force_destroy = true
  tags          = merge(local.common_tags, var.tags)

}

# Creates a logs directory under the S3 bucket
resource "aws_s3_bucket_object" "directories" {

  count = length(var.directories)

  bucket     = aws_s3_bucket.out_bucket.id
  key        = element(var.directories, count.index)
  depends_on = [aws_s3_bucket.out_bucket]
}


