       . .\.github\build\common.ps1 #importing common functions
		
      pro_info "Installing NuGet packages - Step 1 of 2: Create local nuget.config file"
      $sourceName = "TISArtifactory"
      $sourceUrl = "https://artifactory.utc.com/artifactory/api/nuget/tisus-usp-travel-itinerary-us-nuget-dev-l"
      $sourceNameMsftOffline = "Microsoft Visual Studio Offline Packages"
      $sourceUrlMsftOffline  = "C:\Program Files (x86)\Microsoft SDKs\NuGetPackages\"
      
      # Create local nuget.config file in the current directory
      $xml = New-Object System.Xml.XmlDocument
      # Create the root element <configuration>
      $root = $xml.CreateElement("configuration")
      $xml.AppendChild($root)
      # Create the child element <packageSources>
      $packageSources = $xml.CreateElement("packageSources")
      $root.AppendChild($packageSources)
      # Save the XML document to a file named nuget.config
      $xml.Save("nuget.config")
      # Add the source and credentials to nuget.config file
      nuget sources add -Name $sourceName -Source $sourceUrl -username $env:JF_USER -password $env:JF_USER_TOKEN -ConfigFile ".\nuget.config"
      
      #pro_info "Begin listing the local nuget.config contents"
      #$content = Get-Content -Path ".\nuget.config"
      #Write-Output "nuget.config = $content"
     
      nuget sources list -configfile "./nuget.config"
      
      pro_info "Installing NuGet packages - Step 2 of 2: run nuget restore (using the local nuget.config file)"
      
      #nuget restore "./TIS.sln" -ConfigFile "./nuget.config" 
      pro_info      "nuget restore ./Api/Api.csproj" 
      nuget restore               "./Api/Api.csproj"                     -ConfigFile "./nuget.config"
      pro_info      "nuget restore ./Core/Core.csproj" 
      nuget restore               "./Core/Core.csproj"                   -ConfigFile "./nuget.config" 
      pro_info      "nuget restore ./Data/Data.csproj" 
      nuget restore               "./Data/Data.csproj"                   -ConfigFile "./nuget.config" 
      pro_info      "nuget restore ./Services/Services.csproj" 
      nuget restore               "./Services/Services.csproj"           -ConfigFile "./nuget.config"  
      pro_info      "nuget restore ./PingOne.Core/PingOne.Core.csproj"
      nuget restore               "./PingOne.Core/PingOne.Core.csproj"   -ConfigFile "./nuget.config"
      pro_info      "nuget restore ./WebJobs/WebJob/WebJob.csproj" 
      nuget restore               "./WebJobs/WebJob/WebJob.csproj"       -ConfigFile "./nuget.config"
      
		  
      pro_info "Done Installing NuGet packages"
