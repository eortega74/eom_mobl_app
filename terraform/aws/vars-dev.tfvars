subnet_id_zone_a  = "subnet-03082f2fb355dead7" # us-gov-east-1a
subnet_id_zone_b  = "subnet-019b0d4a166611ac1" # us-gov-east-1b
default_security_groups = ["sg-07e012f0a511a87da", "sg-039f5d705fdac717d", "sg-00b754f826e39319b"] # these are automated by xetacloud and include tanium, rapid 7, etc... including them here prevents noise in the terraform plan
ec2_kms_key       = "35f8180f-8659-4a1d-bd78-9f6234cb0def"
ec2_instance_name = "x32d1iotrns0001"
iam_instance_profile = "wg1-x32d1-iotrns-us-gov-east-1-ec2-profile"
iam_instance_profile_arn = "arn:aws-us-gov:iam::398832017226:role/wg1-x32d1-iotrns-us-gov-east-1-ec2-role"
aws_main_vpc      = "vpc-08bb78537f88f2b62"

ami           = "ami-0e00596381b0fbce9"
region        = "us-gov-east-1"
instance_type = "t3.xlarge"

rds_database_subnet_group_name = "x32d1iotrns-default-rds-subnet-group"
rds_instance_name = "x32d1iotrns-rds-oracle"
rds_engine = "oracle-ee"
rds_engine_version = "19"
rds_instance_class = "db.m5.large"
rds_allocated_storage = 300
rds_storage_type = "gp3"
rds_license_model = "bring-your-own-license"
rds_username = "admin"
rds_final_snapshot_identifier = "x32d1iotrns-rds-final-snapshot"
rds_kms_key_id = "3761e0cb-6edf-4706-9c7b-de6b80bd666d"
rds-option-group-name = "oracle-s3-integration-delete"

iam_policy_name_rds_s3_integration = "oracle-s3-read-write-iotbucket-destroy"
iam_role_name_rds_s3_integration = "rds-s3-integration-role-destroy"

s3_bucket_name_rds = "x32d1iotrns-db-upload-destroy"
s3_bucket_name_artifacts = "x32d1iotrns-artifacts"