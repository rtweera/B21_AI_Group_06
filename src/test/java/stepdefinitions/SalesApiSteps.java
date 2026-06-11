package stepdefinitions;

import api.ApiClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIResponse;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import utils.PlaywrightFactory;

import static org.testng.Assert.*;

// The Java code for each API test step
public class SalesApiSteps {

    private ApiClient api;
    private APIResponse lastResponse;
    private int storedPlantId;
    private int stockBeforeSale;  // remember stock before selling

    private int storedSaleId;  // remember the sale id we created

    // Set up the API client before each API test
    @Before("@API")
    public void initApi() {
        api = new ApiClient(PlaywrightFactory.getApiContext());
    }

    @Given("I am authenticated as admin via API")
    public void authAsAdmin() {
        System.out.println("[STEP] Authenticating as admin via API...");
        String token = api.login("admin", "admin123");
        assertNotNull(token, "Admin token should not be null");
        System.out.println("[PASS] Got admin token");
    }

    @And("I use the existing plant with id {int}")
    public void useExistingPlant(int plantId) {
        System.out.println("[STEP] Using existing plant id=" + plantId + "...");
        storedPlantId = plantId;
        // Read and remember the current stock so we can verify the reduction later
        stockBeforeSale = api.getPlantStock(storedPlantId);
        System.out.println("[PASS] Plant id=" + plantId + " has stock " + stockBeforeSale);
    }

    @When("I create a sale for that plant with quantity {int}")
    public void createSale(int quantity) {
        System.out.println("[STEP] Creating sale for plant " + storedPlantId + " qty " + quantity + "...");
        lastResponse = api.postNoBody("/api/sales/plant/" + storedPlantId + "?quantity=" + quantity);
        System.out.println("[INFO] Response status: " + lastResponse.status());
    }

    @Then("the API response status should be {int}")
    public void responseStatusShouldBe(int expected) {
        System.out.println("[STEP] Verifying status is " + expected + "...");
        assertEquals(lastResponse.status(), expected,
                "Expected " + expected + " but got " + lastResponse.status());
        System.out.println("[PASS] Status is " + expected);
    }

    @And("the plant stock should be reduced by {int}")
    public void plantStockReducedBy(int reduction) {
        System.out.println("[STEP] Verifying plant stock dropped by " + reduction + "...");
        int stockAfter = api.getPlantStock(storedPlantId);
        int expectedStock = stockBeforeSale - reduction;
        System.out.println("[INFO] Stock before: " + stockBeforeSale + ", after: " + stockAfter);
        assertEquals(stockAfter, expectedStock,
                "Stock should be " + expectedStock + " but was " + stockAfter);
        System.out.println("[PASS] Stock reduced from " + stockBeforeSale + " to " + stockAfter);
    }

    @Then("the API response status should be 400 or 422")
    public void responseStatusShouldBe400Or422() {
        System.out.println("[STEP] Verifying status is 400 or 422...");
        int status = lastResponse.status();
        assertTrue(status == 400 || status == 422,
                "Expected 400 or 422 but got " + status);
        System.out.println("[PASS] Status is " + status);
    }

    @And("the plant stock should remain unchanged")
    public void plantStockShouldRemainUnchanged() {
        System.out.println("[STEP] Verifying plant stock did not change...");
        int stockAfter = api.getPlantStock(storedPlantId);
        System.out.println("[INFO] Stock before: " + stockBeforeSale + ", after: " + stockAfter);
        assertEquals(stockAfter, stockBeforeSale,
                "Stock should remain " + stockBeforeSale + " but was " + stockAfter);
        System.out.println("[PASS] Stock unchanged at " + stockAfter);
    }

    @And("I have created a sale for that plant with quantity {int}")
    public void createSaleForPlant(int quantity) {
        System.out.println("[STEP] Creating a sale for plant " + storedPlantId + "...");
        storedSaleId = api.createSale(storedPlantId, quantity);
        System.out.println("[PASS] Created sale id=" + storedSaleId);
    }

    @When("I delete that sale via API")
    public void deleteThatSale() {
        System.out.println("[STEP] Deleting sale id=" + storedSaleId + "...");
        lastResponse = api.delete("/api/sales/" + storedSaleId);
        System.out.println("[INFO] Response status: " + lastResponse.status());
    }

    @When("I get the sales list sorted by date descending")
    public void getSalesSorted() {
        System.out.println("[STEP] Getting sales list sorted by soldAt desc...");
        lastResponse = api.get("/api/sales/page?sort=soldAt,desc");
        System.out.println("[INFO] Response status: " + lastResponse.status());
    }

    @And("the response should contain a list of sales")
    public void responseContainsListOfSales() {
        System.out.println("[STEP] Verifying response contains a content list...");
        String body = lastResponse.text();
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        assertTrue(json.has("content"), "Response should have a 'content' array");

        var content = json.getAsJsonArray("content");
        int count = content.size();
        System.out.println("[PASS] Response contains a list of " + count + " sales");

        // Verify the sales are in descending order by soldAt date
        String previousDate = null;
        for (int i = 0; i < count; i++) {
            JsonObject sale = content.get(i).getAsJsonObject();
            String soldAt = sale.get("soldAt").getAsString();
            System.out.println("[INFO] Sale " + i + " soldAt: " + soldAt);

            if (previousDate != null) {
                // Each date should be <= the previous one (newest first)
                // soldAt dates like "2026-06-07T14:38:10" sort correctly as text
                assertTrue(soldAt.compareTo(previousDate) <= 0,
                        "Sales not in descending order. " + soldAt + " should be older than " + previousDate);
            }
            previousDate = soldAt;
        }
        System.out.println("[PASS] Sales are sorted by date descending");
    }

    @Given("I am authenticated as user via API")
    public void authAsUser() {
        System.out.println("[STEP] Authenticating as user via API...");
        String token = api.login("testuser", "test123");
        assertNotNull(token, "User token should not be null");
        System.out.println("[PASS] Got user token");
    }
}