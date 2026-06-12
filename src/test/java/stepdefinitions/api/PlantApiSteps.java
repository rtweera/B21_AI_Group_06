package stepdefinitions.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIResponse;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.testng.Assert.*;

/**
 * Plants API step definitions.
 * Covers 215565L plant CRUD + sale scenarios and 215552U plant filter/sort scenarios.
 */
public class PlantApiSteps extends ApiStepSupport {

    private String uniquePlantName() {
        String suffix = Long.toString(System.nanoTime(), 36);
        return "P_" + suffix.substring(Math.max(0, suffix.length() - 6));
    }

    // ── Pre-conditions ────────────────────────────────────────────────────────

    @Given("I am authenticated as user for plants")
    public void authAsUserForPlants() {
        System.out.println("[STEP] Authenticating as user for plants...");
        remember(login(userUsername(), userPassword()));
        ApiTestContext.context().userToken = extractToken();
        ApiTestContext.context().activeToken = ApiTestContext.context().userToken;
        assertNotNull(ApiTestContext.context().userToken, "User token should not be null");
        System.out.println("[PASS] Got user token");
    }

    @And("a plant exists with its id captured")
    public void aPlantExistsWithItsIdCaptured() {
        ApiTestContext.State state = ApiTestContext.context();
        if (state.adminToken == null) {
            remember(login(adminUsername(), adminPassword()));
            state.adminToken = extractToken();
            assertNotNull(state.adminToken, "Setup: could not obtain admin token to create plant");
        }
        createAndCapturePlant();
    }

    @And("a plant named {string} exists under category {int}")
    public void aPlantNamedExistsUnderCategory(String name, int categoryId) {
        ApiTestContext.State state = ApiTestContext.context();
        if (state.adminToken == null) {
            remember(login(adminUsername(), adminPassword()));
            state.adminToken = extractToken();
            assertNotNull(state.adminToken, "Setup: could not obtain admin token to ensure plant exists");
        }
        System.out.println("[STEP] Ensuring plant '" + name + "' exists under category " + categoryId);
        APIResponse getAll = state.plants.getAll(state.adminToken);
        if (!getAll.text().contains("\"name\":\"" + name + "\"")) {
            System.out.println("[INFO] Plant '" + name + "' not found – creating it...");
            APIResponse created = state.plants.create(categoryId, name, 150.0, 25, state.adminToken);
            System.out.println("[INFO] Create response (" + created.status() + "): " + created.text());
        } else {
            System.out.println("[INFO] Plant '" + name + "' already exists.");
        }
    }

    @And("a sale exists for the captured plant id with its id captured")
    public void aSaleExistsForTheCapturedPlantIdWithItsIdCaptured() {
        ApiTestContext.State state = ApiTestContext.context();
        if (state.adminToken == null) {
            remember(login(adminUsername(), adminPassword()));
            state.adminToken = extractToken();
            assertNotNull(state.adminToken, "Setup: could not obtain admin token to create sale");
        }
        Long plantId = state.createdPlantId;
        assertNotNull(plantId, "Pre-condition: no captured plant id available – run \"a plant exists with its id captured\" first");
        System.out.println("[STEP] Creating sale for plant id " + plantId + "...");
        APIResponse response = state.sales.sell(plantId, 2, state.adminToken);
        assertEquals(response.status(), 201, "Pre-condition: sale creation failed. Body: " + response.text());
        remember(response);
        Long saleId = extractId();
        assertNotNull(saleId, "Pre-condition: could not extract sale id from: " + lastBody());
        state.createdSaleId = saleId;
        System.out.println("[INFO] Captured sale id: " + saleId);
    }

    private void createAndCapturePlant() {
        ApiTestContext.State state = ApiTestContext.context();
        System.out.println("[STEP] Creating a plant to capture its ID...");
        String name = uniquePlantName();
        int catId = state.testSubCategoryId > 0 ? state.testSubCategoryId : 5;
        APIResponse response = state.plants.create(catId, name, 10.0, 20, state.adminToken);
        assertEquals(response.status(), 201, "Pre-condition: plant creation failed. Body: " + response.text());
        remember(response);
        Long id = extractId();
        assertNotNull(id, "Pre-condition: could not extract plant id from: " + lastBody());
        state.createdPlantId = id;
        System.out.println("[INFO] Captured plant id: " + id);
    }

    // ── When – plant CRUD ─────────────────────────────────────────────────────

