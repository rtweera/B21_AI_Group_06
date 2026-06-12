package stepdefinitions;

import api.PlaywrightApiContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import io.cucumber.java.en.*;
import org.testng.Assert;

public class PlaywrightApiSteps {

    private String adminToken;
    private String userToken;

    private APIResponse response;

    private int createdCategoryId;
    private int categoryForDeleteId;
    private int categoryForUserTestId;
    private int plantId;
    private int categoryForPlantId;

    // -------------------------
    // Common helper methods
    // -------------------------

    private RequestOptions jsonOptions() {
        return RequestOptions.create()
                .setHeader("Content-Type", "application/json");
    }

    private RequestOptions authOptions(String token) {
        return RequestOptions.create()
                .setHeader("Authorization", "Bearer " + token.trim())
                .setHeader("Content-Type", "application/json");
    }

    private String extractToken(String responseText) {
        JsonObject json = JsonParser.parseString(responseText).getAsJsonObject();

        // Response format: {"token":"..."}
        if (json.has("token")) {
            return json.get("token").getAsString();
        }

        // Response format: {"status":200,"content":{"token":"..."}}
        if (json.has("content") && json.get("content").isJsonObject()) {
            JsonObject content = json.getAsJsonObject("content");
            if (content.has("token")) {
                return content.get("token").getAsString();
            }
        }

        throw new RuntimeException("Token not found in response: " + responseText);
    }

    private int extractId(String responseText) {
        JsonObject json = JsonParser.parseString(responseText).getAsJsonObject();

        // Response format: {"id":1}
        if (json.has("id")) {
            return json.get("id").getAsInt();
        }

        // Response format: {"status":201,"content":{"id":1}}
        if (json.has("content") && json.get("content").isJsonObject()) {
            JsonObject content = json.getAsJsonObject("content");
            if (content.has("id")) {
                return content.get("id").getAsInt();
            }
        }

        throw new RuntimeException("ID not found in response: " + responseText);
    }

    private int createCategoryAsAdmin(String categoryName) {
        String body = """
                {
                  "name": "%s"
                }
                """.formatted(categoryName);

        APIResponse createResponse = PlaywrightApiContext.request.post(
                "/api/categories",
                authOptions(adminToken).setData(body)
        );

        if (createResponse.status() == 400 && createResponse.text().contains("already exists")) {
            APIResponse getCats = PlaywrightApiContext.request.get(
                    "/api/categories",
                    authOptions(adminToken)
            );
            if (getCats.status() == 200) {
                com.google.gson.JsonArray catsArray = com.google.gson.JsonParser.parseString(getCats.text()).getAsJsonArray();
                for (com.google.gson.JsonElement el : catsArray) {
                    JsonObject c = el.getAsJsonObject();
                    if (c.get("name").getAsString().equalsIgnoreCase(categoryName)) {
                        return c.get("id").getAsInt();
                    }
                }
            }
        }

        Assert.assertTrue(
                createResponse.status() == 200 || createResponse.status() == 201,
                "Category setup failed. Status: " + createResponse.status()
                        + " Body: " + createResponse.text()
        );

        return extractId(createResponse.text());
    }

    private int createSubCategoryAsAdmin(String categoryName, int parentId) {
        String body = """
                {
                  "name": "%s",
                  "parent": {
                    "id": %d
                  }
                }
                """.formatted(categoryName, parentId);

        APIResponse createResponse = PlaywrightApiContext.request.post(
                "/api/categories",
                authOptions(adminToken).setData(body)
        );

        if (createResponse.status() == 400 && createResponse.text().contains("already exists")) {
            APIResponse getCats = PlaywrightApiContext.request.get(
                    "/api/categories",
                    authOptions(adminToken)
            );
            if (getCats.status() == 200) {
                com.google.gson.JsonArray catsArray = com.google.gson.JsonParser.parseString(getCats.text()).getAsJsonArray();
                for (com.google.gson.JsonElement el : catsArray) {
                    JsonObject c = el.getAsJsonObject();
                    if (c.get("name").getAsString().equalsIgnoreCase(categoryName)) {
                        return c.get("id").getAsInt();
                    }
                }
            }
        }

        Assert.assertTrue(
                createResponse.status() == 200 || createResponse.status() == 201,
                "Sub-category setup failed. Status: " + createResponse.status()
                        + " Body: " + createResponse.text()
        );

        return extractId(createResponse.text());
    }

