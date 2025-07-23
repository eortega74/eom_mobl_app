
using Duende.IdentityServer;
using Duende.IdentityServer.Models;
using Duende.IdentityServer.Test;
using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using System.Collections.Generic;
using System.Security.Claims;

var builder = WebApplication.CreateBuilder(args);
builder.Services.AddRazorPages();

builder.Services.AddIdentityServer()
    .AddInMemoryClients(new List<Client> {
        new Client {
            ClientId = "springboot-client",
            ClientSecrets = { new Secret("secret".Sha256()) },
            AllowedGrantTypes = GrantTypes.Code,
            RequirePkce = false,
            RequireClientSecret = true,
            RedirectUris = { "http://localhost:8080/login/oauth2/code/duende" },
            PostLogoutRedirectUris = { "http://localhost:8080/" },
            AllowedScopes = { "openid", "profile" },
            AllowOfflineAccess = true
        }
    })
    .AddInMemoryIdentityResources(new List<IdentityResource> {
        new IdentityResources.OpenId(),
        new IdentityResources.Profile()
    })
    .AddTestUsers(new List<TestUser> {
        new TestUser {
            SubjectId = "1",
            Username = "alice",
            Password = "alice",
            Claims = new List<Claim> {
                new Claim("name", "Alice"),
                new Claim("website", "https://alice.com")
            }
        }
    })
    .AddDeveloperSigningCredential();

var app = builder.Build();
app.UseStaticFiles();
app.UseRouting();
app.UseIdentityServer();
app.UseAuthorization();
app.MapRazorPages();
app.Run();
