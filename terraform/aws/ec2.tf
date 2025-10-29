data "aws_vpc" "default_vpc" {
  id = var.aws_main_vpc
}

data "aws_subnet" "subnet_id_zone_a" {
  id = var.subnet_id_zone_a
}

data "aws_kms_key" "ec2_kms_key" {
  key_id = var.ec2_kms_key
}

resource "aws_security_group" "https_sg" {
  name        = "${var.ec2_instance_name}-https-sg"
  description = "Security Group for HTTPS inbound"
  vpc_id      = data.aws_vpc.default_vpc.id

  tags = {
    Name = "${var.ec2_instance_name}-https-sg"
  }

  lifecycle {
    ignore_changes = [
      tags
    ]
  }
}

resource "aws_vpc_security_group_ingress_rule" "allow_tcp_https_ipv4" {
  security_group_id = aws_security_group.https_sg.id
  cidr_ipv4         = "0.0.0.0/0"
  from_port         = 443
  ip_protocol       = "tcp"
  to_port           = 443
}

resource "aws_instance" "vm" {
  ami                    = var.ami
  instance_type          = var.instance_type
  subnet_id              = data.aws_subnet.subnet_id_zone_a.id
  iam_instance_profile   = var.iam_instance_profile
  vpc_security_group_ids = concat(var.default_security_groups, [aws_security_group.https_sg.id])

  user_data = templatefile("${path.root}/iis-install.tpl", {

  })


  tags = {
    Name    = var.ec2_instance_name
    vm_Name = var.ec2_instance_name
    iot_web_server = true
  }

  lifecycle {
    ignore_changes = [
      tags
    ]
  }
}