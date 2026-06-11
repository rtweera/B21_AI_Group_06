package utils;

import com.microsoft.playwright.*;

public class PlaywrightFactory {

    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;
    private static Page page;
    private static APIRequestContext apiContext;   // NEW - for API tests

    // Get a browser tab (for UI tests)
    public static Page getPage() {
        if (page == null || page.isClosed()) {
            playwright = Playwright.create();
            browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false)
                            .setSlowMo(500)
            );
            context = browser.newContext();
            page = context.newPage();
        }
        return page;
    }

    // NEW - Get an API request tool (for API tests)
    public static APIRequestContext getApiContext() {
        if (apiContext == null) {
            if (playwright == null) {
                playwright = Playwright.create();
            }
            apiContext = playwright.request().newContext(
                    new APIRequest.NewContextOptions()
                            .setBaseURL("http://localhost:8080")
            );
        }
        return apiContext;
    }

    // Close browser
    public static void closeBrowser() {
        if (page != null && !page.isClosed()) { page.close(); page = null; }
        if (context != null) { context.close(); context = null; }
        if (browser != null) { browser.close(); browser = null; }
    }

    // NEW - Close API tool
    public static void closeApi() {
        if (apiContext != null) { apiContext.dispose(); apiContext = null; }
    }
}