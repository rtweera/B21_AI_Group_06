package stepdefinitions;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.testng.Assert.*;

/**
 * Step definitions for Plants API tests written by tester 215565L.
 *
 * Uses the Playwright APIRequestContext via {@link ApiStepSupport} and
 * {@link ApiTestContext} – consistent with the rest of the team.
 * No additional API-abstraction layer is used (lecturer requirement).
 *
 * Test Cases covered:
 *   API_POST_PLT_ADM_001  – Admin creates valid plant
 *   API_POST_PLT_ADM_002  – Admin duplicate plant rejected (400)
 *   API_POST_PLT_ADM_003  – Admin negative quantity rejected (400)
 *   API_PUT_PLT_ADM_004   – Admin updates non-existent plant (404)
 *   API_DEL_PLT_ADM_005   – Admin deletes existing plant (204)
 *   API_PUT_PLT_USR_001   – Normal user cannot update plant (403)
 *   API_DEL_PLT_USR_002   – Normal user cannot delete plant (403)
 *   API_GET_PLT_USR_003   – Normal user reads all plants (200)
 *   API_GET_SLS_USR_001   – Normal user reads sale by id (200)
 *   API_POST_SLS_USR_002  – Normal user cannot create sale (403)
 */
public class PlantsApiSteps extends ApiStepSupport {

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    /**
     * Generate a plant name that is unique, under the 25-char API limit,
     * and unlikely to collide across parallel runs.
     * Format: "P_" + 6-digit nanotime base-36 suffix = max 8 chars.
     */
    private String uniquePlantName() {
        String suffix = Long.toString(System.nanoTime(), 36);
        return "P_" + suffix.substring(Math.max(0, suffix.length() - 6));
    }

    /**
     * Build the standard plant request body map.
     */
    private Map<String, Object> plantBody(String name, double price, int quantity) {
        return Map.of("name", name, "price", price, "quantity", quantity);
    }

    /**
     * POST to /api/plants/category/{categoryId} using the admin token and
     * store the response. Returns the raw {@link APIResponse}.
     */
    private APIResponse createPlant(String name, long categoryId, double price, int quantity) {
        ApiTestContext.State state = ApiTestContext.context();
        return state.api.post(
                "/api/plants/category/" + categoryId,
                bearer(state.adminToken).setData(plantBody(name, price, quantity)));
    }

    /**
     * Extract the first {@code "id": <number>} from the last response body.
     * Delegates to the parent class {@link ApiStepSupport#extractId()}.
     */
    private Long extractPlantId() {
        return extractId();
    }

    // -----------------------------------------------------------------------
    // Given – authentication (delegates to existing shared steps via reuse)
    // -----------------------------------------------------------------------
    // NOTE: "I have an admin API token" and "I have a normal user API token"
    //       are already defined in ApiLoginSteps and are reused here.

    // -----------------------------------------------------------------------
    // Given – precondition: ensure a specific named plant exists
    // -----------------------------------------------------------------------

    @And("a plant named {string} exists under category {int}")
    public void aPlantNamedExistsUnderCategory(String name, int categoryId) {
        System.out.println("[STEP] Ensuring plant '" + name + "' exists under category " + categoryId);
        ApiTestContext.State state = ApiTestContext.context();

        // Check whether it already exists
        APIResponse getAll = state.api.get("/api/plants", bearer(state.adminToken));
        String body = getAll.text();

        if (!body.contains("\"name\":\"" + name + "\"")) {
            System.out.println("[INFO] Plant '" + name + "' not found – creating it...");
            APIResponse created = createPlant(name, categoryId, 150.0, 25);
            System.out.println("[INFO] Create response (" + created.status() + "): " + created.text());
            // If it already exists (409 / 400) that is also fine – the test will trigger the 400 we want
        } else {
            System.out.println("[INFO] Plant '" + name + "' already exists.");
        }
    }

    @And("a plant exists and its id is captured")
    public void aPlantExistsAndItsIdIsCaptured() {
        System.out.println("[STEP] Creating a plant to capture its ID...");
        String name = uniquePlantName();
        APIResponse response = createPlant(name, 5, 10.0, 20);
        assertEquals(response.status(), 201, "Pre-condition: plant creation failed. Body: " + response.text());
        remember(response);
        Long id = extractPlantId();
        assertNotNull(id, "Pre-condition: could not extract plant id from: " + lastBody());
        ApiTestContext.context().createdPlantId = id;
        System.out.println("[INFO] Captured plant id: " + id);
    }

