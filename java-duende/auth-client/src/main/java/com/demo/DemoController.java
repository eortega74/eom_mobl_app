@RestController
public class DemoController {
    @GetMapping("/secure")
    public String secured(Authentication authentication) {
        return "Autenticado como: " + authentication.getName();
    }
}