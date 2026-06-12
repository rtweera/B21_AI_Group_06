package stepdefinitions;

import com.microsoft.playwright.Page;
import io.cucumber.java.Before;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import pages.PlantsPage;
import utils.PlaywrightFactory;

import static org.testng.Assert.*;

// Steps specific to the Plants page
public class PlantsUiSteps {

    private Page page;
    private PlantsPage plantsPage;

    @Before("@UI")
    public void initializePages() {
        page = PlaywrightFactory.getPage();
        plantsPage = new PlantsPage(page);
    }

    @When("I navigate to the plants page")
    public void iNavigateToThePlantsPage() {
        System.out.println("[STEP] Going to plants page...");
        plantsPage.open();
        System.out.println("[PASS] On plants page");
    }

    @And("I click the Price column header")
    public void iClickPriceColumnHeader() {
        System.out.println("[STEP] Clicking Price column header...");
        // Save the order before clicking, so we can compare
        String orderBefore = plantsPage.getAllPricesAsString();
        System.out.println("[INFO] Order before sort: " + orderBefore);
        // Store it for the verification step
        this.priceOrderBeforeSort = orderBefore;

        plantsPage.clickPriceColumnHeader();
        System.out.println("[PASS] Clicked Price header");
    }

    // Remember the order before sorting (so we can check it changed)
    private String priceOrderBeforeSort;

    @Then("the plant list order should change")
    public void thePlantListOrderShouldChange() {
        System.out.println("[STEP] Checking the order changed after sorting...");
        String orderAfter = plantsPage.getAllPricesAsString();
        System.out.println("[INFO] Order after sort: " + orderAfter);

        assertTrue(plantsPage.getPlantRowCount() > 0, "Plants should be visible");

        // The order should now be different from before (it got sorted)
        assertNotEquals(orderAfter, priceOrderBeforeSort,
                "Plant order should change after clicking Price header");
        System.out.println("[PASS] Plant list order changed after sorting");
    }
}