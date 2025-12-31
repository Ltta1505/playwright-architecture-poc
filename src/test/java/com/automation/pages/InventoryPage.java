package com.automation.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;

public class InventoryPage extends BasePage {

    // Locators
    private final Locator productHeader;

    public InventoryPage(Page page) {
        super(page); // This triggers validate() automatically!
        this.productHeader = page.locator(".title");
    }

    @Override
    public void validate() {
        // If this check fails, the test crashes immediately (Good thing!)
        assertUrlContains("/inventory.html");
    }

    public String getHeaderText() {
        return productHeader.textContent();
    }
}