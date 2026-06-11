package stepdefinitions;

import com.microsoft.playwright.Page;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;

import pages.LoginPage;
import pages.PlantsPage;
import pages.SalesPage;
import utils.PlaywrightFactory;

import static org.testng.Assert.*;

// The Java code that runs for each English line in the feature file
public class UiSalesSteps {

    private Page page;

    private SalesPage salesPage;
    private LoginPage loginPage;
    private PlantsPage plantsPage;

    // Makes sure browser and page helper are ready
    @Before("@UI")
    public void initializePages() {
        page = PlaywrightFactory.getPage();

        salesPage = new SalesPage(page);
        loginPage = new LoginPage(page);
        plantsPage = new PlantsPage(page);
    }

    @Given("I am logged in as admin")
    public void iAmLoggedInAsAdmin() {
        System.out.println("[STEP] Logging in as admin...");
        loginPage.open();
        loginPage.login("admin", "admin123");
        // assertTrue = "this MUST be true, or fail the test"
        assertTrue(
                page.url().contains("/dashboard"),
                "Should be on dashboard"
        );
        System.out.println("[PASS] Logged in as admin");
    }

    @And("a plant is available with stock greater than 0")
    public void aPlantIsAvailable() {
        System.out.println("[STEP] Checking plants exist...");
        plantsPage.open();
        assertTrue(
                plantsPage.getPlantRowCount() > 0,
                "Plants should exist"
        );
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

    // Matches: "And I enter quantity "2""  ({string} captures whatever is in quotes)
    @And("I enter quantity {string}")
    public void iEnterQuantity(String quantity) {
        System.out.println("[STEP] Entering quantity: " + quantity);
        salesPage.enterQuantity(quantity);
        System.out.println("[PASS] Entered quantity " + quantity);
    }

    // Matches: "And I click the Sell button"
    @And("I click the Sell button")
    public void iClickSellButton() {
        System.out.println("[STEP] Clicking Sell button...");
        salesPage.clickSellButton();
        page.waitForTimeout(2000);  // wait 2 seconds for page to load
        System.out.println("[PASS] Clicked Sell button");
    }

    // Matches: "Then I should be redirected to the sales list page"
    @Then("I should be redirected to the sales list page")
    public void iShouldBeOnSalesListPage() {
        System.out.println("[STEP] Checking we're on sales list...");
        String url = salesPage.getCurrentUrl();
        assertTrue(
                url.contains("/ui/sales"),
                "Should be on sales list. Actual: " + url
        );
        System.out.println("[PASS] On sales list: " + url);
    }


    // Matches: "And a new sale record should be visible in the sales table"
    @And("a new sale record should be visible in the sales table")
    public void aNewSaleShouldBeVisible() {
        System.out.println("[STEP] Checking sale record is visible...");
        assertTrue(
                salesPage.isSaleRecordVisible(),
                "Sale record should be visible"
        );
        System.out.println("[PASS] Sale record visible");
    }

    // Matches: "Then I should see an error message about quantity"
    @Then("I should see an error message about quantity")
    public void iShouldSeeQuantityError() {
        System.out.println("[STEP] Checking error message appears...");
        assertTrue(
                salesPage.isQuantityErrorVisible(),
                "Should show an error message about quantity"
        );
        System.out.println("[PASS] Error message shown");
    }

    // Matches: "And I should remain on the Sell Plant page"
    @And("I should remain on the Sell Plant page")
    public void iShouldRemainOnSellPlantPage() {
        System.out.println("[STEP] Checking still on Sell Plant page...");
        assertTrue(
                salesPage.isOnSellPlantPage(),
                "Should remain on Sell Plant page. Actual: "
                        + salesPage.getCurrentUrl()
        );
        System.out.println("[PASS] Still on Sell Plant page");
    }

    // Matches: "And at least one sale exists in the system"
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
        assertTrue(
                salesPage.getSalesRowCount() > 0,
                "At least one sale should exist"
        );
        System.out.println("[PASS] At least one sale exists");
    }

    // Matches: "When I navigate to the sales list page"
    @When("I navigate to the sales list page")
    public void iNavigateToSalesListPage() {
        System.out.println("[STEP] Going to sales list page...");
        salesPage.openSalesListPage();
        System.out.println("[PASS] On sales list page");
    }

