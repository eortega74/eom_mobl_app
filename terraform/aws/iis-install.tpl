<powershell>
# Install IIS and required features
Install-WindowsFeature -Name Web-Server, Web-Asp-Net45, Web-Net-Ext45, `
    Web-ISAPI-Ext, Web-ISAPI-Filter, Web-Mgmt-Console, `
    Web-Http-Logging, Web-Request-Monitor, Web-Static-Content, `
    Web-Default-Doc, Web-Dir-Browsing, Web-Http-Errors, Web-Http-Redirect, `
    Web-Health, Web-Performance, Web-Security, Web-Filtering, `
    Web-WebSockets -IncludeManagementTools

# Start IIS service and set to start automatically
Start-Service W3SVC
Set-Service W3SVC -StartupType Automatic

# Create a new directory for the IOT_UI site
New-Item -Path 'C:\inetpub\IOT_UI' -ItemType Directory -Force

# Write a test index.html for the new site
"Hello from the IOT_UI site!" | Out-File -Encoding UTF8 C:\inetpub\IOT_UI\index.html

# Import the IIS module to manage sites
Import-Module WebAdministration

# Create a new IIS website named 'IOT_UI' on port 80
New-Website -Name "IOT_UI" `
    -Port 80 `
    -PhysicalPath "C:\inetpub\IOT_UI" `
    -ApplicationPool "DefaultAppPool" `
    -Force
</powershell>
<persist>true</persist>
