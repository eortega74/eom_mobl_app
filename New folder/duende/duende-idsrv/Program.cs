var builder = WebApplication.CreateBuilder(args);
builder.Services.AddIdentityServer().AddInMemoryClients([]).AddInMemoryApiScopes([]).AddDeveloperSigningCredential();
var app = builder.Build();
app.UseIdentityServer();
app.Run();
