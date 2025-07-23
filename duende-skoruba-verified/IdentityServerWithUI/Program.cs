using IdentityServerWithUI;
using Serilog;
using Microsoft.AspNetCore.Http;

Log.Logger = new LoggerConfiguration()
    .WriteTo.Console()
    .CreateBootstrapLogger();

Log.Information("Starting up");

try
{
    var builder = WebApplication.CreateBuilder(args);

    builder.Host.UseSerilog((ctx, lc) => lc
        .WriteTo.Console(outputTemplate: "[{Timestamp:HH:mm:ss} {Level}] {SourceContext}{NewLine}{Message:lj}{NewLine}{Exception}{NewLine}")
        .Enrich.FromLogContext()
        .ReadFrom.Configuration(ctx.Configuration));

    // 🔐 Ajustes de cookies para entorno local (desarrollo)
    builder.Services.Configure<CookiePolicyOptions>(options =>
    {
        options.MinimumSameSitePolicy = SameSiteMode.Lax; // 👈 Asegura Lax y no None
    });

    builder.Services.ConfigureApplicationCookie(options =>
    {
        options.Cookie.SameSite = SameSiteMode.Lax; // 👈 Esto evita el warning de SameSite=None sin HTTPS
        options.Cookie.SecurePolicy = CookieSecurePolicy.None; // 👈 Permite usar cookies sin HTTPS (solo para dev local)
    });

    var app = builder
        .ConfigureServices()
        .ConfigurePipeline();

    // Seeding manual con /seed
    if (args.Contains("/seed"))
    {
        Log.Information("Seeding database...");
        SeedData.EnsureSeedData(app);
        Log.Information("Done seeding database. Exiting.");
        return;
    }

    app.UseCookiePolicy(); // 👈 Asegura que la política se aplique

    app.Run();
}
catch (Exception ex) when (ex is not HostAbortedException)
{
    Log.Fatal(ex, "Unhandled exception");
}
finally
{
    Log.Information("Shut down complete");
    Log.CloseAndFlush();
}
