import com.microsoft.playwright.*;
import com.microsoft.playwright.options.ServiceWorkerPolicy;

import java.util.concurrent.atomic.AtomicInteger;

public class ServiceWorkerTest {

    public static void main(String[] args) {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));

        System.out.println("==========================================");
        System.out.println(" EXPERIMENT 1: Service Workers ALLOWED");
        System.out.println("   (Standard User Experience)");
        System.out.println("==========================================");
        runTest(browser, ServiceWorkerPolicy.ALLOW);

        System.out.println("\n\n");

        System.out.println("==========================================");
        System.out.println(" EXPERIMENT 2: Service Workers BLOCKED");
        System.out.println("   (Automation Architect Standard)");
        System.out.println("==========================================");
        runTest(browser, ServiceWorkerPolicy.BLOCK);

        browser.close();
        playwright.close();
    }

    private static void runTest(Browser browser, ServiceWorkerPolicy policy) {
        // 1. Create Context with the specific Policy
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setServiceWorkers(policy));
        Page page = context.newPage();

        // Counter to track how many times we catch the CSS file
        AtomicInteger networkHits = new AtomicInteger(0);

        try {
            // 2. Initial Navigation (Prime the Cache)
            // We go to the site so the Service Worker has a chance to install.
            System.out.println("[INFO] Visit 1: Navigating to populate cache...");
            page.navigate("https://www.saucedemo.com");

            // Wait a moment for the Service Worker to register and activate
            // (In real life, this happens silently in the background)
            page.waitForTimeout(3000);

            // 3. Set the Network Trap
            // Now we listen. If the browser goes to the network, we increment the counter.
            page.route("**/main.*.css", route -> {
                System.out.println("   >>> TRAP TRIGGERED! Network request detected.");
                networkHits.incrementAndGet();
                route.resume();
            });

            // 4. The Critical Step: RELOAD
            // If SW is active, this reload should pull from Cache (bypassing our trap).
            // If SW is blocked, this reload MUST go to the Network (triggering our trap).
            System.out.println("[INFO] Visit 2: Reloading page...");
            page.reload();

            // Give it a second to finish requests
            page.waitForTimeout(2000);

            // 5. Results
            System.out.println("------------------------------------------");
            if (networkHits.get() == 0) {
                System.out.println("[RESULT] Network Hits: 0");
                System.out.println("         The Service Worker served the file.");
                System.out.println("         Our network trap was BYPASSED.");
            } else {
                System.out.println("[RESULT] Network Hits: " + networkHits.get());
                System.out.println("         The browser used the network.");
                System.out.println("         Our trap worked perfectly.");
            }
            System.out.println("------------------------------------------");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            context.close();
        }
    }
}