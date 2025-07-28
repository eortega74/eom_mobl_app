# Set JAVA_HOME and add JDK bin to PATH
$env:JAVA_HOME = "C:\Users\C95059698\Documents\repos\jdk-11"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

# Set MAVEN_HOME and add Maven bin to PATH
$env:MAVEN_HOME = "C:\Users\C95059698\Documents\repos\apache-maven-3.9.10"
$env:Path = "$env:MAVEN_HOME\bin;$env:Path"

# Set TERRAFORM_HOME and add Terraform folder to PATH
$env:TERRAFORM_HOME = "C:\Users\C95059698\Documents\repos\terraform"
$env:Path = "$env:TERRAFORM_HOME;$env:Path"

$env:Kubectl_HOME = "C:\kubectl"
$env:Path = "$env:kubectl_HOME;$env:Path"

$env:minikube_HOME = "C:\minikube"
$env:Path = "$env:minikube_HOME;$env:Path"

$env:docker_HOME = "C:\docker"
$env:Path = "$env:docker_HOME;$env:Path"



Write-Host "JAVA_HOME set to $env:JAVA_HOME"
Write-Host "TERRAFORM_HOME set to $env:TERRAFORM_HOME"
Write-Host "PATH updated for this session."