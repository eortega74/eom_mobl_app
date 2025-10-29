subnet_id_zone_a  = "subnet-08df65dc2b8e544ad" # us-gov-east-1a
subnet_id_zone_b  = "subnet-0fa880423a50b3df3" # us-gov-east-1b
default_security_groups = ["sg-0f5a80c3caf395464", "sg-06113992badca08a2", "sg-01c930f11370e2aca"] # these are automated by xetacloud and include tanium, rapid 7, etc... including them here prevents noise in the terraform plan
ec2_kms_key       = "a8d1467a-b5d6-4740-a286-ccd0eb6f435e"
ec2_instance_name = "x32q1iotrns0001"
iam_instance_profile = "wg1-x32q1-iotrns-us-gov-east-1-ec2-profile"
iam_instance_profile_arn  = "value"
aws_main_vpc      = "vpc-09a13abf7d206cf24"

ami           = "ami-020cfb0e2c39d2400"
region        = "us-gov-east-1"
instance_type = "t3.xlarge"

rds_database_subnet_group_name = "x32q1iotrns-default-rds-subnet-group"
rds_instance_name = "x32q1iotrns-rds-oracle"
rds_engine = "oracle-ee"
rds_engine_version = "19"
rds_instance_class = "db.m5.large"
rds_allocated_storage = 300
rds_storage_type = "gp3"
rds_license_model = "bring-your-own-license"
rds_username = "admin"
rds_final_snapshot_identifier = "x32q1iotrns-rds-final-snapshot"
rds_kms_key_id = "ab3ff236-6065-48e9-a872-dde5892c916c"
rds-option-group-name = "oracle-s3-integration-delete"

iam_policy_name_rds_s3_integration = "oracle-s3-read-write-iotbucket-destroy"
iam_role_name_rds_s3_integration = "rds-s3-integration-role-destroy"

s3_bucket_name_rds = "x32q1iotrns-db-upload-destroy"
s3_bucket_name_artifacts = "x32q1iotrns-artifacts-destroy"