    @When("I create a unique plant under category {int} with price {double} and quantity {int}")
    public void iCreateAUniquePlantUnderCategoryWithPriceAndQuantity(int categoryId, double price, int quantity) {
        ApiTestContext.State state = ApiTestContext.context();
        int catId = state.testSubCategoryId > 0 ? state.testSubCategoryId : categoryId;
        String name = uniquePlantName();
        System.out.println("[STEP] Creating plant '" + name + "' in category " + catId
                + " price=" + price + " qty=" + quantity);
        APIResponse response = state.plants.create(catId, name, price, quantity, state.adminToken);
        remember(response);
        state.createdCategoryName = name;
        System.out.println("[API] " + response.status() + " → " + lastBody());
    }

    @When("I create a plant named {string} under category {int} with price {double} and quantity {int}")
    public void iCreateAPlantNamedUnderCategoryWithPriceAndQuantity(String name, int categoryId, double price, int quantity) {
        ApiTestContext.State state = ApiTestContext.context();
        int catId = state.testSubCategoryId > 0 ? state.testSubCategoryId : categoryId;
        System.out.println("[STEP] Creating plant '" + name + "' in category " + catId
                + " price=" + price + " qty=" + quantity);
        APIResponse response = state.plants.create(catId, name, price, quantity, state.adminToken);
        remember(response);
        System.out.println("[API] " + response.status() + " → " + lastBody());
    }

    @When("I send a PUT request for plant id {int} with name {string} price {double} and quantity {int}")
    public void iSendAPutRequestForPlantIdWithNamePriceAndQuantity(int plantId, String name, double price, int quantity) {
        System.out.println("[STEP] PUT /api/plants/" + plantId + " name=" + name + " price=" + price + " qty=" + quantity);
        ApiTestContext.State state = ApiTestContext.context();
        APIResponse response = state.plants.update(plantId, name, price, quantity, state.adminToken);
        remember(response);
        System.out.println("[API] " + response.status() + " → " + lastBody());
    }

    @When("I send a PUT request for the captured plant id with name {string} price {double} and quantity {int}")
    public void iSendAPutRequestForTheCapturedPlantIdWithNamePriceAndQuantity(String name, double price, int quantity) {
        Long plantId = ApiTestContext.context().createdPlantId;
        assertNotNull(plantId, "No captured plant id available");
        System.out.println("[STEP] PUT /api/plants/" + plantId + " (active token) name=" + name);
        ApiTestContext.State state = ApiTestContext.context();
        APIResponse response = state.plants.update(plantId, name, price, quantity, state.activeToken);
        remember(response);
        System.out.println("[API] " + response.status() + " → " + lastBody());
    }

    @When("I send a DELETE request for the captured plant id")
    public void iSendADeleteRequestForTheCapturedPlantId() {
        ApiTestContext.State state = ApiTestContext.context();
        Long plantId = state.createdPlantId;
        assertNotNull(plantId, "No captured plant id available");
        System.out.println("[STEP] DELETE /api/plants/" + plantId + " (active token)");
        APIResponse response = state.plants.delete(plantId, state.activeToken);
        remember(response);
        System.out.println("[API] " + response.status() + " → " + lastBody());
    }

    @When("I request all plants")
    public void iRequestAllPlants() {
        System.out.println("[STEP] GET /api/plants (active token)");
        ApiTestContext.State state = ApiTestContext.context();
        remember(state.plants.getAll(state.activeToken));
        System.out.println("[API] " + state.lastResponse.status() + " → " + lastBody());
    }

    @When("I request the captured sale by id")
    public void iRequestTheCapturedSaleById() {
        ApiTestContext.State state = ApiTestContext.context();
        Long saleId = state.createdSaleId;
        assertNotNull(saleId, "No captured sale id available");
        System.out.println("[STEP] GET /api/sales/" + saleId + " (active token)");
        remember(state.sales.getById(saleId, state.activeToken));
        System.out.println("[API] " + state.lastResponse.status() + " → " + lastBody());
    }

    @When("I attempt to sell the captured plant with quantity {int}")
    public void iAttemptToSellTheCapturedPlantWithQuantity(int quantity) {
        ApiTestContext.State state = ApiTestContext.context();
        Long plantId = state.createdPlantId;
        assertNotNull(plantId, "No captured plant id available");
        System.out.println("[STEP] POST /api/sales/plant/" + plantId + "?quantity=" + quantity + " (active token)");
        remember(state.sales.sell(plantId, quantity, state.activeToken));
        System.out.println("[API] " + state.lastResponse.status() + " → " + lastBody());
    }

