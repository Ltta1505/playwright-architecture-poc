import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
// THIS IMPORT IS CRITICAL for the "smart" assertions
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class Week1_Assertions {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();

            // 1. Navigate to the AJAX simulation page
            page.navigate("http://uitestingplayground.com/ajax");

            // 2. Click the button that triggers a slow request (approx 15 seconds)
            page.locator("#ajaxButton").click();

            System.out.println("Button clicked. Waiting for lazy text to appear...");

            // 3. THE MAGIC: Auto-Retrying Assertion
            // A standard "Assert.assertEquals()" would fail here immediately.
            // Playwright's "assertThat" will sit here and retry this check repeatedly
            // until the text actually appears on the screen.
//            assertThat(page.locator(".bg-success")).hasText("Data loaded with AJAX get request.");
            // Increase timeout to 20,000ms (20 seconds) for this specific check
            assertThat(page.locator(".bg-success")).hasText("Data loaded with AJAX get request.", new LocatorAssertions.HasTextOptions().setTimeout(20000));
            System.out.println("Success! The assertion waited for us automatically.");

            browser.close();
        }
    }
}