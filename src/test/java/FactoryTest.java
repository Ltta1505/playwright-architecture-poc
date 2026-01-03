import com.automation.core.PlaywrightFactory;
import com.microsoft.playwright.Page;

public class FactoryTest {
    public static void main(String[] args) {
        // 1. Get the page from the Factory
        Page page = PlaywrightFactory.getPage();

        // 2. Run your test
        page.navigate("https://www.google.com");
        System.out.println("Page Title: " + page.title());

        Page samePageTab = page.context().newPage();
        samePageTab.navigate("https://facebook.com/");

        // Get second page

        Page secondPage = PlaywrightFactory.getBrowser().newContext().newPage();

        secondPage.navigate("https://gmail.com/");

        // 3. Close it cleanly
        PlaywrightFactory.close();
    }
}