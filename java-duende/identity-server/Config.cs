using Duende.IdentityServer.Models;
using System.Collections.Generic;

public static class Config
{
    public static IEnumerable<IdentityResource> GetIdentityResources() =>
        new List<IdentityResource>
        {
            new IdentityResources.OpenId(),
            new IdentityResources.Profile()
        };

    public static IEnumerable<ApiScope> GetApiScopes() =>
        new List<ApiScope>
        {
            new ApiScope("api1", "My API")
        };

    public static IEnumerable<Client> GetClients() =>
        new List<Client>
        {
            new Client
            {
                ClientId = "java-client",
                ClientName = "Java Spring Boot App",

                AllowedGrantTypes = GrantTypes.Code,
                RequireClientSecret = true,
                ClientSecrets = { new Secret("secret".Sha256()) }, // ⚠️ asegúrate de que este secreto coincida con el del `application.properties`

                RedirectUris = { "http://java-app:8080/login/oauth2/code/duende" },
                PostLogoutRedirectUris = { "http://java-app:8080/" },

                AllowedScopes = { "openid", "profile", "email" },

                RequirePkce = false,
                AllowPlainTextPkce = false,

                AllowedCorsOrigins = { "http://java-app:8080" },
                AllowAccessTokensViaBrowser = true
            }
        };
}
