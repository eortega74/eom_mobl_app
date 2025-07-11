# Elimina contenedores y volúmenes
docker-compose down -v

# Borra y recrea la carpeta ldif
$ldifPath = ".\ldap\ldif"
if (Test-Path $ldifPath) {
    Remove-Item -Recurse -Force $ldifPath
}
New-Item -ItemType Directory -Force -Path $ldifPath | Out-Null

# Contenido del archivo users.ldif
$ldifContent = @"
dn: dc=example,dc=com
objectClass: top
objectClass: domain
dc: example

dn: ou=users,dc=example,dc=com
objectClass: organizationalUnit
ou: users

dn: uid=testuser,ou=users,dc=example,dc=com
objectClass: inetOrgPerson
uid: testuser
sn: User
cn: Test User
userPassword: testpass
"@

# Crea el archivo users.ldif si no existe o está vacío
$ldifFile = Join-Path $ldifPath "users.ldif"
if (!(Test-Path $ldifFile) -or ((Get-Content $ldifFile) -eq $null)) {
    $ldifContent | Set-Content $ldifFile
}

# Levanta los servicios
docker-compose up --build