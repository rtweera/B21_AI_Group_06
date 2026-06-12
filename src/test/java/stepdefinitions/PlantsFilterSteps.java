package stepdefinitions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.options.RequestOptions;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.testng.Assert.*;

/**
 * Step definitions for 215552U Plants API filter/sort scenarios
 * (api/plants_api.feature: API_GET_PLT_USR_004, API_GET_PLT_USR_005).
 */
public class PlantsFilterSteps extends ApiStepSupport {

    @Given("I am authenticated as user for plants")
    public void authAsUserForPlants() {
        System.out.println("[STEP] Authenticating as user for plants...");
        remember(login(userUsername(), userPassword()));
        ApiTestContext.context().userToken = extractToken();
        ApiTestContext.context().activeToken = ApiTestContext.context().userToken;
        assertNotNull(ApiTestContext.context().userToken, "User token should not be null");
        System.out.println("[PASS] Got user token");
    }

    @When("I get plants for category id {int}")
    public void getPlantsForCategory(int categoryId) {
        System.out.println("[STEP] GET /api/plants/category/" + categoryId);
        ApiTestContext.State s = ApiTestContext.context();
        remember(s.api.get(
                "/api/plants/category/" + categoryId,
                RequestOptions.create().setHeader("Authorization", "Bearer " + s.activeToken)));
        System.out.println("[INFO] Status: " + s.lastResponse.status());
    }

    @Then("the plants response status should be {int}")
    public void plantsResponseStatusShouldBe(int expected) {
        int actual = ApiTestContext.context().lastResponse.status();
        assertEquals(actual, expected, "Expected HTTP " + expected + " but got " + actual);
        System.out.println("[PASS] Plants status is " + actual);
    }

    @And("all returned plants should belong to category {int}")
    public void allPlantsShouldBelongToCategory(int categoryId) {
        String body = lastBody();
        JsonElement parsed = JsonParser.parseString(body);
        JsonArray plants = parsed.isJsonArray()
                ? parsed.getAsJsonArray()
                : parsed.getAsJsonObject().getAsJsonArray("content");

        assertNotNull(plants, "Expected a plant list in the response body");
        System.out.println("[INFO] Plants returned: " + plants.size());

        for (JsonElement el : plants) {
            JsonObject plant = el.getAsJsonObject();
            int plantCategoryId = -1;
            if (plant.has("category") && !plant.get("category").isJsonNull()) {
                plantCategoryId = plant.getAsJsonObject("category").get("id").getAsInt();
            } else if (plant.has("categoryId")) {
                plantCategoryId = plant.get("categoryId").getAsInt();
            }
            assertEquals(plantCategoryId, categoryId,
                    "Plant '" + plant.get("name").getAsString() + "' belongs to category "
                            + plantCategoryId + ", not " + categoryId);
        }
        System.out.println("[PASS] All plants belong to category " + categoryId);
    }

    @When("I get plants sorted by price ascending")
    public void getPlantsortedByPriceAsc() {
        System.out.println("[STEP] GET /api/plants/paged?sort=price,asc");
        ApiTestContext.State s = ApiTestContext.context();
        remember(s.api.get(
                "/api/plants/paged?sort=price,asc",
                RequestOptions.create().setHeader("Authorization", "Bearer " + s.activeToken)));
        System.out.println("[INFO] Status: " + s.lastResponse.status());
    }

    @Then("the plants should be ordered by price ascending")
    public void plantsShouldBeOrderedByPriceAsc() {
        String body = lastBody();
        JsonElement parsed = JsonParser.parseString(body);
        JsonArray plants = parsed.isJsonArray()
                ? parsed.getAsJsonArray()
                : parsed.getAsJsonObject().getAsJsonArray("content");

        assertNotNull(plants, "Expected a plant list");
        double prevPrice = Double.MIN_VALUE;
        for (JsonElement el : plants) {
            double price = el.getAsJsonObject().get("price").getAsDouble();
            assertTrue(price >= prevPrice,
                    "Plants are not sorted by price ascending: " + price + " < " + prevPrice);
            prevPrice = price;
        }
        System.out.println("[PASS] Plants are sorted by price ascending");
    }
}
