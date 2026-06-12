package stepdefinitions.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIResponse;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

/**
 * Category and plant API step definitions for tester 215564H.
 * Tests require setup of categories and plants via background steps, with instance-level
 * IDs to track created resources across Given/When/Then in the same scenario.
 */
public class CategoryPlantApiSteps extends ApiStepSupport {

    // Test-data IDs local to 215564H's scenarios.
    private int createdCategoryId;
    private int categoryForDeleteId;
    private int categoryForUserTestId;
    private int plantId;
    private int categoryForPlantId;

    // ── Background steps ──────────────────────────────────────────────────────

    @Given("admin API token is available")
    public void admin_api_token_is_available() {
        ApiTestContext.State ctx = ApiTestContext.context();
        APIResponse adminLogin = ctx.auth.login(adminUsername(), adminPassword());
        Assert.assertEquals(adminLogin.status(), 200, "Admin login failed: " + adminLogin.text());
        ctx.adminToken = ctx.auth.extractToken(adminLogin);
        Assert.assertNotNull(ctx.adminToken, "Admin token is null");
        Assert.assertFalse(ctx.adminToken.isBlank(), "Admin token is blank");
        System.out.println("ADMIN TOKEN: " + ctx.adminToken);

        createdCategoryId = createCategoryAsAdmin("CatUpd");
        categoryForDeleteId = createCategoryAsAdmin("CatDel");
        System.out.println("CREATED CATEGORY ID: " + createdCategoryId);
    }

    @Given("non admin API token is available")
    public void non_admin_api_token_is_available() {
        ApiTestContext.State ctx = ApiTestContext.context();

        APIResponse userLogin = ctx.auth.login(userUsername(), userPassword());
        Assert.assertEquals(userLogin.status(), 200, "User login failed: " + userLogin.text());
        ctx.userToken = ctx.auth.extractToken(userLogin);

        APIResponse adminLogin = ctx.auth.login(adminUsername(), adminPassword());
        Assert.assertEquals(adminLogin.status(), 200, "Admin login failed: " + adminLogin.text());
        ctx.adminToken = ctx.auth.extractToken(adminLogin);

        categoryForUserTestId = createCategoryAsAdmin("UserCat");
        int subCategoryId = createSubCategoryAsAdmin("UserSubCat", categoryForUserTestId);
        categoryForPlantId = subCategoryId;
        plantId = createPlantAsAdmin(subCategoryId, "UserPlant");
    }

    // ── Admin category steps ──────────────────────────────────────────────────

    @When("admin sends PUT request to update category")
    public void admin_updates_a_category() {
        ApiTestContext.State ctx = ApiTestContext.context();
        System.out.println("PUT CATEGORY ID: " + createdCategoryId);
        remember(ctx.categories.update(createdCategoryId, "UpdatedCat", null, ctx.adminToken));
    }

    @When("admin deletes a category")
    public void admin_deletes_a_category() {
        ApiTestContext.State ctx = ApiTestContext.context();
        remember(ctx.categories.delete(categoryForDeleteId, ctx.adminToken));
    }

    @Then("deleted category should not be found")
    public void deleted_category_should_not_be_found() {
        ApiTestContext.State ctx = ApiTestContext.context();
        APIResponse getDeleted = ctx.categories.getById(categoryForDeleteId, ctx.adminToken);
        Assert.assertEquals(getDeleted.status(), 404,
                "Deleted category still found. Response: " + getDeleted.text());
    }

    @When("admin creates category with name {string}")
    public void admin_creates_category_with_name(String categoryName) {
        ApiTestContext.State ctx = ApiTestContext.context();
        remember(ctx.categories.create(categoryName, ctx.adminToken));
    }

    @When("admin gets all plants")
    public void admin_gets_all_plants() {
        ApiTestContext.State ctx = ApiTestContext.context();
        remember(ctx.plants.getAll(ctx.adminToken));
    }

    // ── Non-admin category steps ──────────────────────────────────────────────

    @When("non admin user tries to update a category")
    public void non_admin_user_tries_to_update_a_category() {
        ApiTestContext.State ctx = ApiTestContext.context();
        remember(ctx.categories.update(categoryForUserTestId, "UserUpdate", null, ctx.userToken));
    }

    @When("non admin user tries to delete a category")
    public void non_admin_user_tries_to_delete_a_category() {
        ApiTestContext.State ctx = ApiTestContext.context();
        remember(ctx.categories.delete(categoryForUserTestId, ctx.userToken));
    }

    // ── Non-admin plant steps ─────────────────────────────────────────────────

    @When("non admin user gets all plants")
    public void non_admin_user_gets_all_plants() {
        ApiTestContext.State ctx = ApiTestContext.context();
        remember(ctx.plants.getAll(ctx.userToken));
    }

