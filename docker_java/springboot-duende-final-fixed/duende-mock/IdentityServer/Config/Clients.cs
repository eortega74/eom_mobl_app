
using Duende.IdentityServer.Models;
using System.Collections.Generic;

public static class Clients
{
    public static IEnumerable<Client> Get()
    {
        return new List<Client>
        {
            new Client
            {
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
        };
    }
}
