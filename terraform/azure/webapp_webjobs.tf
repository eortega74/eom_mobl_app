resource "azurerm_windows_web_app" "webapp_webjobs" {
  service_plan_id           = azurerm_service_plan.appplan.id
  location                  = var.location
  name                      = var.WEBAPP_WEBJOBS_NAME
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

 identity {
    type = "SystemAssigned"
  }

  app_settings = {
    APPLICATIONINSIGHTSAGENT_EXTENSION_VERSION = "~3"
    WEBSITE_RUN_FROM_PACKAGE                   = "1"
    WEBSITE_LOAD_CERTIFICATES                  = var.soa_cert_thumbprint
    "APPINSIGHTS_INSTRUMENTATIONKEY"           = azurerm_application_insights.app_insights.instrumentation_key
#    KEY_VAULT_URI = var.key_vault_uri
  }

  logs {
    detailed_error_messages = true
    failed_request_tracing  = true
    application_logs {
      file_system_level = "Verbose"
    }
  }

  lifecycle {
    ignore_changes = [tags]
  }
}

resource "azurerm_private_endpoint" "webapp_webjobs_pe" {
  name                = "${azurerm_windows_web_app.webapp_webjobs.name}-pe01"
  resource_group_name = var.AZURE_RESOURCE_GROUP
  location            = var.location
  subnet_id           = data.azurerm_subnet.subnet_pe.id

  private_service_connection {
    name                           = "${azurerm_windows_web_app.webapp_webjobs.name}-pe01"
    private_connection_resource_id = azurerm_windows_web_app.webapp_webjobs.id
    is_manual_connection           = false
    subresource_names              = ["sites"]
  }

  lifecycle {
    ignore_changes = [tags]
  }
}
