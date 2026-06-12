package hooks;

import com.microsoft.playwright.Page;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import utils.PlaywrightFactory;

public class UiHooks {

    public static Page page;

    @Before(value = "@UI", order = 0)
    public void openBrowser(Scenario scenario) {
        System.out.println("\n=== STARTING UI: " + scenario.getName() + " ===");
        page = PlaywrightFactory.getPage();
    }

    @After(value = "@UI", order = 0)
    public void closeBrowser(Scenario scenario) {
        if (scenario.isFailed() && page != null && !page.isClosed()) {
            byte[] screenshot = page.screenshot();
            scenario.attach(screenshot, "image/png", scenario.getName());
            System.out.println("[TEARDOWN] FAILED - screenshot attached");
        }
        PlaywrightFactory.closeBrowser();
        page = null;
        System.out.println("=== DONE UI: " + scenario.getName() + " ===\n");
    }
}
