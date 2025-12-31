import com.microsoft.playwright.*;

public class ShadowDomExample {
    public static void main(String[] args) {
        // Start Playwright
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));

            // Create a context and page
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // Navigate to the tricky page
            page.navigate("http://uitestingplayground.com/shadowdom");

            // --- THE MAGIC HAPPENS HERE ---
            // Playwright automatically looks inside Shadow DOMs.
            // No need to switch focus or use JavaScript executors.
            page.locator("#buttonGenerate").click();

            // Grab the text from the input field (also inside Shadow DOM)
            String guid = page.locator("#editField").inputValue();

            System.out.println("Success! Generated GUID: " + guid);

            // Keep browser open for 3 seconds so you can see it
            page.waitForTimeout(3000);

            browser.close();
        }
    }
}