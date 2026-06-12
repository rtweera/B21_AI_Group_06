package stepdefinitions.ui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import utils.ConfigReader;

import java.util.ArrayList;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Category UI step definitions.
 * Covers 215527A admin category scenarios and 215564H admin/non-admin category scenarios.
 */
public class CategoryUiSteps extends UiStepSupport {

    private String searchedCategoryName;

    // ── Data setup @Before ────────────────────────────────────────────────────
    // Each scenario gets fresh test data appropriate to what it tests.

    @Before(value = "@UI", order = 1)
    public void ensureDataExists(io.cucumber.java.Scenario scenario) {
        String name = scenario.getName();
        if (name.contains("UI_CAT_ADM_007")) {
            cleanUiTestData();
        } else if (name.contains("UI_CAT_ADM_008")) {
            deleteCategoryByName("SubCat");
            ensureCategoryExists("TestCat");
        } else {
            ensureCategoryExists("TestCat");
            ensureSubCategoryExists("TestSubCat", "TestCat");
            ensurePlantExists("plant", "TestSubCat");
            ensurePlantExists("plant 2", "TestSubCat");
        }
    }

    // ── Step definitions (215527A) ────────────────────────────────────────────

    @When("I open the Categories page")
    public void iOpenTheCategoriesPage() {
        categoriesPage().open();
    }

    @When("I search for an existing category by name")
    public void iSearchForAnExistingCategoryByName() {
        searchedCategoryName = categoriesPage().firstCategoryName();
        categoriesPage().searchByName(searchedCategoryName);
    }

    @Then("the Add A Category button should be visible")
    public void addCategoryButtonShouldBeVisible() {
        categoriesPage().assertAddCategoryVisible();
    }

    @Then("every category row should show edit and delete actions")
    public void everyCategoryRowShouldShowEditAndDeleteActions() {
        categoriesPage().assertEveryRowHasEditAndDeleteActions();
    }

    @Then("only categories matching that name should be visible")
    public void onlyCategoriesMatchingThatNameShouldBeVisible() {
        categoriesPage().assertOnlyRowsContaining(searchedCategoryName);
    }

    @Then("no category management buttons should be visible")
    public void noCategoryManagementButtonsShouldBeVisible() {
        categoriesPage().assertNoManagementControlsVisible();
    }

    // ── Step definitions (215564H) ────────────────────────────────────────────

    @Given("admin user is logged in")
    public void admin_user_is_logged_in() {
        loginPage().navigateToLogin();
        loginPage().login(ConfigReader.getAdminUsername(), ConfigReader.getAdminPassword());
    }

    @Given("non admin user is logged in")
    public void non_admin_user_is_logged_in() {
        loginPage().navigateToLogin();
        loginPage().login(ConfigReader.getUserUsername(), ConfigReader.getUserPassword());
    }

    @When("admin navigates to Categories page")
    public void admin_navigates_to_categories_page() {
        categoriesPage().goToCategories();
    }

    @When("user navigates to Categories page")
    public void user_navigates_to_categories_page() {
        categoriesPage().goToCategories();
    }

    @When("admin clicks Add A Category button")
    public void admin_clicks_add_category_button() {
        categoriesPage().clickAddCategory();
    }

    @When("admin enters category name {string}")
    public void admin_enters_category_name(String name) {
        categoriesPage().enterCategoryName(name);
    }

    @When("admin clicks Cancel button")
    public void admin_clicks_cancel_button() {
        categoriesPage().clickCancel();
    }

    @When("admin clicks Save button")
    public void admin_clicks_save_button() {
        categoriesPage().clickSave();
    }

    @When("admin selects parent category {string}")
    public void admin_selects_parent_category(String parentCategory) {
        categoriesPage().selectParentCategory(parentCategory);
    }

    @Then("sub category {string} should be visible with parent category {string}")
    public void sub_category_should_be_visible_with_parent_category(String subCategory, String parentCategory) {
        categoriesPage().verifySubCategoryWithParent(subCategory, parentCategory);
    }

    @Then("admin should be redirected to Add Category page")
    public void admin_should_be_redirected_to_add_category_page() {
        assertThat(page()).hasURL(ConfigReader.getBaseUrl() + "/ui/categories/add");
    }

    @Then("saved category {string} should be visible in category list")
    public void saved_category_should_be_visible(String categoryName) {
        categoriesPage().verifyCategoryVisible(categoryName);
    }

    @Then("category name should be entered successfully")
    public void category_name_should_be_entered_successfully() {
        categoriesPage().verifyCategoryNameEntered("TestCat");
    }

    @Then("admin should be redirected to Categories page")
    public void admin_should_be_redirected_to_categories_page() {
        assertThat(page()).hasURL(ConfigReader.getBaseUrl() + "/ui/categories");
    }

