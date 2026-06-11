package stepdefinitions;

import api.ApiClient;
import com.microsoft.playwright.APIResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import utils.PlaywrightFactory;

import static org.testng.Assert.*;

// Steps for plant-related API tests
public class PlantsApiSteps {

    private ApiClient api;
    private APIResponse lastResponse;

    @Before("@API")
    public void initApi() {
        api = new ApiClient(PlaywrightFactory.getApiContext());
    }

    @Given("I am authenticated as user for plants")
    public void authAsUserForPlants() {
        System.out.println("[STEP] Authenticating as user via API...");
        String token = api.login("testuser", "test123");
        assertNotNull(token, "User token should not be null");
        System.out.println("[PASS] Got user token");
    }

    @When("I get plants for category id {int}")
    public void getPlantsForCategory(int categoryId) {
        System.out.println("[STEP] Getting plants for category " + categoryId + "...");
        lastResponse = api.get("/api/plants/category/" + categoryId);
        System.out.println("[INFO] Response status: " + lastResponse.status());
    }

    @Then("the plants response status should be {int}")
    public void plantsResponseShouldBe(int expected) {
        System.out.println("[STEP] Verifying status is " + expected + "...");
        assertEquals(lastResponse.status(), expected,
                "Expected " + expected + " but got " + lastResponse.status());
        System.out.println("[PASS] Status is " + expected);
    }

    @And("all returned plants should belong to category {int}")
    public void allPlantsBelongToCategory(int categoryId) {
        System.out.println("[STEP] Verifying all plants belong to category " + categoryId + "...");
        String body = lastResponse.text();

        // The response is a JSON array of plants
        JsonArray plants = JsonParser.parseString(body).getAsJsonArray();
        int count = plants.size();

        System.out.println("[INFO] ===== Plants returned =====");
        for (int i = 0; i < count; i++) {
            JsonObject plant = plants.get(i).getAsJsonObject();
            String name = plant.get("name").getAsString();
            // Each plant has a nested "category" object with its id
            int plantCategoryId = plant.getAsJsonObject("category").get("id").getAsInt();
            String catName = plant.getAsJsonObject("category").get("name").getAsString();
            System.out.println("[INFO]   " + (i + 1) + ". " + name + " (category: " + catName + ", id=" + plantCategoryId + ")");

            // Verify this plant's category matches the one we filtered by
            assertEquals(plantCategoryId, categoryId,
                    "Plant " + name + " should be in category " + categoryId + " but was in " + plantCategoryId);
        }
        System.out.println("[INFO] ===========================");
        System.out.println("[PASS] All " + count + " plants belong to category " + categoryId);
    }

    @When("I get plants sorted by price ascending")
    public void getPlantsSortedByPrice() {
        System.out.println("[STEP] Getting plants sorted by price asc...");
        lastResponse = api.get("/api/plants/paged?sort=price,asc");
        System.out.println("[INFO] Response status: " + lastResponse.status());
    }

    @And("the plants should be ordered by price ascending")
    public void plantsOrderedByPriceAscending() {
        System.out.println("[STEP] Verifying plants are sorted by price ascending...");
        String body = lastResponse.text();
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        assertTrue(json.has("content"), "Response should have a content array");

        JsonArray plants = json.getAsJsonArray("content");
        int count = plants.size();

        System.out.println("[INFO] ===== Plants (sorted by price) =====");
        double previousPrice = -1;
        for (int i = 0; i < count; i++) {
            JsonObject plant = plants.get(i).getAsJsonObject();
            String name = plant.get("name").getAsString();
            double price = plant.get("price").getAsDouble();
            System.out.println("[INFO]   " + (i + 1) + ". " + name + " - price: " + price);

            // Each price should be >= the previous one (ascending order)
            if (previousPrice >= 0) {
                assertTrue(price >= previousPrice,
                        "Prices not ascending: " + price + " came after " + previousPrice);
            }
            previousPrice = price;
        }
        System.out.println("[INFO] ====================================");
        System.out.println("[PASS] Plants are sorted by price ascending");
    }
}