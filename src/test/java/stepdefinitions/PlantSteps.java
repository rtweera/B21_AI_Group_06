package stepdefinitions;

import hooks.UiHooks;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import pages.PlantsPage;

public class PlantSteps {

    private PlantsPage plantsPage;

    @Before(value = "@UI", order = 3)
    public void initPages() {
        plantsPage = new PlantsPage(UiHooks.page);
    }

    @When("user navigates to Plants page")
    public void user_navigates_to_plants_page() {
        plantsPage.goToPlants();
    }

    @When("user searches plant {string}")
    public void user_searches_plant(String plantName) {
        plantsPage.searchPlant(plantName);
        System.out.println("PLANTS PAGE HTML AFTER SEARCH (" + plantName + "): " + UiHooks.page.content());
    }

    @Then("searched plant {string} should be displayed")
    public void searched_plant_should_be_displayed(String plantName) {
        plantsPage.verifyPlantVisible(plantName);
    }

    @Then("Add Plant button should not be visible")
    public void add_plant_button_should_not_be_visible() {
        plantsPage.verifyAddPlantNotVisible();
    }

    @Then("plant list should be displayed as a table")
    public void plant_list_should_be_displayed_as_table() {
        UiHooks.page.locator("table").waitFor();
    }
}