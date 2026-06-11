package hooks;

import com.microsoft.playwright.Page;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import utils.PlaywrightFactory;

// Runs automatically before and after each test
public class Hooks {

    // Before any @UI test: open the browser
    @Before("@UI")
    public void setUp(Scenario scenario) {
        System.out.println("\n=== STARTING: " + scenario.getName() + " ===");
        PlaywrightFactory.getPage();
        System.out.println("[SETUP] Browser opened");
    }

    // After any @UI test: screenshot if failed, then close browser
    @After("@UI")
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            Page page = PlaywrightFactory.getPage();
            byte[] screenshot = page.screenshot();
            scenario.attach(screenshot, "image/png", scenario.getName());
            System.out.println("[TEARDOWN] FAILED - screenshot taken");
        } else {
            System.out.println("[TEARDOWN] PASSED");
        }
        PlaywrightFactory.closeBrowser();
        System.out.println("[TEARDOWN] Browser closed\n");
    }

    // Before any @API test: set up the API tool
    @Before("@API")
    public void setUpApi(Scenario scenario) {
        System.out.println("\n=== STARTING: " + scenario.getName() + " ===");
        utils.PlaywrightFactory.getApiContext();
        System.out.println("[SETUP] API context ready");
    }

    // After any @API test: clean up
    @After("@API")
    public void tearDownApi(Scenario scenario) {
        if (scenario.isFailed()) {
            System.out.println("[TEARDOWN] FAILED");
        } else {
            System.out.println("[TEARDOWN] PASSED");
        }
        utils.PlaywrightFactory.closeApi();
        System.out.println("[TEARDOWN] API closed\n");
    }
}