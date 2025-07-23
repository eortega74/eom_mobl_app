
using IdentityServer4.Models;
using IdentityServer4.Test;
using System.Collections.Generic;
using System.Security.Claims;

public static class Config
{
    public static IEnumerable<Client> Clients =>
        new Client[] {
            new Client {
                ClientId = "java-client",
                ClientSecrets = { new Secret("secret".Sha256()) },
                AllowedGrantTypes = GrantTypes.Code,
                RedirectUris = { "http://localhost:8080/login/oauth2/code/oidc" },
                AllowedScopes = { "openid", "profile" },
                RequirePkce = false
            }
        };
        

    public static IEnumerable<IdentityResource> IdentityResources =>
        new IdentityResource[] {
            new IdentityResources.OpenId(),
            new IdentityResources.Profile()
        };

    public static IEnumerable<ApiScope> ApiScopes =>
        new ApiScope[] { new ApiScope("api1", "My API") };

    public static List<TestUser> Users =>
        new List<TestUser> {
            new TestUser {
                SubjectId = "1",
                Username = "testuser",
                Password = "password",
                Claims = new List<Claim> {
                    new Claim("name", "Test User"),
                    new Claim("website", "https://example.com")
                }
            }
        };
}
