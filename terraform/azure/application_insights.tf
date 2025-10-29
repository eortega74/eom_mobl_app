resource "azurerm_log_analytics_workspace" "log_analytics_workspace" {
  name                = var.log_analytics_workspace_name
  location            = var.location
  resource_group_name = var.AZURE_RESOURCE_GROUP

  lifecycle {
    ignore_changes = [tags]
  }
}

resource "azurerm_application_insights" "app_insights" {
  name                = var.application_insights_name
  location            = var.location
  resource_group_name = var.AZURE_RESOURCE_GROUP
  application_type    = var.application_type
  workspace_id        = azurerm_log_analytics_workspace.log_analytics_workspace.id

  lifecycle {
    ignore_changes = [tags]
  }
}