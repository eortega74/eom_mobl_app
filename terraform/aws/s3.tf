resource "aws_s3_bucket" "rds_bucket" {
  bucket = var.s3_bucket_name_rds

  lifecycle {
    ignore_changes = [
      tags
    ]
  }
}

// This allows the ec2 to read from the bucket to download the website artifacts
resource "aws_s3_bucket_policy" "policy_allow_access_from_ec2" {
  bucket = aws_s3_bucket.app_artifacts_bucket.id
  policy = data.aws_iam_policy_document.policy_doc_allow_access_from_ec2.json
}

data "aws_iam_policy_document" "policy_doc_allow_access_from_ec2" {
  statement {
    principals {
      type        = "AWS"
      identifiers = [var.iam_instance_profile_arn]
    }

    actions = [
      "s3:GetObject",
      "s3:ListBucket",
    ]

    resources = [
      aws_s3_bucket.app_artifacts_bucket.arn,
      "${aws_s3_bucket.app_artifacts_bucket.arn}/*",
    ]
  }
}


resource "aws_s3_bucket" "app_artifacts_bucket" {
  bucket = var.s3_bucket_name_artifacts

  lifecycle {
    ignore_changes = [
      tags
    ]
  }
}