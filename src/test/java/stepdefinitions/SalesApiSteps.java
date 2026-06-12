package stepdefinitions;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.testng.Assert.*;

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
        s.storedPlantId = plantId;
        int currentStock = getPlantStock(plantId, s.adminToken);
        if (currentStock < 10) {
            System.out.println("[INFO] Low stock (" + currentStock + "). Replenishing to 20...");
            setPlantStock(plantId, 20, s.adminToken);
            currentStock = getPlantStock(plantId, s.adminToken);
        }
        s.stockBeforeSale = currentStock;
        System.out.println("[PASS] Plant id=" + plantId + " stock=" + currentStock);
    }

    @When("I create a sale for that plant with quantity {int}")
    public void createSale(int quantity) {
        ApiTestContext.State s = ApiTestContext.context();
        System.out.println("[STEP] Creating sale plant=" + s.storedPlantId + " qty=" + quantity);
        APIResponse response = s.api.post(
                "/api/sales/plant/" + s.storedPlantId + "?quantity=" + quantity,
                bearer(s.activeToken));
        remember(response);
        System.out.println("[INFO] Response: " + response.status());
    }

    @Then("the API response status should be 400 or 422")
    public void responseStatusShouldBe400Or422() {
        int status = ApiTestContext.context().lastResponse.status();
        assertTrue(status == 400 || status == 422,
                "Expected 400 or 422 but got " + status);
        System.out.println("[PASS] Status is " + status);
    }

    @And("the plant stock should be reduced by {int}")
    public void plantStockReducedBy(int reduction) {
        ApiTestContext.State s = ApiTestContext.context();
        int stockAfter = getPlantStock(s.storedPlantId, s.adminToken);
        int expected = s.stockBeforeSale - reduction;
        System.out.println("[INFO] Stock before=" + s.stockBeforeSale + " after=" + stockAfter);
        assertEquals(stockAfter, expected, "Stock mismatch");
        System.out.println("[PASS] Stock reduced correctly");
    }

    @And("the plant stock should remain unchanged")
    public void plantStockShouldRemainUnchanged() {
        ApiTestContext.State s = ApiTestContext.context();
        int stockAfter = getPlantStock(s.storedPlantId, s.adminToken);
        System.out.println("[INFO] Stock before=" + s.stockBeforeSale + " after=" + stockAfter);
        assertEquals(stockAfter, s.stockBeforeSale, "Stock should remain unchanged");
        System.out.println("[PASS] Stock unchanged at " + stockAfter);
    }

    @And("I have created a sale for that plant with quantity {int}")
    public void createSaleForPlant(int quantity) {
        ApiTestContext.State s = ApiTestContext.context();
        System.out.println("[STEP] Creating sale plant=" + s.storedPlantId + " qty=" + quantity);
        APIResponse response = s.api.post(
                "/api/sales/plant/" + s.storedPlantId + "?quantity=" + quantity,
                bearer(s.adminToken));
        assertEquals(response.status(), 201, "Sale creation failed: " + response.text());
        JsonObject json = JsonParser.parseString(response.text()).getAsJsonObject();
        s.storedSaleId = json.get("id").getAsInt();
        System.out.println("[PASS] Created sale id=" + s.storedSaleId);
    }

    @When("I delete that sale via API")
    public void deleteThatSale() {
        ApiTestContext.State s = ApiTestContext.context();
        System.out.println("[STEP] Deleting sale id=" + s.storedSaleId);
        APIResponse response = s.api.delete(
                "/api/sales/" + s.storedSaleId,
                bearer(s.activeToken));
        remember(response);
        System.out.println("[INFO] Response: " + response.status());
    }

    @When("I get the sales list sorted by date descending")
    public void getSalesSorted() {
        ApiTestContext.State s = ApiTestContext.context();
        System.out.println("[STEP] GET /api/sales/page?sort=soldAt,desc");
        APIResponse response = s.api.get(
                "/api/sales/page?sort=soldAt,desc",
                bearer(s.activeToken));
        remember(response);
        System.out.println("[INFO] Response: " + response.status());
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

    private int getPlantStock(int plantId, String token) {
        APIResponse res = ApiTestContext.context().api.get(
                "/api/plants/" + plantId,
                RequestOptions.create().setHeader("Authorization", "Bearer " + token));
        return JsonParser.parseString(res.text()).getAsJsonObject().get("quantity").getAsInt();
    }

    private void setPlantStock(int plantId, int newQuantity, String token) {
        APIResponse getRes = ApiTestContext.context().api.get(
                "/api/plants/" + plantId,
                RequestOptions.create().setHeader("Authorization", "Bearer " + token));
        JsonObject plantJson = JsonParser.parseString(getRes.text()).getAsJsonObject();

        JsonObject putBody = new JsonObject();
        putBody.addProperty("id", plantId);
        putBody.addProperty("name", plantJson.get("name").getAsString());
        putBody.addProperty("price", plantJson.get("price").getAsDouble());
        putBody.addProperty("quantity", newQuantity);

        JsonObject categoryObj = new JsonObject();
        if (plantJson.has("category") && !plantJson.get("category").isJsonNull()) {
            categoryObj.addProperty("id", plantJson.getAsJsonObject("category").get("id").getAsInt());
        }
        putBody.add("category", categoryObj);

        ApiTestContext.context().api.put(
                "/api/plants/" + plantId,
                RequestOptions.create()
                        .setHeader("Authorization", "Bearer " + token)
                        .setData(putBody.toString()));
    }
}