    @Then("if categories are empty {string} should be displayed")
    public void if_categories_are_empty_should_be_displayed(String expectedMessage) {
        boolean hasRows = page().locator("table tbody tr").count() > 0;
        if (!hasRows) {
            categoriesPage().verifyNoCategoryFoundMessage(expectedMessage);
        } else {
            System.out.println("Categories are not empty, skipping 'No category found' assertion.");
        }
    }

    @Then("category edit and delete buttons should not be clickable")
    public void category_edit_and_delete_buttons_should_not_be_clickable() {
        categoriesPage().verifyEditAndDeleteButtonsNotClickable();
    }

    // ── Private data setup helpers ────────────────────────────────────────────

    private void cleanUiTestData() {
        deleteCategoryByName("SubCat");
        deleteCategoryByName("TestCat");
    }

    private void ensureCategoryExists(String categoryName) {
        try (Playwright pw = Playwright.create()) {
            APIRequestContext req = apiContext(pw);
            String token = adminToken(req);
            req.post("/api/categories",
                    RequestOptions.create()
                            .setHeader("Authorization", "Bearer " + token)
                            .setHeader("Content-Type", "application/json")
                            .setData("{\"name\":\"" + categoryName + "\"}"));
        } catch (Exception e) {
            System.out.println("ensureCategoryExists failed: " + e.getMessage());
        }
    }

    private void ensureSubCategoryExists(String subCategoryName, String parentCategoryName) {
        try (Playwright pw = Playwright.create()) {
            APIRequestContext req = apiContext(pw);
            String token = adminToken(req);

            int parentId = getCategoryIdByName(req, token, parentCategoryName);
            if (parentId == 0) {
                APIResponse res = req.post("/api/categories",
                        RequestOptions.create()
                                .setHeader("Authorization", "Bearer " + token)
                                .setHeader("Content-Type", "application/json")
                                .setData("{\"name\":\"" + parentCategoryName + "\"}"));
                if (res.status() == 200 || res.status() == 201) {
                    JsonObject j = JsonParser.parseString(res.text()).getAsJsonObject();
                    parentId = j.has("id") ? j.get("id").getAsInt()
                            : j.getAsJsonObject("content").get("id").getAsInt();
                }
            }

            int subId = getCategoryIdByName(req, token, subCategoryName);
            if (subId > 0 && parentId > 0) {
                APIResponse getCat = req.get("/api/categories/" + subId,
                        RequestOptions.create().setHeader("Authorization", "Bearer " + token));
                if (getCat.status() == 200) {
                    JsonObject catObj = JsonParser.parseString(getCat.text()).getAsJsonObject();
                    int actualParent = catObj.has("parentId") && !catObj.get("parentId").isJsonNull()
                            ? catObj.get("parentId").getAsInt() : 0;
                    if (actualParent != parentId) {
                        System.out.println("Subcategory parent incorrect. Deleting: " + subCategoryName);
                        deletePlantsByCategoryName(req, token, subCategoryName);
                        req.delete("/api/categories/" + subId,
                                RequestOptions.create().setHeader("Authorization", "Bearer " + token));
                        subId = 0;
                    }
                }
            }

            if (subId == 0 && parentId > 0) {
                req.post("/api/categories",
                        RequestOptions.create()
                                .setHeader("Authorization", "Bearer " + token)
                                .setHeader("Content-Type", "application/json")
                                .setData("{\"name\":\"" + subCategoryName + "\",\"parent\":{\"id\":" + parentId + "}}"));
            }
        } catch (Exception e) {
            System.out.println("ensureSubCategoryExists failed: " + e.getMessage());
        }
    }

    private void ensurePlantExists(String plantName, String subCategoryName) {
        try (Playwright pw = Playwright.create()) {
            APIRequestContext req = apiContext(pw);
            String token = adminToken(req);
            int catId = getCategoryIdByName(req, token, subCategoryName);
            if (catId > 0) {
                APIResponse res = req.post("/api/plants/category/" + catId,
                        RequestOptions.create()
                                .setHeader("Authorization", "Bearer " + token)
                                .setHeader("Content-Type", "application/json")
                                .setData("{\"name\":\"" + plantName + "\",\"price\":150,\"quantity\":25}"));
                System.out.println("CREATE PLANT " + plantName + " STATUS: " + res.status());
            }
        } catch (Exception e) {
            System.out.println("ensurePlantExists failed: " + e.getMessage());
        }
    }

