package stepdefinitions;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UiCategoriesSteps extends UiStepSupport {
    private static String searchedCategoryName;

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
}
