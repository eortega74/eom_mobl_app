application_insights_name    = "techrs-prod-app-insights"
log_analytics_workspace_name = "techrs-prod-log-analytics-workspace"

app_service_plan_name        = "techrs-prod-app-service-plan"

webapp_ui_hostname                      = "programmetrics-qa.test.app.ray.com"
soa_cert_thumbprint                     = "2B9E9FB7021C0651632A8D9FE8569FDC7C00F236"
sqlserver_name                          = "pgmmet-qa-sql-server"
sqlserver_administrator_group_object_id = "fe2aafcb-1e1f-49bd-ab42-d74794019b2a"

sqlserver_sp_name      = "zg1-rtx-dce-npd01-x12q1-pgmmet-datamodify"
sqlserver_sp_object_id = "a43d9b5a-6b05-4ea8-b2d2-8e7764307a6e"

database_max_size_gb = "250"
database_sku_name = "S0"

subnet_name                  = "x12p1techrs-sn02"
subnet_name_pe               = "x12p1techrs-sn01"

virtual_network_name         = "x12p1cldnet-vn01"
vnet_resource_group_name     = "zg1-rtx-dce-pro01-x12p1-cldnet"

app_current_stack            = "dotnet"
application_type             = "web"
dotnet_version               = "v4.0"
location                     = "USGov Virginia"
service_plan_os_type         = "Windows"
service_plan_sku_name        = "P1v3"