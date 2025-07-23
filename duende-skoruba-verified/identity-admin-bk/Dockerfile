# ---------- Build stage ----------
FROM mcr.microsoft.com/dotnet/sdk:6.0 AS build
WORKDIR /src

COPY . .

RUN dotnet restore Skoruba.IdentityServer4.Admin.sln

RUN dotnet publish Skoruba.IdentityServer4.Admin.UI/Skoruba.IdentityServer4.Admin.UI.csproj \
    -c Release \
    --self-contained false \
    -o /app/publish

# ---------- Runtime stage ----------
FROM mcr.microsoft.com/dotnet/aspnet:6.0 AS runtime
WORKDIR /app

# Copiar los archivos publicados (incluye runtimeconfig.json)
COPY --from=build /app/publish ./

EXPOSE 80

ENTRYPOINT ["dotnet", "Skoruba.IdentityServer4.Admin.UI.dll"]