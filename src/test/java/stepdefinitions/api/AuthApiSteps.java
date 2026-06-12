package stepdefinitions.api;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Authentication API step definitions.
 * Covers 215527A login/token scenarios and 215552U unauthenticated-access scenario.
 */
public class AuthApiSteps extends ApiStepSupport {

    // ── Token acquisition steps ───────────────────────────────────────────────

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
        remember(ApiTestContext.context().auth.loginWithEmptyBody());
    }

    // ── Pre-condition steps ───────────────────────────────────────────────────

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

    @Given("I have no authentication token")
    public void noAuthToken() {
        System.out.println("[STEP] No authentication token will be sent");
    }

    // ── Assertion steps ───────────────────────────────────────────────────────

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

    @Then("the API response status should be {int}")
    public void theApiResponseStatusShouldBe(int expected) {
        int actual = ApiTestContext.context().lastResponse.status();
        assertEquals(actual, expected, "Expected HTTP " + expected + " but got " + actual);
        System.out.println("[PASS] Status is " + actual);
    }

    @Then("the unauthenticated response status should be 401")
    public void unauthenticatedResponseShouldBe401() {
        int status = ApiTestContext.context().lastResponse.status();
        assertEquals(status, 401, "Expected 401 but got " + status);
        System.out.println("[PASS] Endpoint is protected (401)");
    }

    // ── Unauthenticated access ────────────────────────────────────────────────

    @When("I get categories without authentication")
    public void getCategoriesNoAuth() {
        System.out.println("[STEP] GET /api/categories (no auth)...");
        remember(ApiTestContext.context().categories.getAllNoAuth());
        System.out.println("[INFO] Status: " + ApiTestContext.context().lastResponse.status());
    }
}
