# Terraform

The following terraform came from `https://github-us.utc.com/us-persons-only/SAR-iac.git`.

## Configuration

Each of the environment variable from `GITHUB_WORKSPACE/.github/powershell/config.ps1` and `GITHUB_WORKSPACE/.github/bash/config.ps1` will be populated as terraform variables.  Every time they `export ENV_VAR_NAME="value"`, they will also `export TV_VAR_ENV_VAR_NAME="value"`.

## Depends On

Avoid using explicitaly statins dependencies in terraform.  In most to all cases, terraform can calculate this.  If it's not calculatable, look at restructuring before introducing depends.

## Running Terraform

Run the Terraform as it would be from the pipeline:

**bash**

```
export GITHUB_WORKSPACE="/mnt/c/code/PMXXX-webapp"
cd "$GITHUB_WORKSPACE"

source $GITHUB_WORKSPACE/.github/bash/config.sh local

# proxy local host works if network type mirror
export https_proxy="http://localhost:9000"
export http_proxy="http://localhost:9000"

bash $GITHUB_WORKSPACE/.github/workflows/tf-plan.sh
bash $GITHUB_WORKSPACE/.github/workflows/tf-apply.sh "$env:VERSION_BREAKING.$env:VERSION_NEW_FEATURE.$env:GITHUB_RUN_NUMBER"

# NOTE: Your first deployment should fail due to private endpoint errors - the PEs get provisioned automatically by XetaCloud remediation upon app service spin-up. After the first deployment fails, go into your resource group and delete the PEs. Then re-run the deployment so it is created & tracked via Terraform. 

$env:VERSION_NEW_FEATURE.$env:GITHUB_RUN_NUMBER"
bash $GITHUB_WORKSPACE/.github/workflows/destroy.sh
bash $GITHUB_WORKSPACE/.github/workflows/plan.sh
terraform destroy --var-file "vars-$env.tfvars" -auto-approve -input=false
```