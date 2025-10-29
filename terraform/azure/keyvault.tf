data "azurerm_key_vault" "existing_key_vault" {
  name                = var.key_vault_name
  resource_group_name = var.AZURE_RESOURCE_GROUP
}

data "azurerm_windows_web_app" "webapp_ui" {
  name                = azurerm_windows_web_app.webapp_ui.name
  resource_group_name = var.AZURE_RESOURCE_GROUP
}

data "azurerm_client_config" "client_config" {}

resource "azurerm_key_vault_secret" "ping_federate_client_secret" {
  name         = "PingFederateClientSecret"
  value        = var.PING_FEDERATE_CLIENT_SECRET
  key_vault_id = data.azurerm_key_vault.existing_key_vault.id
}

resource "azurerm_key_vault_access_policy" "key_vault_access_policy_app_service" {
  key_vault_id = data.azurerm_key_vault.existing_key_vault.id
  tenant_id    = data.azurerm_windows_web_app.webapp_ui.identity[0].tenant_id
  object_id    = data.azurerm_windows_web_app.webapp_ui.identity[0].principal_id
  secret_permissions = ["List", "Get"]
}
