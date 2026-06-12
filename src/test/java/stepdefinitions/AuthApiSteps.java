package stepdefinitions;

import api.ApiClient;
import com.microsoft.playwright.APIResponse;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import utils.PlaywrightFactory;

import static org.testng.Assert.*;

// Steps for authentication-related API tests
public class AuthApiSteps {

    private ApiClient api;
    private APIResponse lastResponse;

    @Before("@API")
    public void initApi() {
        api = new ApiClient(PlaywrightFactory.getApiContext());
    }

    @Given("I have no authentication token")
    public void noAuthToken() {
        System.out.println("[STEP] Using no authentication token...");
        // We simply won't log in - api has no token
        System.out.println("[PASS] No token will be sent");
    }

    @When("I get categories without authentication")
    public void getCategoriesNoAuth() {
        System.out.println("[STEP] Getting categories with NO auth header...");
        lastResponse = api.getNoAuth("/api/categories");
        System.out.println("[INFO] Response status: " + lastResponse.status());
    }

    @Then("the unauthenticated response status should be 401")
    public void unauthenticatedResponseShouldBe401() {
        System.out.println("[STEP] Verifying status is 401...");
        assertEquals(lastResponse.status(), 401,
                "Expected 401 but got " + lastResponse.status());
        System.out.println("[PASS] Status is 401 - endpoint is protected");
    }
}