using Duende.IdentityServer.Models;
using Duende.IdentityServer.Test;
using System.Collections.Generic;
using System.Security.Claims;

public static class Config
{
    public static IEnumerable<IdentityResource> IdentityResources =>
        new[] { new IdentityResources.OpenId(), new IdentityResources.Profile() };

    public static IEnumerable<Client> Clients =>
        new[] {
            new Client {
                ClientId = "java-client",
                ClientSecrets = { new Secret("secret".Sha256()) },
                AllowedGrantTypes = GrantTypes.Code,
                RedirectUris = { "http://localhost:8080/login/oauth2/code/duende" },
                AllowedScopes = { "openid", "profile" },
                RequirePkce = true
            }
        };

    public static List<TestUser> Users =>
        new() {
            new TestUser {
                SubjectId = "1",
                Username = "demo",
                Password = "password",
                Claims = new[] { new Claim("name", "Demo User") }
            }
        };
}