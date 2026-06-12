package utils;

import com.microsoft.playwright.*;

public class PlaywrightFactory {

    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;
    private static Page page;
    private static APIRequestContext apiContext;

    public static Page getPage() {
        if (page == null || page.isClosed()) {
            if (playwright == null) playwright = Playwright.create();
            browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(ConfigReader.isHeadless())
                            .setSlowMo(500));
            context = browser.newContext();
            page = context.newPage();
        }
        return page;
    }

    public static APIRequestContext getApiContext() {
        if (apiContext == null) {
            if (playwright == null) playwright = Playwright.create();
            apiContext = playwright.request().newContext(
                    new APIRequest.NewContextOptions()
                            .setBaseURL(ConfigReader.getBaseUrl()));
        }
        return apiContext;
    }

    public static void closeBrowser() {
        if (page != null && !page.isClosed()) { page.close(); }
        page = null;
        if (context != null) { context.close(); context = null; }
        if (browser != null) { browser.close(); browser = null; }
        if (playwright != null) { playwright.close(); playwright = null; }
    }

    public static void closeApi() {
        if (apiContext != null) { apiContext.dispose(); apiContext = null; }
        if (playwright != null && browser == null) { playwright.close(); playwright = null; }
    }
}
