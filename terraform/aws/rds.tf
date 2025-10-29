resource "aws_db_subnet_group" "default_rds_subnet_group" {
  name        = var.rds_database_subnet_group_name
  subnet_ids  = [var.subnet_id_zone_a, var.subnet_id_zone_b]

  lifecycle {
    ignore_changes = [
      tags
    ]
  }
}

resource "aws_security_group" "allow_tcp_oracle" {
  name        = "allow_tcp_oracle"
  vpc_id      = var.aws_main_vpc

  lifecycle {
    ignore_changes = [
      tags
    ]
  }
}

resource "aws_vpc_security_group_ingress_rule" "allow_tcp_oracle_ipv4" {
  security_group_id = aws_security_group.allow_tcp_oracle.id
  cidr_ipv4         = "0.0.0.0/0"
  from_port         = 1521
  ip_protocol       = "tcp"
  to_port           = 1521
}


resource "aws_db_instance" "rds_oracle" {
  identifier                      = var.rds_instance_name
  engine                          = var.rds_engine
  engine_version                  = var.rds_engine_version
  instance_class                  = var.rds_instance_class
  allocated_storage               = var.rds_allocated_storage
  storage_type                    = var.rds_storage_type
  db_subnet_group_name            = aws_db_subnet_group.default_rds_subnet_group.id
  vpc_security_group_ids          = [aws_security_group.allow_tcp_oracle.id]
  storage_encrypted               = true
  kms_key_id                      = data.aws_kms_key.wg1_platform_rds_key.arn
  license_model                   = var.rds_license_model
  username                        = var.rds_username
  password                        = var.RDS_PASSWORD
  final_snapshot_identifier       = var.rds_final_snapshot_identifier
  enabled_cloudwatch_logs_exports = ["alert", "audit", "trace", "listener", "oemagent"]
  option_group_name               = aws_db_option_group.rds_option_group.name

  tags = {
    Name = var.rds_instance_name
  }

  lifecycle {
    ignore_changes = [
      tags
    ]
  }
}

data "aws_kms_key" "wg1_platform_rds_key" {
  key_id = var.rds_kms_key_id # Key Management Services (KMS) - wg1-platform-rds-key
}

resource "aws_db_option_group" "rds_option_group" {
  name                     = var.rds-option-group-name
  option_group_description = "Allow the oracle rds database to access S3 Buckets"
  engine_name              = var.rds_engine
  major_engine_version     = var.rds_engine_version
    
  option {
    option_name = "S3_INTEGRATION"
  }

}