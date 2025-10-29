variable "ami" {
  type = string
}

variable "region" {
  type = string
}

variable "instance_type" {
  type = string
}

variable "aws_main_vpc" {
  type = string
}

variable "subnet_id_zone_a" {
  type = string
}

variable "subnet_id_zone_b" {
  type = string
}

variable "ec2_kms_key" {
  type = string
}

variable "ec2_instance_name" {
  type = string
}

variable "rds_database_subnet_group_name" {
    type = string
}

variable "rds_instance_name" {
    type = string
}

variable "rds_engine" {
    type = string
}

variable "rds_engine_version" {
    type = string
}

variable "rds_instance_class" {
    type = string
}

variable "rds_allocated_storage" {
    type = string
}

variable "rds_storage_type" {
    type = string
}

variable "rds_license_model" {
    type = string
}

variable "rds_username" {
    type = string
}

variable "RDS_PASSWORD" {
    type = string
    sensitive = true
}

variable "rds_final_snapshot_identifier" {
    type = string
}

variable "rds_kms_key_id" {
    type = string
}

variable "s3_bucket_name_rds" {
    type = string
}

variable "s3_bucket_name_artifacts" {
    type = string
}

variable "iam_policy_name_rds_s3_integration" {
    type = string
}

variable "iam_role_name_rds_s3_integration" {
    type = string
}

variable "default_security_groups" {
    type = list(string)
}

variable "iam_instance_profile" {
    type = string
}

variable "iam_instance_profile_arn" {
  type = string
}

variable "rds-option-group-name" {
    type = string
}