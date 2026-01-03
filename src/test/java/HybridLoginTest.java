import com.automation.core.AuthService;
import com.automation.core.PlaywrightFactory;
import com.microsoft.playwright.Page;

public class HybridLoginTest {
    public static void main(String[] args) {
        // 1. Get a fresh, clean Page from your Thread-Safe Factory
        Page page = PlaywrightFactory.getPage();

        // --- ADD THIS LINE ---
        // Visit the domain first to initialize the "storage state" for this domain
        page.navigate("https://www.saucedemo.com/");

        // 2. THE ARCHITECT MOVE: Inject Session *before* navigating
        // We do this on the CONTEXT, because cookies belong to the Context, not the Page.
        AuthService.injectAuth(page.context());

        // 3. Navigate directly to the internal dashboard
        // Note: We intentionally skip 'index.html' (Login Page) and go to 'inventory.html'
        System.out.println("🚀 Navigating directly to Dashboard...");
        page.navigate("https://www.saucedemo.com/inventory.html");

        // 4. Assert we are actually inside
        // If the injection failed, SauceDemo would kick us back to the Login screen.
        if (page.locator(".title").isVisible()) {
            System.out.println("TEST PASSED: Logged in instantly without typing!");
            System.out.println("   Verified Element: " + page.locator(".title").textContent());
        } else {
            System.err.println("TEST FAILED: Application rejected the cookie.");
        }

        // 5. Cleanup
        PlaywrightFactory.close();
    }
}