package com.automation.pages;

import com.microsoft.playwright.Page;

public abstract class BasePage {
    protected Page page;

    public BasePage(Page page) {
        this.page = page;
        validate();
    }

    public abstract void validate();

    protected void assertUrlContains(String partialUrl) {
        try {
            page.waitForURL("**" + partialUrl, new Page.WaitForURLOptions().setTimeout(2000));
        } catch (Exception e) {
            throw new IllegalStateException(
                    "[FAIL] STRICT POM ERROR: Expected to be on a page containing '" + partialUrl +
                            "', but current URL is: " + page.url()
            );
        }
    }
}