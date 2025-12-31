import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;

public class ContextTest {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));

            // --- Step 1: Create Two Isolated Contexts ---
            // Think of these as two completely separate Incognito windows
            BrowserContext adminContext = browser.newContext();
            BrowserContext guestContext = browser.newContext();

            // Create pages for each context
            Page adminPage = adminContext.newPage();
            Page guestPage = guestContext.newPage();

            // --- Step 2: Log in with Admin Context ---
            System.out.println("Logging in on Admin Page...");
            adminPage.navigate("https://www.saucedemo.com/");
            adminPage.locator("#user-name").fill("standard_user");
            adminPage.locator("[data-test='password']").fill("secret_sauce");
            adminPage.locator("[data-test='login-button']").click();

            // Verify Admin is logged in (Check for 'Products' title)
            if (adminPage.getByText("Products").isVisible()) {
                System.out.println("✅ Admin Context is logged in.");
                Page anotherAdminPage = adminContext.newPage();
                // Don't go to "/", go to where a logged-in user belongs
                anotherAdminPage.navigate("https://www.saucedemo.com/inventory.html");
                System.out.println("✅ Open another admin page.");
            }

            // --- Step 3: Verify Isolation on Guest Context ---
            System.out.println("Checking Guest Page (Should be logged OUT)...");
            guestPage.navigate("https://www.saucedemo.com/inventory.html");

            // CRITICAL CHECK: Guest page should still see the Login Button
            // If cookies leaked, this assertion would fail because we'd see 'Products'
            boolean isLoginVisible = guestPage.locator("[data-test='login-button']").isVisible();

            if (isLoginVisible) {
                System.out.println("Guest Context is clean! No session leakage detected.");
            } else {
                System.err.println("TEST FAILED: Guest context picked up the Admin session!");
            }

            // Close browser
            browser.close();
        }
    }
}