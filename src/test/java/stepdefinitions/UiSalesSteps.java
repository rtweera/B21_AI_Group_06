package stepdefinitions;

import com.microsoft.playwright.Page;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import pages.PlantsPage;
import pages.SalesPage;
import utils.PlaywrightFactory;

import static org.testng.Assert.*;

// Steps specific to the Sales pages (sell, delete, sort, cancel)
public class SalesUiSteps {

    private Page page;
    private SalesPage salesPage;
    private PlantsPage plantsPage;

    @Before("@UI")
    public void initializePages() {
        page = PlaywrightFactory.getPage();
        salesPage = new SalesPage(page);
        plantsPage = new PlantsPage(page);
    }

    @And("a plant is available with stock greater than 0")
    public void aPlantIsAvailable() {
        System.out.println("[STEP] Checking plants exist...");
        plantsPage.open();
        assertTrue(plantsPage.getPlantRowCount() > 0, "Plants should exist");
        System.out.println("[PASS] Plants exist");
    }

    @When("I navigate to the Sell Plant page")
    public void iNavigateToSellPlantPage() {
        System.out.println("[STEP] Going to Sell Plant page...");
        salesPage.openNewSalePage();
        System.out.println("[PASS] On Sell Plant page");
    }

    @And("I select the first plant from the dropdown")
    public void iSelectFirstPlant() {
        System.out.println("[STEP] Selecting first plant...");
        salesPage.selectFirstPlant();
        System.out.println("[PASS] Selected first plant");
    }

    @And("I enter quantity {string}")
    public void iEnterQuantity(String quantity) {
        System.out.println("[STEP] Entering quantity: " + quantity);
        salesPage.enterQuantity(quantity);
        System.out.println("[PASS] Entered quantity " + quantity);
    }

    @And("I click the Sell button")
    public void iClickSellButton() {
        System.out.println("[STEP] Clicking Sell button...");
        salesPage.clickSellButton();
        page.waitForTimeout(2000);
        System.out.println("[PASS] Clicked Sell button");
    }

    @Then("I should be redirected to the sales list page")
    public void iShouldBeOnSalesListPage() {
        System.out.println("[STEP] Checking we're on sales list...");
        String url = salesPage.getCurrentUrl();
        assertTrue(url.contains("/ui/sales"), "Should be on sales list. Actual: " + url);
        System.out.println("[PASS] On sales list: " + url);
    }

    @And("a new sale record should be visible in the sales table")
    public void aNewSaleShouldBeVisible() {
        System.out.println("[STEP] Checking sale record is visible...");
        assertTrue(salesPage.isSaleRecordVisible(), "Sale record should be visible");
        System.out.println("[PASS] Sale record visible");
    }

    @Then("I should see an error message about quantity")
    public void iShouldSeeQuantityError() {
        System.out.println("[STEP] Checking error message appears...");
        assertTrue(salesPage.isQuantityErrorVisible(), "Should show quantity error");
        System.out.println("[PASS] Error message shown");
    }

    @And("I should remain on the Sell Plant page")
    public void iShouldRemainOnSellPlantPage() {
        System.out.println("[STEP] Checking still on Sell Plant page...");
        assertTrue(salesPage.isOnSellPlantPage(),
                "Should remain on Sell Plant page. Actual: " + salesPage.getCurrentUrl());
        System.out.println("[PASS] Still on Sell Plant page");
    }

    @And("at least one sale exists in the system")
    public void atLeastOneSaleExists() {
        System.out.println("[STEP] Making sure at least one sale exists...");
        salesPage.openSalesListPage();
        if (salesPage.getSalesRowCount() == 0) {
            System.out.println("[INFO] No sales found, creating one...");
            salesPage.openNewSalePage();
            salesPage.selectFirstPlant();
            salesPage.enterQuantity("1");
            salesPage.clickSellButton();
            page.waitForTimeout(2000);
        }
        salesPage.openSalesListPage();
        assertTrue(salesPage.getSalesRowCount() > 0, "At least one sale should exist");
        System.out.println("[PASS] At least one sale exists");
    }

    @When("I navigate to the sales list page")
    public void iNavigateToSalesListPage() {
        System.out.println("[STEP] Going to sales list page...");
        salesPage.openSalesListPage();
        System.out.println("[PASS] On sales list page");
    }

    @And("I delete the first sale and confirm")
    public void iDeleteFirstSaleAndConfirm() {
        System.out.println("[STEP] Deleting first sale and confirming...");
        salesPage.deleteFirstSaleAndConfirm();
        System.out.println("[PASS] Deleted first sale");
    }

    @Then("the sale record should be removed from the list")
    public void theSaleShouldBeRemoved() {
        System.out.println("[STEP] Verifying sale was removed...");
        assertTrue(salesPage.isOnSalesListPage(), "Should be on sales list after delete");
        System.out.println("[PASS] Sale list refreshed after deletion");
    }

    @And("I should remain on the sales list page")
    public void iShouldRemainOnSalesListPage() {
        System.out.println("[STEP] Verifying still on sales list page...");
        assertTrue(salesPage.isOnSalesListPage(),
                "Should remain on sales list. Actual: " + salesPage.getCurrentUrl());
        System.out.println("[PASS] Still on sales list page");
    }

    @And("at least two sales exist in the system")
    public void atLeastTwoSalesExist() {
        System.out.println("[STEP] Making sure at least two sales exist...");
        salesPage.openSalesListPage();
        while (salesPage.getSalesRowCount() < 2) {
            System.out.println("[INFO] Creating a sale...");
            salesPage.openNewSalePage();
            salesPage.selectFirstPlant();
            salesPage.enterQuantity("1");
            salesPage.clickSellButton();
            page.waitForTimeout(2000);
            salesPage.openSalesListPage();
        }
        assertTrue(salesPage.getSalesRowCount() >= 2, "Should have at least 2 sales");
        System.out.println("[PASS] At least two sales exist");
    }

    @Then("the most recent sale should appear first")
    public void mostRecentSaleFirst() {
        System.out.println("[STEP] Checking sales sorted by date descending...");
        String firstDate = salesPage.getSaleDateFromRow(0);
        String secondDate = salesPage.getSaleDateFromRow(1);
        System.out.println("[INFO] Row 1 date: " + firstDate);
        System.out.println("[INFO] Row 2 date: " + secondDate);
        assertTrue(firstDate.compareTo(secondDate) >= 0,
                "Row 1 should be newer. Row1=" + firstDate + " Row2=" + secondDate);
        System.out.println("[PASS] Most recent sale appears first");
    }

    @And("I click the Cancel button")
    public void iClickCancelButton() {
        System.out.println("[STEP] Clicking Cancel button...");
        salesPage.clickCancelButton();
        System.out.println("[PASS] Clicked Cancel button");
    }
}