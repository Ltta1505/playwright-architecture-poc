import com.automation.core.PlaywrightFactory;
import com.automation.core.AuthService;
import com.automation.pages.InventoryPage;
import com.microsoft.playwright.Page;

public class StrictPomTest {
    public static void main(String[] args) {
        Page page = PlaywrightFactory.getPage();

        page.navigate("https://www.saucedemo.com");
        AuthService.injectAuth(page.context());
        page.navigate("https://www.saucedemo.com/inventory.html");

        InventoryPage inventory = new InventoryPage(page);
        System.out.println("[PASS] STRICT POM SUCCESS: We are on the " + inventory.getHeaderText() + " page.");

        PlaywrightFactory.close();
    }
}