    @When("non admin user gets a plant by ID")
    public void non_admin_user_gets_a_plant_by_id() {
        ApiTestContext.State ctx = ApiTestContext.context();
        remember(ctx.plants.getById(plantId, ctx.userToken));
    }

    @When("non admin user tries to create a plant")
    public void non_admin_user_tries_to_create_a_plant() {
        ApiTestContext.State ctx = ApiTestContext.context();
        remember(ctx.plants.create(categoryForPlantId, "UserPlant", 150, 25, ctx.userToken));
    }

    // ── Common assertions ─────────────────────────────────────────────────────

    @Then("API response status should be {int}")
    public void api_response_status_should_be(Integer expectedStatus) {
        APIResponse response = ApiTestContext.context().lastResponse;
        Assert.assertNotNull(response, "API response is null");
        Assert.assertEquals(response.status(), expectedStatus.intValue(),
                "Unexpected status. Response body: " + response.text());
    }

    @Then("API response should contain category name {string}")
    public void api_response_should_contain_category_name(String expectedName) {
        Assert.assertTrue(lastBody().contains(expectedName),
                "Expected category name not found. Response: " + lastBody());
    }

    @Then("API response should contain message {string}")
    public void api_response_should_contain_message(String expectedMessage) {
        Assert.assertTrue(lastBody().contains(expectedMessage),
                "Expected message not found. Response: " + lastBody());
    }

    // ── Private data setup helpers ────────────────────────────────────────────

    private int createCategoryAsAdmin(String categoryName) {
        ApiTestContext.State ctx = ApiTestContext.context();
        APIResponse res = ctx.categories.create(categoryName, ctx.adminToken);

        if (res.status() == 400 && res.text().contains("already exists")) {
            APIResponse getCats = ctx.categories.getAll(ctx.adminToken);
            if (getCats.status() == 200) {
                for (JsonElement el : JsonParser.parseString(getCats.text()).getAsJsonArray()) {
                    JsonObject c = el.getAsJsonObject();
                    if (c.get("name").getAsString().equalsIgnoreCase(categoryName)) return c.get("id").getAsInt();
                }
            }
        }

        Assert.assertTrue(res.status() == 200 || res.status() == 201,
                "Category setup failed. Status: " + res.status() + " Body: " + res.text());
        return JsonParser.parseString(res.text()).getAsJsonObject().get("id").getAsInt();
    }

    private int createSubCategoryAsAdmin(String categoryName, int parentId) {
        ApiTestContext.State ctx = ApiTestContext.context();
        APIResponse res = ctx.categories.createSub(categoryName, parentId, ctx.adminToken);

        if (res.status() == 400 && res.text().contains("already exists")) {
            APIResponse getCats = ctx.categories.getAll(ctx.adminToken);
            if (getCats.status() == 200) {
                for (JsonElement el : JsonParser.parseString(getCats.text()).getAsJsonArray()) {
                    JsonObject c = el.getAsJsonObject();
                    if (c.get("name").getAsString().equalsIgnoreCase(categoryName)) return c.get("id").getAsInt();
                }
            }
        }

        Assert.assertTrue(res.status() == 200 || res.status() == 201,
                "Sub-category setup failed. Status: " + res.status() + " Body: " + res.text());
        return JsonParser.parseString(res.text()).getAsJsonObject().get("id").getAsInt();
    }

    private int createPlantAsAdmin(int categoryId, String plantName) {
        ApiTestContext.State ctx = ApiTestContext.context();
        APIResponse res = ctx.plants.create(categoryId, plantName, 150, 25, ctx.adminToken);

        if (res.status() == 400 && res.text().contains("already exists")) {
            APIResponse getPlants = ctx.plants.getAll(ctx.adminToken);
            if (getPlants.status() == 200) {
                JsonElement parsed = JsonParser.parseString(getPlants.text());
                JsonArray plantsArray = parsed.isJsonArray() ? parsed.getAsJsonArray()
                        : parsed.getAsJsonObject().has("content")
                                ? parsed.getAsJsonObject().getAsJsonArray("content") : null;
                if (plantsArray != null) {
                    for (JsonElement el : plantsArray) {
                        JsonObject p = el.getAsJsonObject();
                        if (p.get("name").getAsString().equalsIgnoreCase(plantName)) return p.get("id").getAsInt();
                    }
                }
            }
        }

        Assert.assertTrue(res.status() == 200 || res.status() == 201,
                "Plant setup failed. Status: " + res.status() + " Body: " + res.text());
        return JsonParser.parseString(res.text()).getAsJsonObject().get("id").getAsInt();
    }
}
