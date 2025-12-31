import com.automation.core.PlaywrightFactory;
import com.microsoft.playwright.Page;

public class RealNetworkInterceptTest {
    public static void main(String[] args) {
        Page page = PlaywrightFactory.getPage();

        // 1. Navigate to a site with REAL server-side login
        System.out.println("Navigating to The Internet (Herokuapp)...");
        page.navigate("https://the-internet.herokuapp.com/login");

        // 2. Fill valid credentials
        page.fill("#username", "tomsmith");
        page.fill("#password", "SuperSecretPassword!");

        System.out.println("Wait! About to click Login...");

        // 3. THE TRAP: Intercept the POST request to /authenticate
        // This is the specific signal sent when you click the button.
        page.route("**/authenticate", route -> {
            System.out.println(">>>TRAP TRIGGERED! Killing request to: " + route.request().url());
            route.abort("failed"); // Simulate Server Crash (500/Network Error)
        });

        // 4. Trigger the Action
        page.locator(".radius").click();

        // 5. Verification
        // Give it a moment to realize the network failed
        page.waitForTimeout(2000);

        String currentUrl = page.url();
        System.out.println("Current URL: " + currentUrl);

        // LOGIC UPDATE:
        // We passed if we did NOT reach the secure area.
        // It is okay if we are stuck on "/authenticate" with an error page.
        boolean reachedSecureArea = currentUrl.contains("/secure");

        page.waitForURL("**/authenticate.html");

        if (!reachedSecureArea) {
            System.out.println("[PASS] Resilience Success: We blocked access to the secure area.");
        } else {
            System.out.println("[FAIL] Security Breach! The user reached the dashboard.");
        }

        PlaywrightFactory.close();
    }
}