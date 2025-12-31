import com.automation.core.PlaywrightFactory;
import com.automation.pages.InventoryPage;
import com.microsoft.playwright.Page;

public class StrictPomFailureTest {
    public static void main(String[] args) {
        Page page = PlaywrightFactory.getPage();

        // 1. Go to the WRONG page (Login screen)
        System.out.println("Navigating to Login Page (Wrong page for InventoryPage)...");
        page.navigate("https://www.saucedemo.com/");

        try {
            // 2. Try to create the Object. This SHOULD fail immediately.
            System.out.println("Attempting to create InventoryPage object...");

            // This line calls: InventoryPage -> BasePage -> validate()
            InventoryPage wrongPage = new InventoryPage(page);

            // 3. If we reach this line, your Framework is BROKEN.
            System.err.println("[FAIL] Critical Error: The object was created despite being on the wrong page!");

        } catch (IllegalStateException e) {
            // 4. If we land here, your Framework is WORKING.
            System.out.println("------------------------------------------------");
            System.out.println("[PASS] GREAT! The test failed fast as expected.");
            System.out.println("       Error Message caught: " + e.getMessage());
            System.out.println("------------------------------------------------");
        } finally {
            PlaywrightFactory.close();
        }
    }
}