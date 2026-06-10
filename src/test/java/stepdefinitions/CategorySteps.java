package stepdefinitions;

import hooks.Hooks;
import io.cucumber.java.en.*;
import pages.LoginPage;
import pages.CategoriesPage;
import pages.PlantsPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CategorySteps {
    LoginPage loginPage = new LoginPage(Hooks.page);
    CategoriesPage categoriesPage = new CategoriesPage(Hooks.page);
    PlantsPage plantsPage = new PlantsPage(Hooks.page);

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
        assertThat(Hooks.page).hasURL("http://localhost:8080/ui/categories/add");
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
        assertThat(Hooks.page).hasURL("http://localhost:8080/ui/categories");
    }

    @Then("if categories are empty {string} should be displayed")
    public void if_categories_are_empty_should_be_displayed(String expectedMessage) {
        categoriesPage.verifyNoCategoryFoundMessage(expectedMessage);
    }

    @Then("category edit and delete buttons should not be clickable")
    public void category_edit_and_delete_buttons_should_not_be_clickable() {
        categoriesPage.verifyEditAndDeleteButtonsNotClickable();
    }
}
