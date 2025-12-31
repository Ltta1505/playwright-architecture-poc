import com.microsoft.playwright.*;
import com.microsoft.playwright.options.ServiceWorkerPolicy;
import com.microsoft.playwright.options.WaitUntilState;

public class LoadStateTest {
    public static void main(String[] args) {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));

        // 1. Block Service Workers (Force Network)
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setServiceWorkers(ServiceWorkerPolicy.BLOCK));

        Page page = context.newPage();

        // Login
        page.navigate("https://www.saucedemo.com");
        page.fill("#user-name", "standard_user");
        page.fill("#password", "secret_sauce");
        page.locator("#login-button").click();


        // 2. LOOSE NAVIGATION (Simulating Selenium)
        // We tell Playwright: "Don't wait for the page to load. Just wait for the connection."
        // This will PASS immediately, just like driver.get() in Selenium often does.
        page.navigate("https://www.saucedemo.com/inventory.html",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.COMMIT));

        System.out.println("[INFO] Navigation 'finished' (URL changed). Now checking strict wait...");
        System.out.println("Current URL: " + page.url());

        page.waitForTimeout(3000);

        // 3. THE TRAP: Hang CSS
        page.route("**/*.css", route -> {
            System.out.println("[INFO] Hanging request: " + route.request().url());
        });

        System.out.println("[INFO] Navigating to Inventory...");

        System.out.println("[INFO] Reloading page one more time...");
        page.navigate("https://www.saucedemo.com/inventory.html",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.COMMIT));
        try {
            // 4. STRICT ASSERTION
            // This is where Playwright shines.
            // Even though we are on the URL, this line will BLOCK because the 'load' event is missing.
            page.waitForURL("**/inventory.html", new Page.WaitForURLOptions().setTimeout(5000));

            System.out.println("[FAIL] waitForURL succeeded? It should have timed out!");

        } catch (TimeoutError e) {
            System.out.println("--------------------------------------------------");
            System.out.println("[PASS] Playwright correctly FAILED at waitForURL!");
            System.out.println("       Navigation passed (URL changed), but the page was not ready.");
            System.out.println("--------------------------------------------------");
        }

        playwright.close();
    }
    public static void main1(String[] args) {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));

        // 1. ARCHITECTURAL CHANGE: Block Service Workers
        // We force the browser to go to the network for every file.
        // This ensures our network traps will actually catch the requests.
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setServiceWorkers(ServiceWorkerPolicy.ALLOW));

        Page page = context.newPage();

        // 2. Login
        page.navigate("https://www.saucedemo.com");
        page.fill("#user-name", "standard_user");
        page.fill("#password", "secret_sauce");
        page.locator("#login-button").click();

        // 3. THE TRAP: Hang ANY CSS file
        // We use a wildcard "**/*.css" to catch 'main.8a7d....css' or any other name.
        page.route("**/*.css", route -> {
            System.out.println("[INFO] Hanging request: " + route.request().url());
            // We do nothing here. The browser will wait forever for this file.
        });

        System.out.println("[INFO] Navigating to Inventory...");

        // 4. Trigger Navigation
        // We navigate to the same page (or reload) to trigger the css download again.
        page.navigate("https://www.saucedemo.com/inventory.html");

        System.out.println("Current URL is: " + page.url());

        try {
            // 5. The Playwright Assertion
            System.out.println("[INFO] Playwright: Waiting for URL + Load Event (Timeout 5s)...");

            // This should now FAIL because:
            // 1. We forced a network request (Service Worker Blocked).
            // 2. We intercepted that request.
            // 3. We left it hanging, so the 'load' event never fires.
            page.waitForURL("**/inventory.html", new Page.WaitForURLOptions().setTimeout(5000));

        } catch (TimeoutError e) {
            System.out.println("--------------------------------------------------");
            System.out.println("[PASS] Playwright correctly FAILED!");
            System.out.println("       The URL matches, but the page never finished loading.");
            System.out.println("--------------------------------------------------");
        }

        playwright.close();
    }
}