    @And("a sale is created for the captured plant id with quantity {int} and its id is captured")
    public void aSaleIsCreatedForTheCapturedPlantIdAndItsIdIsCaptured(int quantity) {
        ApiTestContext.State state = ApiTestContext.context();
        Long plantId = state.createdPlantId;
        assertNotNull(plantId, "Pre-condition: no captured plant id available");
        System.out.println("[STEP] Creating sale for plant id " + plantId + " with quantity " + quantity + "...");
        APIResponse response = state.api.post(
                "/api/sales/plant/" + plantId + "?quantity=" + quantity,
                bearer(state.adminToken));
        assertEquals(response.status(), 201, "Pre-condition: sale creation failed. Body: " + response.text());
        remember(response);
        Long saleId = extractId();
        assertNotNull(saleId, "Pre-condition: could not extract sale id from: " + lastBody());
        state.createdSaleId = saleId;
        System.out.println("[INFO] Captured sale id: " + saleId);
    }

    // -----------------------------------------------------------------------
    // When – plant operations
    // -----------------------------------------------------------------------

    @When("I create a unique plant under category {int} with price {double} and quantity {int}")
    public void iCreateAUniquePlantUnderCategoryWithPriceAndQuantity(int categoryId, double price, int quantity) {
        String name = uniquePlantName();
        System.out.println("[STEP] Creating plant '" + name + "' in category " + categoryId
                + " price=" + price + " qty=" + quantity);
        ApiTestContext.State state = ApiTestContext.context();
        APIResponse response = state.api.post(
                "/api/plants/category/" + categoryId,
                bearer(state.adminToken).setData(plantBody(name, price, quantity)));
        remember(response);
        // Store name so assertion steps can reference it
        state.createdCategoryName = name;   // reuse existing field as "createdPlantName"
        System.out.println("[API] " + response.status() + " → " + lastBody());
    }

    @When("I create a plant named {string} under category {int} with price {double} and quantity {int}")
    public void iCreateAPlantNamedUnderCategoryWithPriceAndQuantity(String name, int categoryId, double price, int quantity) {
        System.out.println("[STEP] Creating plant '" + name + "' in category " + categoryId
                + " price=" + price + " qty=" + quantity);
        ApiTestContext.State state = ApiTestContext.context();
        APIResponse response = state.api.post(
                "/api/plants/category/" + categoryId,
                bearer(state.adminToken).setData(plantBody(name, price, quantity)));
        remember(response);
        System.out.println("[API] " + response.status() + " → " + lastBody());
    }

    @When("I send a PUT request for plant id {int} with name {string} price {double} and quantity {int}")
    public void iSendAPutRequestForPlantIdWithNamePriceAndQuantity(int plantId, String name, double price, int quantity) {
        System.out.println("[STEP] PUT /api/plants/" + plantId + " name=" + name + " price=" + price + " qty=" + quantity);
        ApiTestContext.State state = ApiTestContext.context();
        APIResponse response = state.api.put(
                "/api/plants/" + plantId,
                bearer(state.adminToken).setData(plantBody(name, price, quantity)));
        remember(response);
        System.out.println("[API] " + response.status() + " → " + lastBody());
    }

    @When("I send a PUT request for the captured plant id with name {string} price {double} and quantity {int}")
    public void iSendAPutRequestForTheCapturedPlantIdWithNamePriceAndQuantity(String name, double price, int quantity) {
        Long plantId = ApiTestContext.context().createdPlantId;
        assertNotNull(plantId, "No captured plant id available");
        System.out.println("[STEP] PUT /api/plants/" + plantId + " (active token) name=" + name);
        ApiTestContext.State state = ApiTestContext.context();
        APIResponse response = state.api.put(
                "/api/plants/" + plantId,
                bearer(state.activeToken).setData(plantBody(name, price, quantity)));
        remember(response);
        System.out.println("[API] " + response.status() + " → " + lastBody());
    }

    @When("I send a DELETE request for the captured plant id")
    public void iSendADeleteRequestForTheCapturedPlantId() {
        ApiTestContext.State state = ApiTestContext.context();
        Long plantId = state.createdPlantId;
        assertNotNull(plantId, "No captured plant id available");
        // activeToken is always the most recently acquired token (admin or user)
        // so this correctly uses user credentials after "Given I have a normal user API token"
        String token = state.activeToken;
        System.out.println("[STEP] DELETE /api/plants/" + plantId + " (active token)");
        APIResponse response = state.api.delete("/api/plants/" + plantId, bearer(token));
        remember(response);
        System.out.println("[API] " + response.status() + " → " + lastBody());
    }

    @When("I request all plants with the user token")
    public void iRequestAllPlantsWithTheUserToken() {
        System.out.println("[STEP] GET /api/plants with user token");
        ApiTestContext.State state = ApiTestContext.context();
        APIResponse response = state.api.get("/api/plants", bearer(state.userToken));
        remember(response);
        System.out.println("[API] " + response.status() + " → " + lastBody());
    }