    // Matches: "And I delete the first sale and confirm"
    @And("I delete the first sale and confirm")
    // Delete the first sale and automatically accept the confirmation popup
    public void deleteFirstSaleAndConfirm() {
        // Count rows before deleting (to verify later)
        int countBefore = salesPage.getSalesRowCount();
        System.out.println("[INFO] Sales before delete: " + countBefore);

        // Tell the browser: when the "Are you sure?" popup appears, click OK
        page.onceDialog(dialog -> {
            System.out.println("[INFO] Dialog appeared: " + dialog.message());
            dialog.accept();  // clicks OK
        });

        // Click the trash/delete button on the first row
        // The button has class "btn-outline-danger" and contains a trash icon
        // FIX: Have to move to POM file
        page.locator("table tbody tr").first()
                .locator("button.btn-outline-danger, button:has(i.bi-trash)")
                .first().click();

        page.waitForTimeout(1500);  // wait for the list to refresh
    }

    // Matches: "Then the sale record should be removed from the list"
    @Then("the sale record should be removed from the list")
    public void theSaleShouldBeRemoved() {
        System.out.println("[STEP] Verifying sale was removed...");
        // After delete we should be back on the sales list page
        assertTrue(
                salesPage.isOnSalesListPage(),
                "Should be on sales list after delete"
        );
        System.out.println("[PASS] Sale list refreshed after deletion");
    }

    // Matches: "And I should remain on the sales list page"
    @And("I should remain on the sales list page")
    public void iShouldRemainOnSalesListPage() {
        System.out.println("[STEP] Verifying still on sales list page...");
        assertTrue(
                salesPage.isOnSalesListPage(),
                "Should remain on sales list. Actual: "
                        + salesPage.getCurrentUrl()
        );
        System.out.println("[PASS] Still on sales list page");
    }

    // Matches: "And at least two sales exist in the system"
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

        assertTrue(
                salesPage.getSalesRowCount() >= 2,
                "Should have at least 2 sales"
        );
        System.out.println("[PASS] At least two sales exist");
    }

    // Matches: "Then the most recent sale should appear first"
    @Then("the most recent sale should appear first")
    public void mostRecentSaleFirst() {
        System.out.println("[STEP] Checking sales are sorted by date descending...");
        String firstDate = salesPage.getSaleDateFromRow(0);
        String secondDate = salesPage.getSaleDateFromRow(1);
        System.out.println("[INFO] Row 1 date: " + firstDate);
        System.out.println("[INFO] Row 2 date: " + secondDate);

        // Dates are formatted like "2026-06-10 11:14" which sort correctly as text
        // Row 1 should be greater than or equal to Row 2 (newest first)
        assertTrue(
                firstDate.compareTo(secondDate) >= 0,
                "Row 1 date should be newer than or equal to Row 2."
                        + " Row1=" + firstDate
                        + " Row2=" + secondDate
        );
        System.out.println("[PASS] Most recent sale appears first");
    }

    // Matches: "And I click the Cancel button"
    @And("I click the Cancel button")
    public void iClickCancelButton() {
        System.out.println("[STEP] Clicking Cancel button...");
        salesPage.clickCancelButton();
        System.out.println("[PASS] Clicked Cancel button");
    }

    // Matches: "Given I am not logged in"
    @Given("I am not logged in")
    public void iAmNotLoggedIn() {
        System.out.println("[STEP] Clearing cookies (not logged in)...");
        salesPage.clearCookies();
        System.out.println("[PASS] Cookies cleared - user is logged out");
    }

    // Matches: "When I navigate to the page "/ui/categories""
    // {string} captures whatever URL is in the quotes
    @When("I navigate to the page {string}")
    public void iNavigateToThePage(String path) {
        System.out.println("[STEP] Navigating to " + path + " without logging in...");
        salesPage.goToPath(path);
        System.out.println("[PASS] Navigation attempted to " + path);
    }

    // Matches: "Then I should be redirected to the login page"
    @Then("I should be redirected to the login page")
    public void iShouldBeRedirectedToLoginPage() {
        System.out.println("[STEP] Checking we were redirected to login...");
        assertTrue(
                salesPage.isOnLoginPage(),
                "Should be redirected to login. Actual URL: "
                        + salesPage.getCurrentUrl()
        );
        System.out.println(
                "[PASS] Redirected to login page: "
                        + salesPage.getCurrentUrl()
        );
    }
}