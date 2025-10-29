variable "application_insights_name" { type = string }
variable "log_analytics_workspace_name" { type = string }

variable "app_service_plan_name" { type = string }
variable "soa_cert_thumbprint" { type = string }

variable "WEBAPP_UI_NAME" { type = string }
variable "webapp_ui_hostname" { type = string }
variable "WEBAPP_API_NAME" { type = string }
variable "WEBAPP_WEBJOBS_NAME" { type = string }

variable "sqlserver_name" { type = string }
variable "SQL_SERVER_ADMINISTRATOR_PASSWORD" { type = string }
variable "SQLSERVER_ADMINISTRATOR_NAME" { type = string }
variable "sqlserver_administrator_group_object_id" { type = string }
variable "sqlserver_sp_name" { type = string }
variable "sqlserver_sp_object_id" { type = string }

variable "MSSQL_DATABASE" { type = string }
variable "database_max_size_gb" { type = string }
variable "database_sku_name" { type = string }

variable "subnet_name" { type = string }
variable "subnet_name_pe" { type = string }

variable "virtual_network_name" { type = string }
variable "vnet_resource_group_name" { type = string }

variable "app_current_stack" { type = string }
variable "application_type" { type = string }
variable "dotnet_version" { type = string }
variable "location" { type = string }
variable "service_plan_os_type" { type = string }
variable "service_plan_sku_name" { type = string }

variable "AZURE_RESOURCE_GROUP" { type = string }
variable "PFX_FILE" { type = string }

#variable "SOA_API_PUBLIC_CERT_FILE" { type = string } # TODO - bring SOA and federated into commented out options

variable "PFX_PASSWORD" { 
    type = string 
    sensitive = true
}

#variable "PING_FEDERATE_CLIENT_SECRET" { # TODO - bring SOA and federated into commented out options
#    type = string 
#    sensitive = true
#}


