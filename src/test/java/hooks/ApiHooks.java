package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import stepdefinitions.ApiTestContext;

public class ApiHooks {

    @Before(value = "@API", order = 0)
    public void initApiContext(Scenario scenario) {
        System.out.println("\n=== STARTING API: " + scenario.getName() + " ===");
        ApiTestContext.initContext();
    }

    @After(value = "@API", order = 0)
    public void destroyApiContext(Scenario scenario) {
        ApiTestContext.destroyContext();
        System.out.println("=== DONE API: " + scenario.getName() + " ===\n");
    }
}
