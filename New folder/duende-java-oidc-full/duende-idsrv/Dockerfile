
FROM mcr.microsoft.com/dotnet/sdk:6.0 AS build
WORKDIR /src
COPY . .
RUN dotnet new web -o idsrv
WORKDIR /src/idsrv
COPY ../Program.cs Program.cs
RUN dotnet add package Duende.IdentityServer.AspNetIdentity --version 6.0.0
RUN dotnet add package Duende.IdentityServer --version 6.0.0
RUN dotnet add package Microsoft.AspNetCore.Razor.Runtime --version 6.0.10
RUN dotnet build -c Release

FROM mcr.microsoft.com/dotnet/aspnet:6.0
WORKDIR /app
COPY --from=build /src/idsrv/bin/Release/net6.0/ .
ENTRYPOINT ["dotnet", "idsrv.dll"]
