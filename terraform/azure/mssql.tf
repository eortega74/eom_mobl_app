resource "azurerm_mssql_server" "sqlserver" {
  name                = var.sqlserver_name
  resource_group_name = var.AZURE_RESOURCE_GROUP
  location            = var.location
  version             = "12.0"
  minimum_tls_version = "1.2"
  public_network_access_enabled = false
  
  ### SQL AUTH ###
  # Desired User Name - ClousSa####
  administrator_login          = var.SQLSERVER_ADMINISTRATOR_NAME
  # 24 char random password
  administrator_login_password = var.SQL_SERVER_ADMINISTRATOR_PASSWORD
  ### SQL AUTH ###

  ### SERVICE PRINCIPAL AUTH ###
  azuread_administrator {
    azuread_authentication_only = false
    login_username              = "data-modify-group" # do not change this name
    # guid for the data modify group
    object_id                   = var.sqlserver_administrator_group_object_id # this has to be data modify guid
  }
  ### SERVICE PRINCIPAL AUTH ###

  identity {
    type = "SystemAssigned"
  }

  lifecycle {
    # Prevent Terraform from modifying 'azuread_authentication_only' and 'administrator_login_password' by ignoring changes to this block
    ignore_changes = [
      administrator_login_password,
      tags,
      azuread_administrator
    ]
  }
}

resource "azurerm_private_endpoint" "sqlserver_pe" {
  name                = "${azurerm_mssql_server.sqlserver.name}-pe"
  resource_group_name = var.AZURE_RESOURCE_GROUP
  location            = var.location
  subnet_id           = data.azurerm_subnet.subnet_pe.id

  private_service_connection {
    name                           = "${azurerm_mssql_server.sqlserver.name}-pe"
    private_connection_resource_id = azurerm_mssql_server.sqlserver.id
    is_manual_connection           = false
    subresource_names              = ["sqlServer"]
  }

  lifecycle {
    ignore_changes = [tags]
  }
}

resource "azurerm_mssql_database" "sqlserver_database" {
  name        = var.MSSQL_DATABASE
  server_id   = azurerm_mssql_server.sqlserver.id
  collation   = "SQL_Latin1_General_CP1_CI_AS"
  max_size_gb = var.database_max_size_gb
  sku_name    = var.database_sku_name

  lifecycle {
    ignore_changes = [tags]
  }
}
