package utils;

import com.microsoft.playwright.*;

// This class opens and closes the browser
public class PlaywrightFactory {

    private static Playwright playwright;   // the engine
    private static Browser browser;         // Chrome window
    private static BrowserContext context;   // a clean profile
    private static Page page;               // one tab

    // Get a browser tab (opens browser if not already open)
    public static Page getPage() {
        if (page == null || page.isClosed()) {
            playwright = Playwright.create();
            browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false)   // false = you can SEE the browser
                            .setSlowMo(500)       // slows it down so you can watch
            );
            context = browser.newContext();
            page = context.newPage();
        }
        return page;
    }

    // Close everything
    public static void closeBrowser() {
        if (page != null && !page.isClosed()) { page.close(); page = null; }
        if (context != null) { context.close(); context = null; }
        if (browser != null) { browser.close(); browser = null; }
        if (playwright != null) { playwright.close(); playwright = null; }
    }
}