    @When("I request the captured sale by id with the user token")
    public void iRequestTheCapturedSaleByIdWithTheUserToken() {
        ApiTestContext.State state = ApiTestContext.context();
        Long saleId = state.createdSaleId;
        assertNotNull(saleId, "No captured sale id available");
        System.out.println("[STEP] GET /api/sales/" + saleId + " with user token");
        APIResponse response = state.api.get("/api/sales/" + saleId, bearer(state.userToken));
        remember(response);
        System.out.println("[API] " + response.status() + " → " + lastBody());
    }

    @When("I attempt to sell the captured plant with quantity {int} as the normal user")
    public void iAttemptToSellTheCapturedPlantWithQuantityAsTheNormalUser(int quantity) {
        ApiTestContext.State state = ApiTestContext.context();
        Long plantId = state.createdPlantId;
        assertNotNull(plantId, "No captured plant id available");
        System.out.println("[STEP] POST /api/sales/plant/" + plantId + "?quantity=" + quantity + " with user token");
        APIResponse response = state.api.post(
                "/api/sales/plant/" + plantId + "?quantity=" + quantity,
                bearer(state.userToken));
        remember(response);
        System.out.println("[API] " + response.status() + " → " + lastBody());
    }

    // -----------------------------------------------------------------------
    // Then – response assertions
    // -----------------------------------------------------------------------

    @Then("the API response body should contain the created plant name")
    public void theApiResponseBodyShouldContainTheCreatedPlantName() {
        String plantName = ApiTestContext.context().createdCategoryName; // reused field
        assertNotNull(plantName, "No plant name was recorded");
        assertTrue(lastBody().contains(plantName),
                "Expected response to contain plant name '" + plantName + "', body: " + lastBody());
        System.out.println("[PASS] Plant name '" + plantName + "' found in response");
    }

    @Then("the API response body should contain price {double}")
    public void theApiResponseBodyShouldContainPrice(double price) {
        // Match both "price":150.0 and "price":150 (integer representation)
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
        // The Plant schema embeds a full Category object: {"id": 5, "name": ...}
        // We look for "id": <categoryId> AND the presence of "category" to distinguish
        // it from the plant's own id.
        String body = lastBody();
        boolean hasCategoryKey = body.contains("\"category\"") || body.contains("\"categoryId\"");
        boolean hasId = body.contains("\"id\":" + categoryId);
        assertTrue(hasCategoryKey && hasId,
                "Expected category id " + categoryId + " in response body: " + body);
        System.out.println("[PASS] Category id " + categoryId + " found in response");
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
        // Also acceptable: empty body (some 404 responses have no body)
        boolean emptyOk = lastBody().isBlank();
        assertTrue(hasError || emptyOk,
                "Expected not found error in response body: " + lastBody());
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
        APIResponse response = state.api.get("/api/plants/" + plantId, bearer(state.adminToken));
        System.out.println("[API] " + response.status() + " → " + response.text());
        assertEquals(response.status(), 404,
                "Expected 404 after deletion of plant id " + plantId + ". Got: " + response.status());
        System.out.println("[PASS] Deleted plant correctly returns 404");
    }

    // -----------------------------------------------------------------------
    // API_POST_PLT_ADM_006 – Bug: full Swagger example body causes 500
    // -----------------------------------------------------------------------

    /**
     * Sends the exact Swagger example body (including the optional {@code id} and
     * nested {@code category} fields) to POST /api/plants/category/{categoryId}.
     *
     * Per the OpenAPI spec those fields are marked as optional; the backend must
     * handle them gracefully and must NOT return 500.
     *
     * The plant name is made unique to avoid interference with other tests.
     */
    @When("I create a plant using the full Swagger example body under category {int}")
    public void iCreateAPlantUsingTheFullSwaggerExampleBodyUnderCategory(int categoryId) {
        String uniqueName = uniquePlantName();
        System.out.println("[STEP] POST /api/plants/category/" + categoryId
                + " with full Swagger example body (name=" + uniqueName + ")");

        // This is the exact body the Swagger UI generates as its example,
        // including the optional "id" and nested "category" object.
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

        ApiTestContext.State state = ApiTestContext.context();
        APIResponse response = state.api.post(
                "/api/plants/category/" + categoryId,
                bearer(state.adminToken).setHeader("Content-Type", "application/json").setData(fullBody));
        remember(response);
        System.out.println("[API] " + response.status() + " → " + lastBody());
    }

    /**
     * API_POST_PLT_ADM_007 – Error-handling robustness.
     * Asserts that the last response status is NOT 500 (Internal Server Error).
     *
     * A 500 means the server itself crashed. Client input — even if unexpected —
     * must never cause a server crash. The correct response for bad client input
     * is always a 4xx status code.
     *
     * This step is intentionally designed to FAIL while the bug is present,
     * producing a clear defect message in the test report.
     */
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

}
