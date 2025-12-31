import com.microsoft.playwright.*;
import org.testng.annotations.Test;

public class ContextTestOrg {

    @Test
    public void testIsolatedContexts() {
        try (Playwright playwright = Playwright.create()) {
            // 1. Launch ONE Browser (Heavy process)
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(false)
            );

            // 2. Create Context A (User: Admin)
            // This acts like Incognito Window #1
            BrowserContext adminContext = browser.newContext();
            Page adminPage = adminContext.newPage();
            adminPage.navigate("https://www.pokerstars.com");
            System.out.println("Context 1 Title: " + adminPage.title());

            // 3. Create Context B (User: Visitor)
            // This acts like Incognito Window #2 - completely isolated from Context A
            BrowserContext visitorContext = browser.newContext();
            Page visitorPage = visitorContext.newPage();
            visitorPage.navigate("https://www.google.com");
            System.out.println("Context 2 Title: " + visitorPage.title());

            // 4. Architect Check: Confirm they are different
            System.out.println("We have 2 active contexts in 1 browser!");

            // Optional: Pause briefly to let you see the two windows
            // (Architect Note: We NEVER use Thread.sleep in real tests, only for learning/debugging)
            try { Thread.sleep(3000); } catch (InterruptedException e) {}
        }
    }
}