package com.automation.core;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.options.Cookie;
import java.util.ArrayList;
import java.util.List;

public class AuthService {

    public static void injectAuth(BrowserContext context) {
        // 1. Create the cookie
        Cookie sessionCookie = new Cookie("session-username", "standard_user");

        // 2. THE FIX: Explicitly set Domain and Path (Remove setUrl)
        // This tells the browser: "This cookie belongs strictly to www.saucedemo.com"
        sessionCookie.setDomain("www.saucedemo.com");
        sessionCookie.setPath("/");

        // 3. Add to list
        List<Cookie> cookies = new ArrayList<>();
        cookies.add(sessionCookie);

        // 4. Inject
        context.addCookies(cookies);

        System.out.println("💉 Security Token Injected: Login Bypassed.");
    }
}