package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.testng.Assert.assertEquals;

public class AuthApiSteps extends ApiStepSupport {

    @Given("I have no authentication token")
    public void noAuthToken() {
        System.out.println("[STEP] No authentication token will be sent");
    }

    @When("I get categories without authentication")
    public void getCategoriesNoAuth() {
        System.out.println("[STEP] GET /api/categories (no auth)...");
        remember(ApiTestContext.context().api.get("/api/categories"));
        System.out.println("[INFO] Status: " + ApiTestContext.context().lastResponse.status());
    }

    @Then("the unauthenticated response status should be 401")
    public void unauthenticatedResponseShouldBe401() {
        int status = ApiTestContext.context().lastResponse.status();
        assertEquals(status, 401, "Expected 401 but got " + status);
        System.out.println("[PASS] Endpoint is protected (401)");
    }
}
