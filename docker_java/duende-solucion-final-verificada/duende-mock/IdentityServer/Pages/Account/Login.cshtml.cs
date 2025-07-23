
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;

public class LoginModel : PageModel
{
    [BindProperty]
    public string Username { get; set; }
    [BindProperty]
    public string Password { get; set; }

    public IActionResult OnPost()
    {
        if (Username == "testuser" && Password == "password")
        {
            return Redirect(Request.Query["ReturnUrl"]);
        }
        return Page();
    }
}