    private int createPlantAsAdmin(int categoryId, String plantName) {
        String body = """
                {
                  "name": "%s",
                  "price": 150,
                  "quantity": 25
                }
                """.formatted(plantName);

        APIResponse createResponse = PlaywrightApiContext.request.post(
                "/api/plants/category/" + categoryId,
                authOptions(adminToken).setData(body)
        );

        if (createResponse.status() == 400 && createResponse.text().contains("already exists")) {
            APIResponse getPlants = PlaywrightApiContext.request.get(
                    "/api/plants",
                    authOptions(adminToken)
            );
            if (getPlants.status() == 200) {
                com.google.gson.JsonArray plantsArray = null;
                com.google.gson.JsonElement parsed = com.google.gson.JsonParser.parseString(getPlants.text());
                if (parsed.isJsonArray()) {
                    plantsArray = parsed.getAsJsonArray();
                } else if (parsed.isJsonObject() && parsed.getAsJsonObject().has("content")) {
                    plantsArray = parsed.getAsJsonObject().getAsJsonArray("content");
                }
                if (plantsArray != null) {
                    for (com.google.gson.JsonElement el : plantsArray) {
                        JsonObject p = el.getAsJsonObject();
                        if (p.get("name").getAsString().equalsIgnoreCase(plantName)) {
                            return p.get("id").getAsInt();
                        }
                    }
                }
            }
        }

        Assert.assertTrue(
                createResponse.status() == 200 || createResponse.status() == 201,
                "Plant setup failed. Status: " + createResponse.status()
                        + " Body: " + createResponse.text()
        );

        return extractId(createResponse.text());
    }

    // -------------------------
    // Background
    // -------------------------
    @Given("admin API token is available")
    public void admin_api_token_is_available() {

        String adminBody = """
            {
              "username": "admin",
              "password": "admin123"
            }
            """;

        APIResponse adminLogin = PlaywrightApiContext.request.post(
                "/api/auth/login",
                jsonOptions().setData(adminBody)
        );

        Assert.assertEquals(
                adminLogin.status(),
                200,
                "Admin login failed: " + adminLogin.text()
        );

        adminToken = extractToken(adminLogin.text());

        Assert.assertNotNull(adminToken, "Admin token is null");
        Assert.assertFalse(adminToken.isBlank(), "Admin token is blank");

        System.out.println("ADMIN TOKEN: " + adminToken);

        createdCategoryId = createCategoryAsAdmin("CatUpd");
        categoryForDeleteId = createCategoryAsAdmin("CatDel");

        System.out.println("CREATED CATEGORY ID: " + createdCategoryId);
    }
    @Given("non admin API token is available")
    public void non_admin_api_token_is_available() {
        String userBody = """
                {
                  "username": "testuser",
                  "password": "test123"
                }
                """;

        APIResponse userLogin = PlaywrightApiContext.request.post(
                "/api/auth/login",
                jsonOptions().setData(userBody)
        );

        Assert.assertEquals(
                userLogin.status(),
                200,
                "User login failed: " + userLogin.text()
        );

        userToken = extractToken(userLogin.text());

        // Log in as admin to create test data for non-admin tests
        String adminBody = """
                {
                  "username": "admin",
                  "password": "admin123"
                }
                """;

        APIResponse adminLogin = PlaywrightApiContext.request.post(
                "/api/auth/login",
                jsonOptions().setData(adminBody)
        );

        Assert.assertEquals(
                adminLogin.status(),
                200,
                "Admin login failed: " + adminLogin.text()
        );

        adminToken = extractToken(adminLogin.text());

        categoryForUserTestId = createCategoryAsAdmin("UserCat");
        int subCategoryId = createSubCategoryAsAdmin("UserSubCat", categoryForUserTestId);
        categoryForPlantId = subCategoryId;
        plantId = createPlantAsAdmin(subCategoryId, "UserPlant");
    }

