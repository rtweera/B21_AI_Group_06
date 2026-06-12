package stepdefinitions;

import api.ApiClient;
import com.microsoft.playwright.APIResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import utils.PlaywrightFactory;

import static org.testng.Assert.*;

// Steps for category-related API tests
public class CategoriesApiSteps {

    private ApiClient api;
    private APIResponse lastResponse;

    @Before("@API")
    public void initApi() {
        api = new ApiClient(PlaywrightFactory.getApiContext());
    }

    @Given("I am authenticated as user for categories")
    public void authAsUserForCategories() {
        System.out.println("[STEP] Authenticating as user via API...");
        String token = api.login("testuser", "test123");
        System.out.println("[DEBUG] Token received: " + token);
        assertNotNull(token, "User token should not be null");
        System.out.println("[PASS] Got user token");
    }

    @When("I get categories with page {int} and size {int}")
    public void getCategoriesPaged(int pageNum, int size) {
        System.out.println("[STEP] Getting categories page=" + pageNum + " size=" + size + "...");
        lastResponse = api.get("/api/categories/page?page=" + pageNum + "&size=" + size);
        System.out.println("[INFO] Response status: " + lastResponse.status());
    }

    @Then("the categories response status should be {int}")
    public void categoriesResponseShouldBe(int expected) {
        System.out.println("[STEP] Verifying status is " + expected + "...");
        assertEquals(lastResponse.status(), expected,
                "Expected " + expected + " but got " + lastResponse.status());
        System.out.println("[PASS] Status is " + expected);
    }

    @And("the response should contain at most {int} categories")
    public void responseContainsAtMost(int maxItems) {
        System.out.println("[STEP] Verifying at most " + maxItems + " categories returned...");
        String body = lastResponse.text();
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();

        // Check the content array
        assertTrue(json.has("content"), "Response should have a content array");
        var content = json.getAsJsonArray("content");
        int count = content.size();

        // Print each category so we can see them in the terminal
        System.out.println("[INFO] ===== Categories returned =====");
        for (int i = 0; i < count; i++) {
            JsonObject category = content.get(i).getAsJsonObject();
            int id = category.get("id").getAsInt();
            String name = category.get("name").getAsString();
            System.out.println("[INFO]   " + (i + 1) + ". id=" + id + ", name=" + name);
        }
        System.out.println("[INFO] ===============================");

        assertTrue(count <= maxItems, "Expected at most " + maxItems + " but got " + count);

        // Print and check pagination metadata
        int totalElements = json.get("totalElements").getAsInt();
        int totalPages = json.get("totalPages").getAsInt();
        int size = json.get("size").getAsInt();
        int pageNumber = json.get("number").getAsInt();

        System.out.println("[INFO] Pagination: totalElements=" + totalElements
                + ", totalPages=" + totalPages
                + ", size=" + size
                + ", currentPage=" + pageNumber);

        assertTrue(json.has("totalElements"), "Should have totalElements");
        assertTrue(json.has("totalPages"), "Should have totalPages");
        assertTrue(json.has("size"), "Should have size");

        System.out.println("[PASS] Returned " + count + " categories (<= " + maxItems + ") with pagination data");
    }
}