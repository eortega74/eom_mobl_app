#resource "azurerm_app_service_certificate" "webapp_ui_hostname_binding_ssl" {
#  name                = "${var.webapp_ui_hostname}-cert"
#  resource_group_name = var.AZURE_RESOURCE_GROUP
#  location            = var.location
#  pfx_blob            = filebase64(var.PFX_FILE)
#  password            = var.PFX_PASSWORD
#
#  lifecycle {
#    ignore_changes = [tags]
#  }
#}
#
#// This public certificate is coming from the soa api used for ccn
#resource "azurerm_app_service_public_certificate" "webapp_ui_public_api_certificate" {
#  resource_group_name  = var.AZURE_RESOURCE_GROUP
#  app_service_name     = azurerm_windows_web_app.webapp_ui.name
#  certificate_name     = var.SOA_API_PUBLIC_CERT_FILE
#  certificate_location = "CurrentUserMy"
#  blob                 = filebase64("${var.SOA_API_PUBLIC_CERT_FILE}.cer")
#}

resource "azurerm_app_service_custom_hostname_binding" "webapp_ui_hostname_binding" {
  hostname            = var.webapp_ui_hostname
  app_service_name    = azurerm_windows_web_app.webapp_ui.name
  resource_group_name = var.AZURE_RESOURCE_GROUP
#  ssl_state           = "SniEnabled" # IpBasedEnabled SniEnabled
#  thumbprint          = azurerm_app_service_certificate.webapp_ui_hostname_binding_ssl.thumbprint
}

resource "azurerm_windows_web_app" "webapp_ui" {
  service_plan_id           = azurerm_service_plan.appplan.id
  location                  = var.location
  name                      = var.WEBAPP_UI_NAME
  resource_group_name       = var.AZURE_RESOURCE_GROUP
  https_only                = true
  virtual_network_subnet_id = data.azurerm_subnet.subnet.id

  site_config {
    application_stack {
      current_stack  = var.app_current_stack
      dotnet_version = var.dotnet_version
    }
    vnet_route_all_enabled = true
    http2_enabled          = true
  }

  app_settings = {
    APPLICATIONINSIGHTSAGENT_EXTENSION_VERSION = "~3"
    WEBSITE_LOAD_CERTIFICATES                  = var.soa_api_public_certificate_thumbprint
    WEBSITE_RUN_FROM_PACKAGE                   = "1"
    APPINSIGHTS_INSTRUMENTATIONKEY             = azurerm_application_insights.app_insights.instrumentation_key
#    KEY_VAULT_URI = var.key_vault_uri
  }

  logs {
    detailed_error_messages = true
    failed_request_tracing  = true
    application_logs {
      file_system_level = "Verbose"
    }
  }
  # Specifies the type of Managed Service Identity
  identity {
    type = "SystemAssigned"
  }

  lifecycle {
    ignore_changes = [tags]
  }
}

resource "azurerm_private_endpoint" "webapp_ui_pe" {
  name                = "${azurerm_windows_web_app.webapp_ui.name}-pe01"
  resource_group_name = var.AZURE_RESOURCE_GROUP
  location            = var.location
  subnet_id           = data.azurerm_subnet.subnet_pe.id

  private_service_connection {
    name                           = "${azurerm_windows_web_app.webapp_ui.name}-pe01"
    private_connection_resource_id = azurerm_windows_web_app.webapp_ui.id
    is_manual_connection           = false
    subresource_names              = ["sites"]
  }

  lifecycle {
    ignore_changes = [tags]
  }
}