    // ── When – filter/sort (215552U) ──────────────────────────────────────────

    @When("I get plants for category id {int}")
    public void getPlantsForCategory(int categoryId) {
        ApiTestContext.State s = ApiTestContext.context();
        int catId = s.testSubCategoryId > 0 ? s.testSubCategoryId : categoryId;
        System.out.println("[STEP] GET /api/plants/category/" + catId);
        remember(s.plants.getByCategory(catId, s.activeToken));
        System.out.println("[INFO] Status: " + s.lastResponse.status());
    }

    @When("I get plants sorted by price ascending")
    public void getPlantsortedByPriceAsc() {
        System.out.println("[STEP] GET /api/plants/paged?sort=price,asc");
        ApiTestContext.State s = ApiTestContext.context();
        remember(s.plants.getPaged("price,asc", s.activeToken));
        System.out.println("[INFO] Status: " + s.lastResponse.status());
    }

    // ── When – Swagger edge case (215565L) ────────────────────────────────────

    @When("I create a plant using the full Swagger example body under category {int}")
    public void iCreateAPlantUsingTheFullSwaggerExampleBodyUnderCategory(int categoryId) {
        ApiTestContext.State state = ApiTestContext.context();
        int catId = state.testSubCategoryId > 0 ? state.testSubCategoryId : categoryId;
        String uniqueName = uniquePlantName();
        System.out.println("[STEP] POST /api/plants/category/" + catId
                + " with full Swagger example body (name=" + uniqueName + ")");

        // Exact body the Swagger UI generates as its example, including optional id/category fields.
        String fullBody = "{"
                + "\"id\":0,"
                + "\"name\":\"" + uniqueName + "\","
                + "\"price\":150,"
                + "\"quantity\":25,"
                + "\"category\":{"
                +   "\"id\":0,"
                +   "\"name\":\"Anthurium\","
                +   "\"parent\":\"string\","
                +   "\"subCategories\":[\"string\"]"
                + "}"
                + "}";

        remember(state.plants.createWithRawBody(catId, fullBody, state.adminToken));
        System.out.println("[API] " + state.lastResponse.status() + " → " + lastBody());
    }

    // ── Then – response assertions ────────────────────────────────────────────

    @Then("the API response body should contain the created plant name")
    public void theApiResponseBodyShouldContainTheCreatedPlantName() {
        String plantName = ApiTestContext.context().createdCategoryName;
        assertNotNull(plantName, "No plant name was recorded");
        assertTrue(lastBody().contains(plantName),
                "Expected response to contain plant name '" + plantName + "', body: " + lastBody());
        System.out.println("[PASS] Plant name '" + plantName + "' found in response");
    }

    @Then("the API response body should contain price {double}")
    public void theApiResponseBodyShouldContainPrice(double price) {
        String priceStr = String.valueOf(price);
        String priceInt = String.valueOf((int) price);
        boolean found = lastBody().contains("\"price\":" + priceStr)
                || lastBody().contains("\"price\":" + priceInt)
                || lastBody().contains(priceStr);
        assertTrue(found, "Expected price " + price + " in response body: " + lastBody());
        System.out.println("[PASS] Price " + price + " found in response");
    }

    @Then("the API response body should contain quantity {int}")
    public void theApiResponseBodyShouldContainQuantity(int quantity) {
        assertTrue(lastBody().contains("\"quantity\":" + quantity) || lastBody().contains(String.valueOf(quantity)),
                "Expected quantity " + quantity + " in response body: " + lastBody());
        System.out.println("[PASS] Quantity " + quantity + " found in response");
    }

    @Then("the API response body should contain category id {int}")
    public void theApiResponseBodyShouldContainCategoryId(int categoryId) {
        ApiTestContext.State state = ApiTestContext.context();
        int expectedId = state.testSubCategoryId > 0 ? state.testSubCategoryId : categoryId;
        String body = lastBody();
        boolean hasCategoryKey = body.contains("\"category\"") || body.contains("\"categoryId\"");
        boolean hasId = body.contains("\"id\":" + expectedId);
        assertTrue(hasCategoryKey && hasId,
                "Expected category id " + expectedId + " in response body: " + body);
        System.out.println("[PASS] Category id " + expectedId + " found in response");
    }

