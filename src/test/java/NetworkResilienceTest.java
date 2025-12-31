import com.automation.core.PlaywrightFactory;
import com.automation.core.AuthService;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;

public class NetworkResilienceTest {
    public static void main(String[] args) {
        Page page = PlaywrightFactory.getPage();

        // 1. Setup
        page.navigate("https://www.saucedemo.com");
        AuthService.injectAuth(page.context());
        page.navigate("https://www.saucedemo.com/checkout-step-two.html");

        System.out.println("Wait! About to click Finish...");

        // 2. THE SURGEON'S KNIFE: The Zombie Switch
        // We find the button, make a clone (which has NO events), and replace the original.
        // This is 100% effective against ANY framework (React, Angular, Vue).
        page.evaluate("var oldBtn = document.querySelector('#finish'); " +
                "var newBtn = oldBtn.cloneNode(true); " +
                "oldBtn.parentNode.replaceChild(newBtn, oldBtn);");

        // 3. Trigger the Action
        System.out.println("Attempting to click the Zombie button...");

        // We force click because a 'Zombie' button might be considered 'non-interactive' by some strict rules,
        // though usually, it's fine. We use force just to be safe.
        page.locator("#finish").click(new Locator.ClickOptions().setForce(true));

        // 4. Verification
        page.waitForTimeout(1000);

        if (page.url().contains("checkout-step-two.html")) {
            System.out.println("[PASS] Graceful Failure: User remained on page (Button was dead).");
        } else {
            System.out.println("[FAIL] The page navigated! The sabotage failed.");
        }

        PlaywrightFactory.close();
    }
}