resource "azurerm_service_plan" "appplan" {
  name                = var.app_service_plan_name
  resource_group_name = var.AZURE_RESOURCE_GROUP
  location            = var.location
  sku_name            = var.service_plan_sku_name
  os_type             = var.service_plan_os_type

  lifecycle {
    ignore_changes = [tags]
  }
}