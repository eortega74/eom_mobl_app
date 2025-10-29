data "azurerm_subnet" "subnet" {
  name                 = var.subnet_name
  virtual_network_name = var.virtual_network_name
  resource_group_name  = var.vnet_resource_group_name
}

data "azurerm_subnet" "subnet_pe" {
  name                 = var.subnet_name_pe
  virtual_network_name = var.virtual_network_name
  resource_group_name  = var.vnet_resource_group_name
}

output "subnet_id" {
  value = data.azurerm_subnet.subnet.id
}

output "subnet_pe_id" {
  value = data.azurerm_subnet.subnet_pe.id
}