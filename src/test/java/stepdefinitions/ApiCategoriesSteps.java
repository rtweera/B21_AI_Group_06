package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class ApiCategoriesSteps extends ApiStepSupport {
    @Given("an admin-created category exists")
    public void anAdminCreatedCategoryExists() {
        createUniqueCategoryWithAdminToken();
        assertCreatedCategoryId();
    }

    @When("I request all categories with the admin token")
    public void iRequestAllCategoriesWithTheAdminToken() {
        remember(ApiTestContext.context().api.get("/api/categories", bearer(ApiTestContext.context().adminToken)));
    }

    @When("I request all categories with the normal user token")
    public void iRequestAllCategoriesWithTheNormalUserToken() {
        remember(ApiTestContext.context().api.get("/api/categories", bearer(ApiTestContext.context().userToken)));
    }

    @When("I create a unique category with the admin token")
    public void createUniqueCategoryWithAdminToken() {
        ApiTestContext.State state = ApiTestContext.context();
        state.createdCategoryName = uniqueCategoryName();
        remember(state.api.post("/api/categories",
                bearer(state.adminToken).setData(Map.of("name", state.createdCategoryName))));
        state.createdCategoryId = extractId();
    }

    @When("I request that category by id with the admin token")
    public void iRequestThatCategoryByIdWithTheAdminToken() {
        ApiTestContext.State state = ApiTestContext.context();
        remember(state.api.get("/api/categories/" + state.createdCategoryId, bearer(state.adminToken)));
    }

    @When("I create a category with a missing name using the admin token")
    public void iCreateACategoryWithAMissingNameUsingTheAdminToken() {
        remember(ApiTestContext.context().api.post("/api/categories",
                bearer(ApiTestContext.context().adminToken).setData(Map.of())));
    }

    @When("I create a category with the normal user token")
    public void iCreateACategoryWithTheNormalUserToken() {
        remember(ApiTestContext.context().api.post("/api/categories",
                bearer(ApiTestContext.context().userToken).setData(Map.of("name", "UsrCat"))));
    }

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

    private void assertCreatedCategoryId() {
        ApiTestContext.context().createdCategoryId = extractId();
        assertNotNull(ApiTestContext.context().createdCategoryId, "Expected created category id in response body");
        assertTrue(ApiTestContext.context().createdCategoryId > 0, "Expected positive category id");
    }
}
