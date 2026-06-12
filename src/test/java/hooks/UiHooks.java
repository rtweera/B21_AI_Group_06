package hooks;


// import com.microsoft.playwright.*;
// import io.cucumber.java.After;
// import io.cucumber.java.Before;

// public class Hooks {

//     public static Playwright playwright;
//     public static Browser browser;
//     public static Page page;

//     @Before
//     public void setup() {
//         playwright = Playwright.create();

//         browser = playwright.chromium().launch(
//                 new BrowserType.LaunchOptions()
//                         .setHeadless(false)
//         );

//         page = browser.newPage();
//     }

//     @After
//     public void tearDown() {
//         if (page != null) page.close();
//         if (browser != null) browser.close();
//         if (playwright != null) playwright.close();

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.cucumber.java.After;
import io.cucumber.java.Before;

// Runs automatically before and after each test
public class UiHooks {

    public static Playwright playwright;
    public static Browser browser;
    public static Page page;

    @Before(value = "@UI", order = 2)
    public void setup() {
        playwright = Playwright.create();

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
        );

        page = browser.newPage();
    }

//    // Before any @UI test: open the browser
//    @Before("@UI")
//    public void setUp(Scenario scenario) {
//        System.out.println("\n=== STARTING: " + scenario.getName() + " ===");
//        PlaywrightFactory.getPage();
//        System.out.println("[SETUP] Browser opened");
//    }

    @After("@UI")
    public void tearDown() {
        if (page != null) page.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    // After any @UI test: screenshot if failed, then close browser
//    @After("@UI")
//    public void tearDown(Scenario scenario) {
//        if (scenario.isFailed()) {
//            Page page = PlaywrightFactory.getPage();
//            byte[] screenshot = page.screenshot();
//            scenario.attach(screenshot, "image/png", scenario.getName());
//            System.out.println("[TEARDOWN] FAILED - screenshot taken");
//        } else {
//            System.out.println("[TEARDOWN] PASSED");
//        }
//        PlaywrightFactory.closeBrowser();
//        System.out.println("[TEARDOWN] Browser closed\n");
//    }
}