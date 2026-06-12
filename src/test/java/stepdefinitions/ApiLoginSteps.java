package stepdefinitions;

import com.microsoft.playwright.options.RequestOptions;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class ApiLoginSteps extends ApiStepSupport {
    @When("I request an admin auth token")
    public void iRequestAnAdminAuthToken() {
        remember(login(adminUsername(), adminPassword()));
        ApiTestContext.context().adminToken = extractToken();
    }

    @When("I request a normal user auth token")
    public void iRequestANormalUserAuthToken() {
        remember(login(userUsername(), userPassword()));
        ApiTestContext.context().userToken = extractToken();
    }

    @When("I request a normal user auth token with password {string}")
    public void iRequestANormalUserAuthTokenWithPassword(String password) {
        remember(login(userUsername(), password));
    }

    @When("I request an auth token with an empty body")
    public void iRequestAnAuthTokenWithAnEmptyBody() {
        remember(ApiTestContext.context().api.post("/api/auth/login", RequestOptions.create().setData(Map.of())));
    }

    @Given("I have an admin API token")
    public void iHaveAnAdminApiToken() {
        remember(login(adminUsername(), adminPassword()));
        ApiTestContext.context().adminToken = extractToken();
        ApiTestContext.context().activeToken = ApiTestContext.context().adminToken;
        assertNotNull(ApiTestContext.context().adminToken, "Expected admin auth token");
    }

    @Given("I have a normal user API token")
    public void iHaveANormalUserApiToken() {
        remember(login(userUsername(), userPassword()));
        ApiTestContext.context().userToken = extractToken();
        ApiTestContext.context().activeToken = ApiTestContext.context().userToken;
        assertNotNull(ApiTestContext.context().userToken, "Expected normal user auth token");
    }

    @Then("the API response should contain an auth token")
    public void theApiResponseShouldContainAnAuthToken() {
        assertNotNull(extractToken(), "Expected auth token in response body");
    }

    @Then("the API response should contain an unauthorized error")
    public void theApiResponseShouldContainAnUnauthorizedError() {
        String body = lastBody().toLowerCase();
        assertTrue(body.contains("unauthorized") || body.contains("401"),
                "Expected unauthorized response body, but was: " + body);
    }
}