    private void deleteCategoryByName(String categoryName) {
        try (Playwright pw = Playwright.create()) {
            APIRequestContext req = apiContext(pw);
            String token = adminToken(req);
            boolean foundAndDeleted;
            do {
                foundAndDeleted = false;
                APIResponse getCats = req.get("/api/categories",
                        RequestOptions.create().setHeader("Authorization", "Bearer " + token));
                if (getCats.status() == 200) {
                    JsonArray catsArray = JsonParser.parseString(getCats.text()).getAsJsonArray();
                    int targetId = 0;
                    List<Integer> subIds = new ArrayList<>();
                    List<String> subNames = new ArrayList<>();

                    for (JsonElement el : catsArray) {
                        JsonObject c = el.getAsJsonObject();
                        if (c.get("name").getAsString().equalsIgnoreCase(categoryName)) {
                            targetId = c.get("id").getAsInt();
                            break;
                        }
                    }
                    if (targetId > 0) {
                        for (JsonElement el : catsArray) {
                            JsonObject c = el.getAsJsonObject();
                            if (c.has("parentName") && !c.get("parentName").isJsonNull()
                                    && c.get("parentName").getAsString().equalsIgnoreCase(categoryName)) {
                                int sid = c.get("id").getAsInt();
                                if (!subIds.contains(sid)) {
                                    subIds.add(sid);
                                    subNames.add(c.get("name").getAsString());
                                }
                            }
                        }
                        for (String sn : subNames) deletePlantsByCategoryName(req, token, sn);
                        deletePlantsByCategoryName(req, token, categoryName);
                        for (int sid : subIds) {
                            req.delete("/api/categories/" + sid,
                                    RequestOptions.create().setHeader("Authorization", "Bearer " + token));
                        }
                        APIResponse delRes = req.delete("/api/categories/" + targetId,
                                RequestOptions.create().setHeader("Authorization", "Bearer " + token));
                        System.out.println("DELETE CATEGORY " + categoryName + " (ID " + targetId + ") STATUS: " + delRes.status());
                        foundAndDeleted = true;
                    }
                }
            } while (foundAndDeleted);
        } catch (Exception e) {
            System.out.println("deleteCategoryByName failed: " + e.getMessage());
        }
    }

    private void deletePlantsByCategoryName(APIRequestContext req, String token, String categoryName) {
        try {
            APIResponse getPlants = req.get("/api/plants",
                    RequestOptions.create().setHeader("Authorization", "Bearer " + token));
            if (getPlants.status() == 200) {
                JsonElement parsed = JsonParser.parseString(getPlants.text());
                JsonArray plants = parsed.isJsonArray() ? parsed.getAsJsonArray()
                        : parsed.getAsJsonObject().has("content")
                                ? parsed.getAsJsonObject().getAsJsonArray("content") : null;
                if (plants != null) {
                    for (JsonElement el : plants) {
                        JsonObject p = el.getAsJsonObject();
                        if (p.has("category") && !p.get("category").isJsonNull()) {
                            JsonObject cat = p.getAsJsonObject("category");
                            if (cat.get("name").getAsString().equalsIgnoreCase(categoryName)) {
                                int id = p.get("id").getAsInt();
                                APIResponse del = req.delete("/api/plants/" + id,
                                        RequestOptions.create().setHeader("Authorization", "Bearer " + token));
                                System.out.println("DELETE PLANT " + p.get("name").getAsString() + " STATUS: " + del.status());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("deletePlantsByCategoryName failed: " + e.getMessage());
        }
    }

    private int getCategoryIdByName(APIRequestContext req, String token, String name) {
        APIResponse res = req.get("/api/categories",
                RequestOptions.create().setHeader("Authorization", "Bearer " + token));
        if (res.status() == 200) {
            for (JsonElement el : JsonParser.parseString(res.text()).getAsJsonArray()) {
                JsonObject c = el.getAsJsonObject();
                if (c.get("name").getAsString().equalsIgnoreCase(name)) return c.get("id").getAsInt();
            }
        }
        return 0;
    }

    private APIRequestContext apiContext(Playwright pw) {
        return pw.request().newContext(
                new APIRequest.NewContextOptions().setBaseURL(ConfigReader.getBaseUrl()));
    }

    private String adminToken(APIRequestContext req) {
        String body = "{\"username\":\"" + ConfigReader.getAdminUsername()
                + "\",\"password\":\"" + ConfigReader.getAdminPassword() + "\"}";
        APIResponse res = req.post("/api/auth/login",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(body));
        if (res.status() == 200) {
            JsonObject json = JsonParser.parseString(res.text()).getAsJsonObject();
            return json.has("token") ? json.get("token").getAsString()
                    : json.getAsJsonObject("content").get("token").getAsString();
        }
        throw new RuntimeException("Admin login failed: " + res.text());
    }
}