    // -------------------------
    // Admin API test cases
    // -------------------------

    @When("admin sends PUT request to update category")
    public void admin_updates_a_category_using_playwright_api() {
        String body = """
                {
                  "name": "UpdatedCat",
                  "parentId": null
                }
                """;
        System.out.println("PUT CATEGORY ID: " + createdCategoryId);
        System.out.println("ADMIN TOKEN USED: Bearer " + adminToken);

        response = PlaywrightApiContext.request.put(
                "/api/categories/" + createdCategoryId,
                authOptions(adminToken).setData(body)
        );
    }

    @When("admin deletes a category")
    public void admin_deletes_a_category_using_playwright_api() {
        response = PlaywrightApiContext.request.delete(
                "/api/categories/" + categoryForDeleteId,
                authOptions(adminToken)
        );
    }

    @Then("deleted category should not be found")
    public void deleted_category_should_not_be_found() {
        APIResponse getDeleted = PlaywrightApiContext.request.get(
                "/api/categories/" + categoryForDeleteId,
                authOptions(adminToken)
        );

        Assert.assertEquals(
                getDeleted.status(),
                404,
                "Deleted category still found. Response: " + getDeleted.text()
        );
    }

    @When("admin creates category with name {string}")
    public void admin_creates_category_with_name(String categoryName) {
        String body = """
                {
                  "name": "%s"
                }
                """.formatted(categoryName);

        response = PlaywrightApiContext.request.post(
                "/api/categories",
                authOptions(adminToken).setData(body)
        );
    }

    @When("admin gets all plants")
    public void admin_gets_all_plants_using_playwright_api() {
        response = PlaywrightApiContext.request.get(
                "/api/plants",
                authOptions(adminToken)
        );
    }

    // -------------------------
    // Non-admin API test cases
    // -------------------------

    @When("non admin user tries to update a category")
    public void non_admin_user_tries_to_update_a_category_using_playwright_api() {
        String body = """
                {
                  "name": "UserUpdate",
                  "parentId": null
                }
                """;

        response = PlaywrightApiContext.request.put(
                "/api/categories/" + categoryForUserTestId,
                authOptions(userToken).setData(body)
        );
    }

    @When("non admin user tries to delete a category")
    public void non_admin_user_tries_to_delete_a_category_using_playwright_api() {
        response = PlaywrightApiContext.request.delete(
                "/api/categories/" + categoryForUserTestId,
                authOptions(userToken)
        );
    }

    @When("non admin user gets all plants")
    public void non_admin_user_gets_all_plants_using_playwright_api() {
        response = PlaywrightApiContext.request.get(
                "/api/plants",
                authOptions(userToken)
        );
    }

    @When("non admin user gets a plant by ID")
    public void non_admin_user_gets_a_plant_by_id_using_playwright_api() {
        response = PlaywrightApiContext.request.get(
                "/api/plants/" + plantId,
                authOptions(userToken)
        );
    }

    @When("non admin user tries to create a plant")
    public void non_admin_user_tries_to_create_a_plant_using_playwright_api() {
        String body = """
                {
                  "name": "UserPlant",
                  "price": 150,
                  "quantity": 25
                }
                """;

        response = PlaywrightApiContext.request.post(
                "/api/plants/category/" + categoryForPlantId,
                authOptions(userToken).setData(body)
        );
    }

    // -------------------------
    // Common assertions
    // -------------------------

    @Then("API response status should be {int}")
    public void api_response_status_should_be(Integer expectedStatus) {
        Assert.assertNotNull(response, "API response is null");

        Assert.assertEquals(
                response.status(),
                expectedStatus.intValue(),
                "Unexpected status. Response body: " + response.text()
        );
    }

    @Then("API response should contain category name {string}")
    public void api_response_should_contain_category_name(String expectedName) {
        Assert.assertTrue(
                response.text().contains(expectedName),
                "Expected category name not found. Response: " + response.text()
        );
    }

    @Then("API response should contain message {string}")
    public void api_response_should_contain_message(String expectedMessage) {
        Assert.assertTrue(
                response.text().contains(expectedMessage),
                "Expected message not found. Response: " + response.text()
        );
    }
}