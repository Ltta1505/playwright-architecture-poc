package com.automation.core;

import com.microsoft.playwright.*;

public class PlaywrightFactory {

    // ThreadLocal storage for parallel execution safety
    private static final ThreadLocal<Playwright> threadPlaywright = new ThreadLocal<>();
    private static final ThreadLocal<Browser> threadBrowser = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> threadContext = new ThreadLocal<>();
    private static final ThreadLocal<Page> threadPage = new ThreadLocal<>();

    /**
     * STANDARD MODE: Returns the thread-local singleton Page.
     * Use this for 95% of tests (Simple linear flows).
     */
    public static Page getPage() {
        if (threadPlaywright.get() == null) {
            createPlaywright();
        }
        if (threadBrowser.get() == null) {
            createBrowser();
        }
        if (threadContext.get() == null) {
            createContext();
        }
        if (threadPage.get() == null) {
            threadPage.set(threadContext.get().newPage());
        }
        return threadPage.get();
    }

    /**
     * ADVANCED MODE: Returns the Browser instance.
     * Use this for "Chat" tests where you need to create MULTIPLE contexts manually.
     * Example:
     * Browser browser = PlaywrightFactory.getBrowser();
     * BrowserContext admin = browser.newContext();
     * BrowserContext user = browser.newContext();
     */
    public static Browser getBrowser() {
        if (threadPlaywright.get() == null) {
            createPlaywright();
        }
        if (threadBrowser.get() == null) {
            createBrowser();
        }
        return threadBrowser.get();
    }

    // --- Private Initialization Methods ---

    private static void createPlaywright() {
        threadPlaywright.set(Playwright.create());
    }

    private static void createBrowser() {
        // In Week 4, we will read this from a config file (Headless=true/false)
        threadBrowser.set(threadPlaywright.get().chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
        ));
    }

    private static void createContext() {
        threadContext.set(threadBrowser.get().newContext());
    }

    // --- Cleanup ---

    public static void close() {
        // Close Page
        if (threadPage.get() != null) {
            threadPage.get().close();
            threadPage.remove();
        }
        // Close Context
        if (threadContext.get() != null) {
            threadContext.get().close();
            threadContext.remove();
        }
        // Close Browser
        if (threadBrowser.get() != null) {
            threadBrowser.get().close();
            threadBrowser.remove();
        }
        // Close Playwright
        if (threadPlaywright.get() != null) {
            threadPlaywright.get().close();
            threadPlaywright.remove();
        }
    }
}