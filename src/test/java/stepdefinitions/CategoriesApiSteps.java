package stepdefinitions;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.options.RequestOptions;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.testng.Assert.assertTrue;

public class CategoriesApiSteps extends ApiStepSupport {

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
        remember(s.api.get(
                "/api/categories/page?page=" + pageNum + "&size=" + size,
                RequestOptions.create().setHeader("Authorization", "Bearer " + s.activeToken)));
        System.out.println("[INFO] Status: " + s.lastResponse.status());
    }

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
}
