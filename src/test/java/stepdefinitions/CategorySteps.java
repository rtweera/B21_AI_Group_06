package stepdefinitions;

import hooks.UiHooks;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import pages.LoginPage;
import pages.CategoriesPage;
import pages.PlantsPage;
import com.microsoft.playwright.APIRequest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CategorySteps {
    private LoginPage loginPage;
    private CategoriesPage categoriesPage;
    private PlantsPage plantsPage;

    @Before(value = "@UI", order = 1)
    public void ensureDataExists(io.cucumber.java.Scenario scenario) {
        String scenarioName = scenario.getName();
        if (scenarioName.contains("UI_CAT_ADM_007")) {
            cleanUiTestData();
        } else if (scenarioName.contains("UI_CAT_ADM_008")) {
            deleteCategoryByName("SubCat");
            ensureCategoryExists("TestCat");
        } else {
            ensureCategoryExists("TestCat");
            ensureSubCategoryExists("TestSubCat", "TestCat");
            ensurePlantExists("plant", "TestSubCat");
            ensurePlantExists("plant 2", "TestSubCat");
        }
    }

    private void cleanUiTestData() {
        deleteCategoryByName("SubCat");
        deleteCategoryByName("TestCat");
    }

    private void deletePlantsByCategoryName(APIRequestContext request, String token, String categoryName) {
        try {
            APIResponse getPlants = request.get("/api/plants", 
                RequestOptions.create().setHeader("Authorization", "Bearer " + token));
            if (getPlants.status() == 200) {
                JsonArray plantsArray = null;
                JsonElement parsed = JsonParser.parseString(getPlants.text());
                if (parsed.isJsonArray()) {
                    plantsArray = parsed.getAsJsonArray();
                } else if (parsed.isJsonObject() && parsed.getAsJsonObject().has("content")) {
                    plantsArray = parsed.getAsJsonObject().getAsJsonArray("content");
                }
                if (plantsArray != null) {
                    for (JsonElement el : plantsArray) {
                        JsonObject p = el.getAsJsonObject();
                        if (p.has("category") && !p.get("category").isJsonNull()) {
                            JsonObject cat = p.getAsJsonObject("category");
                            if (cat.get("name").getAsString().equalsIgnoreCase(categoryName)) {
                                int plantId = p.get("id").getAsInt();
                                APIResponse delRes = request.delete("/api/plants/" + plantId, 
                                    RequestOptions.create().setHeader("Authorization", "Bearer " + token));
                                System.out.println("DELETE PLANT " + p.get("name").getAsString() + " (ID " + plantId + ") STATUS: " + delRes.status());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("deletePlantsByCategoryName failed: " + e.getMessage());
        }
    }

    private void deleteCategoryByName(String categoryName) {
        try (Playwright playwright = Playwright.create()) {
            APIRequestContext request = playwright.request().newContext(
                new APIRequest.NewContextOptions().setBaseURL("http://localhost:8080")
            );
            String loginBody = "{\"username\":\"admin\",\"password\":\"admin123\"}";
            APIResponse loginRes = request.post("/api/auth/login", 
                RequestOptions.create().setHeader("Content-Type", "application/json").setData(loginBody));
            if (loginRes.status() == 200) {
                JsonObject json = JsonParser.parseString(loginRes.text()).getAsJsonObject();
                String token = json.has("token") ? json.get("token").getAsString() : 
                               json.getAsJsonObject("content").get("token").getAsString();
                
                boolean foundAndDeleted;
                do {
                    foundAndDeleted = false;
                    APIResponse getCats = request.get("/api/categories", 
                        RequestOptions.create().setHeader("Authorization", "Bearer " + token));
                    if (getCats.status() == 200) {
                        JsonArray catsArray = JsonParser.parseString(getCats.text()).getAsJsonArray();
                        int targetCatId = 0;
                        java.util.List<Integer> subCatIds = new java.util.ArrayList<>();
                        java.util.List<String> subCatNames = new java.util.ArrayList<>();
                        
                        for (JsonElement el : catsArray) {
                            JsonObject c = el.getAsJsonObject();
                            if (c.get("name").getAsString().equalsIgnoreCase(categoryName)) {
                                targetCatId = c.get("id").getAsInt();
                                break;
                            }
                        }
                        
                        if (targetCatId > 0) {
                            for (JsonElement el : catsArray) {
                                JsonObject c = el.getAsJsonObject();
                                if (c.has("parentName") && !c.get("parentName").isJsonNull()) {
                                    String pName = c.get("parentName").getAsString();
                                    if (pName.equalsIgnoreCase(categoryName)) {
                                        int sid = c.get("id").getAsInt();
                                        String sname = c.get("name").getAsString();
                                        if (!subCatIds.contains(sid)) {
                                            subCatIds.add(sid);
                                            subCatNames.add(sname);
                                        }
                                    }
                                }
                            }
                            
                            for (String subName : subCatNames) {
                                deletePlantsByCategoryName(request, token, subName);
                            }
                            
                            deletePlantsByCategoryName(request, token, categoryName);
                            
                            for (int subId : subCatIds) {
                                request.delete("/api/categories/" + subId, 
                                    RequestOptions.create().setHeader("Authorization", "Bearer " + token));
                            }
                            
                            APIResponse delRes = request.delete("/api/categories/" + targetCatId, 
                                RequestOptions.create().setHeader("Authorization", "Bearer " + token));
                            System.out.println("DELETE CATEGORY " + categoryName + " (ID " + targetCatId + ") STATUS: " + delRes.status());
                            foundAndDeleted = true;
                        }
                    }
                } while (foundAndDeleted);
            }
        } catch (Exception e) {
            System.out.println("deleteCategoryByName failed: " + e.getMessage());
        }
    }

    @Before(value = "@UI", order = 3)
    public void initPages() {
        loginPage = new LoginPage(UiHooks.page);
        categoriesPage = new CategoriesPage(UiHooks.page);
        plantsPage = new PlantsPage(UiHooks.page);
    }

    private void ensureCategoryExists(String categoryName) {
        try (Playwright playwright = Playwright.create()) {
            APIRequestContext request = playwright.request().newContext(
                new APIRequest.NewContextOptions().setBaseURL("http://localhost:8080")
            );
            String loginBody = "{\"username\":\"admin\",\"password\":\"admin123\"}";
            APIResponse loginRes = request.post("/api/auth/login", 
                RequestOptions.create().setHeader("Content-Type", "application/json").setData(loginBody));
            if (loginRes.status() == 200) {
                JsonObject json = JsonParser.parseString(loginRes.text()).getAsJsonObject();
                String token = json.has("token") ? json.get("token").getAsString() : 
                               json.getAsJsonObject("content").get("token").getAsString();
                
                String catBody = "{\"name\":\"" + categoryName + "\"}";
                request.post("/api/categories", 
                    RequestOptions.create()
                        .setHeader("Authorization", "Bearer " + token)
                        .setHeader("Content-Type", "application/json")
                        .setData(catBody));
            }
        } catch (Exception e) {
            System.out.println("ensureCategoryExists failed: " + e.getMessage());
        }
    }

    private void ensureSubCategoryExists(String subCategoryName, String parentCategoryName) {
        try (Playwright playwright = Playwright.create()) {
            APIRequestContext request = playwright.request().newContext(
                new APIRequest.NewContextOptions().setBaseURL("http://localhost:8080")
            );
            String loginBody = "{\"username\":\"admin\",\"password\":\"admin123\"}";
            APIResponse loginRes = request.post("/api/auth/login", 
                RequestOptions.create().setHeader("Content-Type", "application/json").setData(loginBody));
            if (loginRes.status() == 200) {
                JsonObject json = JsonParser.parseString(loginRes.text()).getAsJsonObject();
                String token = json.has("token") ? json.get("token").getAsString() : 
                               json.getAsJsonObject("content").get("token").getAsString();
                
                int parentId = getCategoryIdByName(request, token, parentCategoryName);
                if (parentId == 0) {
                    String parentBody = "{\"name\":\"" + parentCategoryName + "\"}";
                    APIResponse parentRes = request.post("/api/categories", 
                        RequestOptions.create()
                            .setHeader("Authorization", "Bearer " + token)
                            .setHeader("Content-Type", "application/json")
                            .setData(parentBody));
                    if (parentRes.status() == 200 || parentRes.status() == 201) {
                        JsonObject parentJson = JsonParser.parseString(parentRes.text()).getAsJsonObject();
                        parentId = parentJson.has("id") ? parentJson.get("id").getAsInt() : 
                                   parentJson.getAsJsonObject("content").get("id").getAsInt();
                    }
                }
                
                int subId = getCategoryIdByName(request, token, subCategoryName);
                if (subId > 0 && parentId > 0) {
                    APIResponse getCat = request.get("/api/categories/" + subId, 
                        RequestOptions.create().setHeader("Authorization", "Bearer " + token));
                    if (getCat.status() == 200) {
                        JsonObject catObj = JsonParser.parseString(getCat.text()).getAsJsonObject();
                        int actualParentId = catObj.has("parentId") && !catObj.get("parentId").isJsonNull() ? 
                                             catObj.get("parentId").getAsInt() : 0;
                        if (actualParentId != parentId) {
                            System.out.println("Subcategory parent incorrect. Deleting existing: " + subCategoryName);
                            deletePlantsByCategoryName(request, token, subCategoryName);
                            request.delete("/api/categories/" + subId, 
                                RequestOptions.create().setHeader("Authorization", "Bearer " + token));
                            subId = 0;
                        }
                    }
                }
                
                if (subId == 0 && parentId > 0) {
                    String subBody = "{\"name\":\"" + subCategoryName + "\",\"parent\":{\"id\":" + parentId + "}}";
                    request.post("/api/categories", 
                        RequestOptions.create()
                            .setHeader("Authorization", "Bearer " + token)
                            .setHeader("Content-Type", "application/json")
                            .setData(subBody));
                }
            }
        } catch (Exception e) {
            System.out.println("ensureSubCategoryExists failed: " + e.getMessage());
        }
    }

    private int getCategoryIdByName(APIRequestContext request, String token, String name) {
        APIResponse getCats = request.get("/api/categories", 
            RequestOptions.create().setHeader("Authorization", "Bearer " + token));
        if (getCats.status() == 200) {
            JsonArray catsArray = JsonParser.parseString(getCats.text()).getAsJsonArray();
            for (JsonElement el : catsArray) {
                JsonObject c = el.getAsJsonObject();
                if (c.get("name").getAsString().equalsIgnoreCase(name)) {
                    return c.get("id").getAsInt();
                }
            }
        }
        return 0;
    }

    private void ensurePlantExists(String plantName, String subCategoryName) {
        try (Playwright playwright = Playwright.create()) {
            APIRequestContext request = playwright.request().newContext(
                new APIRequest.NewContextOptions().setBaseURL("http://localhost:8080")
            );
            String loginBody = "{\"username\":\"admin\",\"password\":\"admin123\"}";
            APIResponse loginRes = request.post("/api/auth/login", 
                RequestOptions.create().setHeader("Content-Type", "application/json").setData(loginBody));
            if (loginRes.status() == 200) {
                JsonObject json = JsonParser.parseString(loginRes.text()).getAsJsonObject();
                String token = json.has("token") ? json.get("token").getAsString() : 
                               json.getAsJsonObject("content").get("token").getAsString();
                
                int catId = getCategoryIdByName(request, token, subCategoryName);
                if (catId > 0) {
                    String plantBody = "{\"name\":\"" + plantName + "\",\"price\":150,\"quantity\":25}";
                    APIResponse plantRes = request.post("/api/plants/category/" + catId, 
                        RequestOptions.create()
                            .setHeader("Authorization", "Bearer " + token)
                            .setHeader("Content-Type", "application/json")
                            .setData(plantBody));
                    System.out.println("CREATE PLANT " + plantName + " STATUS: " + plantRes.status() + " Body: " + plantRes.text());
                }
            }
        } catch (Exception e) {
            System.out.println("ensurePlantExists failed: " + e.getMessage());
        }
    }

    @Given("admin user is logged in")
    public void admin_user_is_logged_in(){
        loginPage.navigateToLogin();
        loginPage.login("admin","admin123");
    }

    @Given("non admin user is logged in")
    public void non_admin_user_is_logged_in() {
        loginPage.navigateToLogin();
        loginPage.login("testuser", "test123");
    }

    @When("admin navigates to Categories page")
    public void admin_navigates_to_categories_page(){
        categoriesPage.goToCategories();
    }

    @When("user navigates to Categories page")
    public void user_navigates_to_categories_page() {
        categoriesPage.goToCategories();
    }

    @When("admin clicks Add A Category button")
    public void admin_clicks_add_category_button(){
        categoriesPage.clickAddCategory();
    }

    @When("admin enters category name {string}")
    public void admin_enters_category_name(String name){
        categoriesPage.enterCategoryName(name);
    }

    @When("admin clicks Cancel button")
    public void admin_clicks_cancel_button(){
        categoriesPage.clickCancel();
    }

    @When("admin clicks Save button")
    public void admin_clicks_save_button(){
        categoriesPage.clickSave();
    }

    @When("admin selects parent category {string}")
    public void admin_selects_parent_category(String parentCategory) {
        categoriesPage.selectParentCategory(parentCategory);
    }

    @Then("sub category {string} should be visible with parent category {string}")
    public void sub_category_should_be_visible_with_parent_category(String subCategory, String parentCategory) {
        categoriesPage.verifySubCategoryWithParent(subCategory, parentCategory);
    }

    @Then("admin should be redirected to Add Category page")
    public void admin_should_be_redirected_to_add_category_page(){
        assertThat(UiHooks.page).hasURL("http://localhost:8080/ui/categories/add");
    }

    @Then("saved category {string} should be visible in category list")
    public void saved_category_should_be_visible(String categoryName){
        categoriesPage.verifyCategoryVisible(categoryName);
    }

    @Then("category name should be entered successfully")
    public void category_name_should_be_entered_successfully() {
        categoriesPage.verifyCategoryNameEntered("TestCat");
    }

    @Then("admin should be redirected to Categories page")
    public void admin_should_be_redirected_to_categories_page(){
        assertThat(UiHooks.page).hasURL("http://localhost:8080/ui/categories");
    }

    @Then("if categories are empty {string} should be displayed")
    public void if_categories_are_empty_should_be_displayed(String expectedMessage) {
        boolean hasRows = UiHooks.page.locator("table tbody tr").count() > 0;
        if (!hasRows) {
            categoriesPage.verifyNoCategoryFoundMessage(expectedMessage);
        } else {
            System.out.println("Categories are not empty, skipping 'No category found' assertion.");
        }
    }

    @Then("category edit and delete buttons should not be clickable")
    public void category_edit_and_delete_buttons_should_not_be_clickable() {
        categoriesPage.verifyEditAndDeleteButtonsNotClickable();
    }
}
