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
 * Sales API step definitions (215552U).
 * Covers sell, stock verification, delete, and sort scenarios.
 */
public class SalesApiSteps extends ApiStepSupport {

    @Given("I am authenticated as admin via API")
    public void authAsAdmin() {
        System.out.println("[STEP] Authenticating as admin via API...");
        remember(login(adminUsername(), adminPassword()));
        ApiTestContext.context().adminToken = extractToken();
        ApiTestContext.context().activeToken = ApiTestContext.context().adminToken;
        assertNotNull(ApiTestContext.context().adminToken, "Admin token should not be null");
        System.out.println("[PASS] Got admin token");
    }

    @Given("I am authenticated as user via API")
    public void authAsUser() {
        System.out.println("[STEP] Authenticating as user via API...");
        remember(login(userUsername(), userPassword()));
        ApiTestContext.context().userToken = extractToken();
        ApiTestContext.context().activeToken = ApiTestContext.context().userToken;
        assertNotNull(ApiTestContext.context().userToken, "User token should not be null");
        System.out.println("[PASS] Got user token");
    }

    @And("I use the existing plant with id {int}")
    public void useExistingPlant(int plantId) {
        System.out.println("[STEP] Using existing plant id=" + plantId + "...");
        ApiTestContext.State s = ApiTestContext.context();
        if (s.plants.getById(plantId, s.adminToken).status() != 200) {
            System.out.println("[INFO] Plant id=" + plantId + " not found. Locating a usable plant...");
            plantId = findOrCreatePlant(s);
        }
        s.storedPlantId = plantId;
        int currentStock = s.plants.getQuantity(plantId, s.adminToken);
        if (currentStock < 10) {
            System.out.println("[INFO] Low stock (" + currentStock + "). Replenishing to 20...");
            s.plants.setQuantity(plantId, 20, s.adminToken);
            currentStock = s.plants.getQuantity(plantId, s.adminToken);
        }
        s.stockBeforeSale = currentStock;
        System.out.println("[PASS] Plant id=" + plantId + " stock=" + currentStock);
    }

    private int findOrCreatePlant(ApiTestContext.State s) {
        com.microsoft.playwright.APIResponse all = s.plants.getAll(s.adminToken);
        if (all.status() == 200) {
            JsonElement parsed = JsonParser.parseString(all.text());
            JsonArray plants = parsed.isJsonArray() ? parsed.getAsJsonArray()
                    : (parsed.getAsJsonObject().has("content")
                            ? parsed.getAsJsonObject().getAsJsonArray("content") : null);
            if (plants != null && plants.size() > 0) {
                int id = plants.get(0).getAsJsonObject().get("id").getAsInt();
                System.out.println("[INFO] Using first available plant id=" + id);
                return id;
            }
        }
        int catId = s.testSubCategoryId > 0 ? s.testSubCategoryId : 1;
        String name = "SalesTestPlant_" + Long.toString(System.nanoTime(), 36);
        com.microsoft.playwright.APIResponse res = s.plants.create(catId, name, 50.0, 100, s.adminToken);
        if (res.status() == 200 || res.status() == 201) {
            int id = JsonParser.parseString(res.text()).getAsJsonObject().get("id").getAsInt();
            System.out.println("[INFO] Created plant id=" + id + " for sales testing");
            return id;
        }
        throw new RuntimeException("Cannot find or create a plant for sales testing. Create status: "
                + res.status() + " body: " + res.text());
    }

    @When("I create a sale for that plant with quantity {int}")
    public void createSale(int quantity) {
        ApiTestContext.State s = ApiTestContext.context();
        System.out.println("[STEP] Creating sale plant=" + s.storedPlantId + " qty=" + quantity);
        remember(s.sales.sell(s.storedPlantId, quantity, s.activeToken));
        System.out.println("[INFO] Response: " + s.lastResponse.status());
    }

    @Then("the API response status should be 400 or 422")
    public void responseStatusShouldBe400Or422() {
        int status = ApiTestContext.context().lastResponse.status();
        assertTrue(status == 400 || status == 422, "Expected 400 or 422 but got " + status);
        System.out.println("[PASS] Status is " + status);
    }

    @And("the plant stock should be reduced by {int}")
    public void plantStockReducedBy(int reduction) {
        ApiTestContext.State s = ApiTestContext.context();
        int stockAfter = s.plants.getQuantity(s.storedPlantId, s.adminToken);
        int expected = s.stockBeforeSale - reduction;
        System.out.println("[INFO] Stock before=" + s.stockBeforeSale + " after=" + stockAfter);
        assertEquals(stockAfter, expected, "Stock mismatch");
        System.out.println("[PASS] Stock reduced correctly");
    }

    @And("the plant stock should remain unchanged")
    public void plantStockShouldRemainUnchanged() {
        ApiTestContext.State s = ApiTestContext.context();
        int stockAfter = s.plants.getQuantity(s.storedPlantId, s.adminToken);
        System.out.println("[INFO] Stock before=" + s.stockBeforeSale + " after=" + stockAfter);
        assertEquals(stockAfter, s.stockBeforeSale, "Stock should remain unchanged");
        System.out.println("[PASS] Stock unchanged at " + stockAfter);
    }

    @And("I have created a sale for that plant with quantity {int}")
    public void createSaleForPlant(int quantity) {
        ApiTestContext.State s = ApiTestContext.context();
        System.out.println("[STEP] Creating sale plant=" + s.storedPlantId + " qty=" + quantity);
        APIResponse response = s.sales.sell(s.storedPlantId, quantity, s.adminToken);
        assertEquals(response.status(), 201, "Sale creation failed: " + response.text());
        JsonObject json = JsonParser.parseString(response.text()).getAsJsonObject();
        s.storedSaleId = json.get("id").getAsInt();
        System.out.println("[PASS] Created sale id=" + s.storedSaleId);
    }

    @When("I delete that sale via API")
    public void deleteThatSale() {
        ApiTestContext.State s = ApiTestContext.context();
        System.out.println("[STEP] Deleting sale id=" + s.storedSaleId);
        remember(s.sales.delete(s.storedSaleId, s.activeToken));
        System.out.println("[INFO] Response: " + s.lastResponse.status());
    }

    @When("I get the sales list sorted by date descending")
    public void getSalesSorted() {
        ApiTestContext.State s = ApiTestContext.context();
        System.out.println("[STEP] GET /api/sales/page?sort=soldAt,desc");
        remember(s.sales.getPaged("soldAt,desc", s.activeToken));
        System.out.println("[INFO] Response: " + s.lastResponse.status());
    }

    @And("the response should contain a list of sales")
    public void responseContainsListOfSales() {
        String body = lastBody();
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        assertTrue(json.has("content"), "Response should have a 'content' array");
        var content = json.getAsJsonArray("content");
        System.out.println("[INFO] Sales returned: " + content.size());
        String previousDate = null;
        for (int i = 0; i < content.size(); i++) {
            JsonObject sale = content.get(i).getAsJsonObject();
            String soldAt = sale.get("soldAt").getAsString();
            if (previousDate != null) {
                assertTrue(soldAt.compareTo(previousDate) <= 0,
                        "Sales not in descending order: " + soldAt + " after " + previousDate);
            }
            previousDate = soldAt;
        }
        System.out.println("[PASS] Sales list returned and sorted by date descending");
    }
}
