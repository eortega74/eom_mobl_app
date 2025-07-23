using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.DependencyInjection;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddIdentityServer()
    .AddInMemoryClients(Config.Clients)
    .AddInMemoryIdentityResources(Config.IdentityResources)
    .AddTestUsers(Config.Users)
    .AddDeveloperSigningCredential();

var app = builder.Build();
app.UseIdentityServer();
app.Run();