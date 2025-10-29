data "aws_iam_policy_document" "instance_assume_role_policy" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["rds.amazonaws.com"]
    }
  }
}

resource "aws_iam_policy" "policy" {
  name        = var.iam_policy_name_rds_s3_integration 
  description = "allow rds read/write access to s3 buckets for db backup"

  policy = jsonencode({
    Version ="2012-10-17"
    Statement = [
        {
            Sid = "VisualEditor0",
            Effect = "Allow",
            Action = [
                "s3:*"
            ],
            Resource = [
                "arn:aws-us-gov:s3:::${var.s3_bucket_name_rds}/*",
                "arn:aws-us-gov:s3:::${var.s3_bucket_name_rds}"
            ]
        }
    ]
})
}

resource "aws_iam_role_policy_attachment" "attachment" {
  role       = aws_iam_role.rds_s3_integration.name
  policy_arn = aws_iam_policy.policy.arn
}

resource "aws_iam_role" "rds_s3_integration" {
  name = var.iam_role_name_rds_s3_integration
  assume_role_policy = data.aws_iam_policy_document.instance_assume_role_policy.json
}