package stepdefinitions.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Categories API step definitions.
 * Covers 215527A CRUD scenarios and 215552U paged-categories scenario.
 */
public class CategoryApiSteps extends ApiStepSupport {

    // ── Pre-conditions (215527A) ──────────────────────────────────────────────

    @Given("an admin-created category exists")
    public void anAdminCreatedCategoryExists() {
        createUniqueCategoryWithAdminToken();
        assertCreatedCategoryId();
    }

    // ── When steps (215527A) ──────────────────────────────────────────────────

    @When("I request all categories with the admin token")
    public void iRequestAllCategoriesWithTheAdminToken() {
        ApiTestContext.State s = ApiTestContext.context();
        remember(s.categories.getAll(s.adminToken));
    }

    @When("I request all categories with the normal user token")
    public void iRequestAllCategoriesWithTheNormalUserToken() {
        ApiTestContext.State s = ApiTestContext.context();
        remember(s.categories.getAll(s.userToken));
    }

    @When("I create a unique category with the admin token")
    public void createUniqueCategoryWithAdminToken() {
        ApiTestContext.State state = ApiTestContext.context();
        state.createdCategoryName = uniqueCategoryName();
        remember(state.categories.create(state.createdCategoryName, state.adminToken));
        state.createdCategoryId = extractId();
    }

    @When("I request that category by id with the admin token")
    public void iRequestThatCategoryByIdWithTheAdminToken() {
        ApiTestContext.State state = ApiTestContext.context();
        remember(state.categories.getById(state.createdCategoryId, state.adminToken));
    }

    @When("I create a category with a missing name using the admin token")
    public void iCreateACategoryWithAMissingNameUsingTheAdminToken() {
        ApiTestContext.State state = ApiTestContext.context();
        remember(state.categories.createWithBody(Map.of(), state.adminToken));
    }

    @When("I create a category with the normal user token")
    public void iCreateACategoryWithTheNormalUserToken() {
        ApiTestContext.State state = ApiTestContext.context();
        remember(state.categories.create("UsrCat", state.userToken));
    }

    // ── When steps (215552U) ──────────────────────────────────────────────────

    @Given("I am authenticated as user for categories")
    public void authAsUserForCategories() {
        System.out.println("[STEP] Authenticating as user...");
        remember(login(userUsername(), userPassword()));
        ApiTestContext.context().userToken = extractToken();
        ApiTestContext.context().activeToken = ApiTestContext.context().userToken;
        System.out.println("[PASS] Got user token");
    }

    @When("I get categories with page {int} and size {int}")
    public void getCategoriesPaged(int pageNum, int size) {
        System.out.println("[STEP] GET /api/categories/page?page=" + pageNum + "&size=" + size);
        ApiTestContext.State s = ApiTestContext.context();
        remember(s.categories.getPaged(pageNum, size, s.activeToken));
        System.out.println("[INFO] Status: " + s.lastResponse.status());
    }

    // ── Then steps (215527A) ──────────────────────────────────────────────────

    @Then("the API response should contain a category list")
    public void theApiResponseShouldContainACategoryList() {
        String body = lastBody();
        assertTrue(body.contains("[") || body.contains("\"content\"") || body.contains("\"name\""),
                "Expected category list in response body, but was: " + body);
    }

    @Then("the API response should contain the created category id")
    public void theApiResponseShouldContainTheCreatedCategoryId() {
        assertCreatedCategoryId();
    }

    @Then("the API response should contain the requested category")
    public void theApiResponseShouldContainTheRequestedCategory() {
        ApiTestContext.State state = ApiTestContext.context();
        assertTrue(lastBody().contains(String.valueOf(state.createdCategoryId)),
                "Expected response to contain category id: " + state.createdCategoryId);
        assertTrue(lastBody().contains(state.createdCategoryName),
                "Expected response to contain category name: " + state.createdCategoryName);
    }

    @Then("the API response should contain a validation error")
    public void theApiResponseShouldContainAValidationError() {
        String body = lastBody();
        assertTrue(body.contains("Validation") || body.contains("mandatory") || body.contains("BAD_REQUEST"),
                "Expected validation error body, but was: " + body);
    }

    @Then("the API response should contain a forbidden error")
    public void theApiResponseShouldContainAForbiddenError() {
        String body = lastBody().toLowerCase();
        assertTrue(body.contains("forbidden") || body.contains("403"),
                "Expected forbidden response body, but was: " + body);
    }

    // ── Then steps (215552U) ──────────────────────────────────────────────────

    @Then("the categories response status should be {int}")
    public void categoriesResponseShouldBe(int expected) {
        int status = ApiTestContext.context().lastResponse.status();
        assertTrue(status == expected, "Expected " + expected + " but got " + status);
        System.out.println("[PASS] Status is " + status);
    }

    @And("the response should contain at most {int} categories")
    public void responseContainsAtMost(int maxItems) {
        String body = lastBody();
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        assertTrue(json.has("content"), "Response should have a content array");
        int count = json.getAsJsonArray("content").size();
        System.out.println("[INFO] Categories returned: " + count);
        assertTrue(count <= maxItems, "Expected at most " + maxItems + " but got " + count);
        assertTrue(json.has("totalElements"), "Should have totalElements");
        System.out.println("[PASS] " + count + " categories (<= " + maxItems + ")");
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void assertCreatedCategoryId() {
        ApiTestContext.context().createdCategoryId = extractId();
        assertNotNull(ApiTestContext.context().createdCategoryId, "Expected created category id in response body");
        assertTrue(ApiTestContext.context().createdCategoryId > 0, "Expected positive category id");
    }
}