    @Then("the API response body should indicate a bad request error")
    public void theApiResponseBodyShouldIndicateABadRequestError() {
        String body = lastBody().toLowerCase();
        boolean hasError = body.contains("bad_request") || body.contains("400")
                || body.contains("validation") || body.contains("invalid")
                || body.contains("duplicate") || body.contains("mandatory");
        assertTrue(hasError, "Expected bad request error in response body: " + lastBody());
        System.out.println("[PASS] Bad request error confirmed in response");
    }

    @Then("the API response body should indicate a not found error")
    public void theApiResponseBodyShouldIndicateANotFoundError() {
        String body = lastBody().toLowerCase();
        boolean hasError = body.contains("not found") || body.contains("404") || body.contains("not_found");
        boolean emptyOk = lastBody().isBlank();
        assertTrue(hasError || emptyOk, "Expected not found error in response body: " + lastBody());
        System.out.println("[PASS] Not found error confirmed in response");
    }

    @Then("the API response body should indicate a forbidden error")
    public void theApiResponseBodyShouldIndicateAForbiddenError() {
        String body = lastBody().toLowerCase();
        boolean hasError = body.contains("forbidden") || body.contains("403") || body.contains("access denied");
        assertTrue(hasError, "Expected forbidden error in response body: " + lastBody());
        System.out.println("[PASS] Forbidden error confirmed in response");
    }

    @Then("the API response body should contain a plant list")
    public void theApiResponseBodyShouldContainAPlantList() {
        String body = lastBody();
        boolean isList = body.trim().startsWith("[")
                || body.contains("\"content\"")
                || body.contains("\"name\"");
        assertTrue(isList, "Expected a plant list in response body: " + body);
        System.out.println("[PASS] Response contains a plant list");
    }

    @Then("the API response body should contain the captured sale id")
    public void theApiResponseBodyShouldContainTheCapturedSaleId() {
        Long saleId = ApiTestContext.context().createdSaleId;
        assertNotNull(saleId, "No captured sale id to verify");
        assertTrue(lastBody().contains("\"id\":" + saleId),
                "Expected sale id " + saleId + " in response body: " + lastBody());
        System.out.println("[PASS] Sale id " + saleId + " confirmed in response");
    }

    @Then("a GET request for the captured plant id should return 404")
    public void aGetRequestForTheCapturedPlantIdShouldReturn404() {
        ApiTestContext.State state = ApiTestContext.context();
        Long plantId = state.createdPlantId;
        assertNotNull(plantId, "No captured plant id available");
        System.out.println("[STEP] GET /api/plants/" + plantId + " (should be 404 after deletion)");
        APIResponse response = state.plants.getById(plantId, state.adminToken);
        System.out.println("[API] " + response.status() + " → " + response.text());
        assertEquals(response.status(), 404,
                "Expected 404 after deletion of plant id " + plantId + ". Got: " + response.status());
        System.out.println("[PASS] Deleted plant correctly returns 404");
    }

    @Then("the API response status should not be 500")
    public void theApiResponseStatusShouldNotBe500() {
        int status = ApiTestContext.context().lastResponse.status();
        System.out.println("[STEP] Verifying response is not 500. Actual status: " + status);
        assertNotEquals(status, 500,
                "DEFECT API_POST_PLT_ADM_007: Backend returned 500 Internal Server Error when "
                + "receiving the Swagger-documented optional fields (id, nested category). "
                + "Client input must never cause the server to crash; expected 4xx, got 500.");
        System.out.println("[PASS] Status " + status + " is not 500");
    }

    // ── Then – filter/sort assertions (215552U) ───────────────────────────────

    @Then("the plants response status should be {int}")
    public void plantsResponseStatusShouldBe(int expected) {
        int actual = ApiTestContext.context().lastResponse.status();
        assertEquals(actual, expected, "Expected HTTP " + expected + " but got " + actual);
        System.out.println("[PASS] Plants status is " + actual);
    }

    @And("all returned plants should belong to category {int}")
    public void allPlantsShouldBelongToCategory(int categoryId) {
        ApiTestContext.State state = ApiTestContext.context();
        int expectedCatId = state.testSubCategoryId > 0 ? state.testSubCategoryId : categoryId;
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
            assertEquals(plantCategoryId, expectedCatId,
                    "Plant '" + plant.get("name").getAsString() + "' belongs to category "
                            + plantCategoryId + ", not " + expectedCatId);
        }
        System.out.println("[PASS] All plants belong to category " + expectedCatId);
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
