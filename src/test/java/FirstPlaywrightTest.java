import com.microsoft.playwright.*;
import org.testng.annotations.Test;

public class FirstPlaywrightTest {

    @Test
    public void testPokerStarsLaunch() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(false)
            );
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            System.out.println("Navigating to PokerStars...");
            page.navigate("https://www.pokerstars.com");

            System.out.println("Page Title is: " + page.title());

            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(java.nio.file.Paths.get("pokerstars_home.png")));
        }
    }
}