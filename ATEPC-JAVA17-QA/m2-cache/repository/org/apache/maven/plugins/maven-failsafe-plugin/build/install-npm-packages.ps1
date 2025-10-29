	. .\.github\build\common.ps1 #importing common functions
		  
	Set-Location "./UI"
          
	# Create local .npmrc file
	pro_info "Installing npm packages - Step 1 of 3: Create local .npmrc file"
	$text = @"
	strict-ssl=false
	registry=https://artifactory.utc.com/artifactory/api/npm/tisus-usp-travel-itinerary-us-npm-dev-l/
	//artifactory.utc.com/artifactory/api/npm/tisus-usp-travel-itinerary-us-npm-dev-l/:_auth=$env:JF_AUTH
	//artifactory.utc.com/artifactory/api/npm/tisus-usp-travel-itinerary-us-npm-dev-l/:_password=$env:JF_PASSWORD
	//artifactory.utc.com/artifactory/api/npm/tisus-usp-travel-itinerary-us-npm-dev-l/:username=$env:JF_USER
	//artifactory.utc.com/artifactory/api/npm/tisus-usp-travel-itinerary-us-npm-dev-l/:always-auth=true
"@

	# Create a file named .npmrc in the current directory and write the text to it
	Set-Content -Path ".npmrc" -Value $text -Force
          
          
	pro_info "Installing npm packages - Step 2 of 3: Update package-lock.json file to use AeroCloud Artifactory"
	# Read the content of the package-lock.json file as an array of lines
        $inputStrings = Get-Content -Path "package-lock.json" 
        # Create an empty array to store the output strings
        $outputArray = @()
        # Loop through each line
        foreach ($line in $inputStrings) 
          {
          $outputString = $line
          # Check if the line contains "resolved"
          if ($line -match "resolved")
              {
              $lineWithUrlReplaced = $line -replace "/[^/]+\/-\/", "-"
              # Replace the old URL with the new URL
              $lineWithUrlReplaced = $lineWithUrlReplaced -replace "registry.npmjs.org", "artifactory.utc.com/artifactory/tisus-usp-travel-itinerary-us-npm-dev-l"    
              $lineWithUrlReplaced = $lineWithUrlReplaced -replace "artifactory.rms.ray.com:443/artifactory/api/npm/appsrvcs-npm-npmjs-remote", "artifactory.utc.com/artifactory/tisus-usp-travel-itinerary-us-npm-dev-l"
              $lineWithUrlReplaced = $lineWithUrlReplaced -replace "artifactory.utc.com/artifactory/tisus-usp-travel-itinerary-us-npm-dev-l/@", "artifactory.utc.com/artifactory/tisus-usp-travel-itinerary-us-npm-dev-l/"
              $lineWithUrlReplaced = $lineWithUrlReplaced -replace "artifactory.utc.com/artifactory/tisus-usp-travel-itinerary-us-npm-dev-l-", "artifactory.utc.com/artifactory/tisus-usp-travel-itinerary-us-npm-dev-l/"
              $outputString = $lineWithUrlReplaced
              }
          # Append the output string to the output array
          $outputArray += $outputString
          }
       	Set-Content -Path "package-lock.json" -Value $outputArray   
	  
       	# We've created a local .nprmc file with the proper credentials, and updated package-lock.json for AeroCloud Artifactory
	# Now install the npm packages (the ci command will use .nprmc & package-lock.json)
	#
	pro_info "Installing npm packages - Step 3 of 3: run npm ci command"
       	npm ci --userconfig=".npmrc" --legacy-peers-deps 
	
	pro_info "Done Installing npm packages"
