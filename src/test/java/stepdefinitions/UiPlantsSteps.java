package stepdefinitions;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.AddPlantPage;
import pages.LoginPage;
import pages.PlantsPage;
import pages.SalesPage;
import utils.PlaywrightFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.*;

/**
 * Step definitions for Plants UI tests (tester 215565L).
 *
 * UI interaction logic is fully delegated to Page Objects:
 *   - {@link PlantsPage}   – plants list page
 *   - {@link AddPlantPage} – add-plant form page
 *   - {@link LoginPage}    – login page
 *   - {@link SalesPage}    – sales page (reused for sales-state setup steps)
 *
 * The only non-page-object code kept here is the {@code ensurePlantExists}
 * helper, which uses the REST API (not the browser) to seed test data.
 */
public class UiPlantsSteps {

    private Page page;
    private PlantsPage plantsPage;
    private AddPlantPage addPlantPage;
    private LoginPage loginPage;
    private SalesPage salesPage;

    @Before("@UI")
    public void initPage() {
        page = PlaywrightFactory.getPage();
        plantsPage   = new PlantsPage(page);
        addPlantPage = new AddPlantPage(page);
        loginPage    = new LoginPage(page);
        salesPage    = new SalesPage(page);
    }

    // -----------------------------------------------------------------------
    // API-based test-data setup helper (not UI interaction – kept here intentionally)
    // -----------------------------------------------------------------------

    /**
     * Ensure a plant with the given name exists in the database.
     * Uses the REST API directly so it does not pollute the browser session.
     * Also creates one sale record for "TestPlant" if needed, so that
     * delete-prevention tests have a referencing sale.
     */
    private void ensurePlantExists(String name) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            // Authenticate as admin
            String authPayload = "{\"username\":\"admin\",\"password\":\"admin123\"}";
            HttpRequest authReq = HttpRequest.newBuilder()
                    .uri(URI.create(plantsPage.getBaseUrl() + "/api/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(authPayload))
                    .build();
            HttpResponse<String> authRes = client.send(authReq, HttpResponse.BodyHandlers.ofString());

            String token = extractFirst(authRes.body(), "\"token\"\\s*:\\s*\"([^\"]+)\"");

            // Fetch all plants
            HttpRequest getReq = HttpRequest.newBuilder()
                    .uri(URI.create(plantsPage.getBaseUrl() + "/api/plants"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();
            HttpResponse<String> getRes = client.send(getReq, HttpResponse.BodyHandlers.ofString());

            String cleanName = name.replace(" ", "");
            String body = getRes.body();
            String plantId = null;

            boolean exists = body.contains("\"name\":\"" + cleanName + "\"")
                    || body.contains("\"name\":\"" + name + "\"");

            if (!exists) {
                System.out.println("[UI Setup] Plant '" + name + "' not found. Creating via API...");
                String plantPayload = "{\"name\":\"" + cleanName + "\",\"price\":100.0,\"quantity\":90}";
                HttpRequest createReq = HttpRequest.newBuilder()
                        .uri(URI.create(plantsPage.getBaseUrl() + "/api/plants/category/2"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .POST(HttpRequest.BodyPublishers.ofString(plantPayload))
                        .build();
                HttpResponse<String> createRes = client.send(createReq, HttpResponse.BodyHandlers.ofString());
                plantId = extractFirst(createRes.body(), "\"id\"\\s*:\\s*(\\d+)");
            } else {
                // Extract the id that appears just before the matching name field
                int nameIndex = body.indexOf("\"name\":\"" + cleanName + "\"");
                if (nameIndex == -1) nameIndex = body.indexOf("\"name\":\"" + name + "\"");
                if (nameIndex != -1) {
                    String prefix = body.substring(0, nameIndex);
                    Pattern idPat = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");
                    Matcher idMat = idPat.matcher(prefix);
                    while (idMat.find()) plantId = idMat.group(1);
                }
            }

            // For "TestPlant" ensure at least one sale exists (needed for delete-prevention test)
            if (plantId != null && cleanName.equalsIgnoreCase("TestPlant")) {
                System.out.println("[UI Setup] Ensuring sale record exists for plant ID: " + plantId);
                HttpRequest saleReq = HttpRequest.newBuilder()
                        .uri(URI.create(plantsPage.getBaseUrl() + "/api/sales/plant/" + plantId + "?quantity=1"))
                        .header("Authorization", "Bearer " + token)
                        .POST(HttpRequest.BodyPublishers.ofString(""))
                        .build();
                client.send(saleReq, HttpResponse.BodyHandlers.ofString());
            }
        } catch (Exception e) {
            System.out.println("[UI Setup] Error ensuring plant exists: " + e.getMessage());
        }
    }

    /** Extract the first capturing group from {@code text} using {@code regex}. */
    private String extractFirst(String text, String regex) {
        Matcher m = Pattern.compile(regex).matcher(text);
        return m.find() ? m.group(1) : "";
    }

    // -----------------------------------------------------------------------
    // Navigation steps
    // -----------------------------------------------------------------------

    @And("I navigate to the Plants page")
    public void iNavigateToPlantsPage() {
        System.out.println("[STEP] Navigating to Plants page...");
        plantsPage.open();
        System.out.println("[PASS] Navigated to Plants page");
    }

    // -----------------------------------------------------------------------
    // Add-plant form steps
    // -----------------------------------------------------------------------

    @When("I click the {string} button")
    public void iClickTheButton(String buttonText) {
        System.out.println("[STEP] Clicking button: " + buttonText);
        if (buttonText.equals("Add a Plant")) {
            plantsPage.clickAddPlant();
        } else {
            page.locator("button:has-text('" + buttonText + "'), a:has-text('" + buttonText + "')")
                    .first().click();
        }
        System.out.println("[PASS] Clicked button: " + buttonText);
    }

    @And("I enter plant name {string}, category {string}, price {string} and quantity {string}")
    public void iEnterPlantDetails(String name, String category, String price, String quantity) {
        System.out.println("[STEP] Entering plant details: " + name + ", " + category + ", " + price + ", " + quantity);
        String actualName = addPlantPage.fillForm(name, category, price, quantity);
        System.out.println("[PASS] Plant details entered (name used: " + actualName + ")");
    }

    @And("I click the Save button")
    public void iClickSaveButton() {
        System.out.println("[STEP] Clicking Save button...");
        addPlantPage.clickSave();
        System.out.println("[PASS] Clicked Save button");
    }

    // -----------------------------------------------------------------------
    // Assertion steps – add-plant outcomes
    // -----------------------------------------------------------------------

    @Then("I should be redirected to the plants page {string}")
    public void iShouldBeRedirectedToPlantsPage(String expectedPath) {
        System.out.println("[STEP] Verifying redirection to: " + expectedPath);
        assertTrue(plantsPage.getCurrentUrl().contains(expectedPath),
                "Should be redirected to " + expectedPath + ". Actual: " + plantsPage.getCurrentUrl());
        System.out.println("[PASS] Redirected successfully");
    }

    @And("I should see a success message {string}")
    public void iShouldSeeSuccessMessage(String expectedMessage) {
        System.out.println("[STEP] Checking success message: " + expectedMessage);
        assertTrue(addPlantPage.getBodyText().contains(expectedMessage),
                "Page should contain message: " + expectedMessage);
        System.out.println("[PASS] Success message displayed");
    }

    @Then("the category dropdown should contain only subcategories")
    public void categoryDropdownContainsOnlySubcategories() {
        System.out.println("[STEP] Checking category dropdown options...");
        Locator options = addPlantPage.getCategoryOptions();
        int count = options.count();
        System.out.println("[INFO] Dropdown options count: " + count);

        assertTrue(count <= 3, "Dropdown should only contain subcategories. Option count: " + count);

        for (int i = 0; i < count; i++) {
            String label = options.nth(i).textContent().trim();
            System.out.println("[INFO] Option " + i + ": " + label);
            assertFalse(
                    label.equals("Roses") || label.equals("Succulents")
                            || label.equals("Tulips") || label.equals("Aloe"),
                    "Dropdown should not contain main category: " + label);
        }
        System.out.println("[PASS] Dropdown contains only subcategories");
    }

    @Then("I should see a validation error {string}")
    public void iShouldSeeValidationError(String expectedError) {
        System.out.println("[STEP] Checking validation error: " + expectedError);
        assertTrue(addPlantPage.getBodyText().contains(expectedError),
                "Should see error message: " + expectedError);
        System.out.println("[PASS] Validation error displayed");
    }

    @And("I should remain on the add plant page")
    public void iShouldRemainOnAddPlantPage() {
        System.out.println("[STEP] Verifying user remains on add plant page...");
        assertTrue(addPlantPage.isOpen(),
                "Should be on /ui/plants/add. Actual: " + addPlantPage.getCurrentUrl());
        System.out.println("[PASS] Remained on add plant page");
    }

    // -----------------------------------------------------------------------
    // Delete steps
    // -----------------------------------------------------------------------

    @When("I click on delete icon on {string}")
    public void iClickDeleteIconOn(String plantName) {
        System.out.println("[STEP] Ensuring plant exists before delete: " + plantName);
        ensurePlantExists(plantName);

        System.out.println("[STEP] Searching for plant and clicking delete: " + plantName);
        plantsPage.open();
        Locator row = plantsPage.findRowForPlant(plantName);
        assertNotNull(row, "Plant row not found for: " + plantName);

        plantsPage.deleteRow(row);
        System.out.println("[PASS] Clicked delete icon");
    }

    @Then("the system should prevent deletion and display an appropriate message indicating that the plant cannot be deleted")
    public void systemShouldPreventDeletion() {
        System.out.println("[STEP] Verifying system prevents deletion...");
        String bodyText = plantsPage.getBodyTextLower();
        boolean hasError = bodyText.contains("cannot delete")
                || bodyText.contains("sales record")
                || bodyText.contains("constraint")
                || bodyText.contains("foreign key")
                || bodyText.contains("error")
                || bodyText.contains("prevent")
                || bodyText.contains("associated");
        assertTrue(hasError,
                "Should show an error message explaining that the plant cannot be deleted.");
        System.out.println("[PASS] Deletion prevented with message");
    }

    // -----------------------------------------------------------------------
    // Authorization / role-based access steps
    // -----------------------------------------------------------------------

    @Given("I am logged in as a non-admin user")
    public void iAmLoggedInAsNonAdminUser() {
        System.out.println("[STEP] Logging in as non-admin user (testuser)...");
        loginPage.clearCookies();
        loginPage.open();
        loginPage.login("testuser", "test123");
        assertTrue(page.url().contains("/dashboard"), "Should navigate to dashboard");
        System.out.println("[PASS] Logged in as testuser");
    }

    @Then("I should not see the {string} button")
    public void iShouldNotSeeTheButton(String buttonText) {
        System.out.println("[STEP] Checking button is not visible: " + buttonText);
        boolean isVisible = buttonText.equals("Add a Plant")
                ? plantsPage.isAddPlantButtonVisible()
                : isAnyVisible(page.locator(
                        "button:has-text('" + buttonText + "'), a:has-text('" + buttonText + "')"));
        assertFalse(isVisible, "Button '" + buttonText + "' should not be visible on page");
        System.out.println("[PASS] Button not visible");
    }

    @Then("I should see an Access Denied message")
    public void iShouldSeeAccessDeniedMessage() {
        System.out.println("[STEP] Verifying Access Denied page...");
        String bodyText = plantsPage.getBodyTextLower();
        boolean hasAccessDenied = bodyText.contains("access denied")
                || bodyText.contains("forbidden")
                || bodyText.contains("403")
                || bodyText.contains("unauthorized");
        assertTrue(hasAccessDenied,
                "Page should show Access Denied or Forbidden. Page text: " + bodyText);
        System.out.println("[PASS] Access Denied message verified");
    }

    // -----------------------------------------------------------------------
    // Sales-state setup steps (used in non-admin sales view tests)
    // -----------------------------------------------------------------------

    @And("no sales records exist in the system")
    public void noSalesRecordsExistInTheSystem() {
        System.out.println("[STEP] Clearing all sales records from system as admin...");

        // Switch to admin
        loginPage.clearCookies();
        loginPage.open();
        loginPage.login("admin", "admin123");

        // Delete all sales rows
        salesPage.openSalesListPage();
        page.waitForTimeout(1000);

        while (salesPage.getSalesRowCount() > 0) {
            Locator firstRow = page.locator("table tbody tr").first();
            String rowText = firstRow.textContent();
            if (rowText.contains("No sales found") || rowText.contains("No record")) break;

            page.onceDialog(Dialog::accept);
            Locator deleteBtn = firstRow.locator(
                    "button.btn-outline-danger, button:has(i.bi-trash), button:has-text('Delete')");
            if (deleteBtn.count() > 0) {
                deleteBtn.first().click();
                page.waitForTimeout(1500);
            } else {
                break;
            }
        }

        // Switch back to testuser
        loginPage.clearCookies();
        loginPage.open();
        loginPage.login("testuser", "test123");
        System.out.println("[PASS] All sales records deleted and logged back in as testuser");
    }

    @Then("the sales list page should display a table with at least one record")
    public void salesListPageDisplaysAtLeastOneRecord() {
        System.out.println("[STEP] Checking sales list has at least one record...");
        int rowCount = salesPage.getSalesRowCount();

        boolean isEmpty = rowCount == 0 || (rowCount == 1 && isEmptyRow());

        if (isEmpty) {
            System.out.println("[INFO] Sales table is empty. Creating a sale as admin...");
            loginPage.clearCookies();
            loginPage.open();
            loginPage.login("admin", "admin123");

            salesPage.openNewSalePage();
            salesPage.selectFirstPlant();
            salesPage.enterQuantity("1");
            salesPage.clickSellButton();
            page.waitForTimeout(1500);

            loginPage.clearCookies();
            loginPage.open();
            loginPage.login("testuser", "test123");

            salesPage.openSalesListPage();
            page.waitForTimeout(1000);
            rowCount = salesPage.getSalesRowCount();
        }

        assertTrue(rowCount >= 1, "Sales table should have at least one record.");
        System.out.println("[PASS] Sales list has at least one record");
    }

    @Then("I should not see delete buttons on sales table rows")
    public void iShouldNotSeeDeleteButtonsOnSalesTableRows() {
        System.out.println("[STEP] Checking delete buttons are not visible in sales rows...");
        Locator deleteButtons = page.locator(
                "table tbody tr button.btn-outline-danger, " +
                "table tbody tr i.bi-trash, " +
                "table tbody tr button:has-text('Delete')");
        assertFalse(isAnyVisible(deleteButtons),
                "Delete buttons should not be visible on sales table rows");
        System.out.println("[PASS] No delete buttons visible");
    }

    @Then("I should see the empty sales message {string}")
    public void iShouldSeeEmptySalesMessage(String expectedMessage) {
        System.out.println("[STEP] Checking empty sales list message: " + expectedMessage);
        assertTrue(plantsPage.getBodyText().contains(expectedMessage),
                "Should display empty sales message: " + expectedMessage);
        System.out.println("[PASS] Empty sales message displayed");
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    /** Return true if any locator in the set is currently visible. */
    private boolean isAnyVisible(Locator locator) {
        for (int i = 0; i < locator.count(); i++) {
            if (locator.nth(i).isVisible()) return true;
        }
        return false;
    }

    /** Return true when the single table row contains a "no records" placeholder text. */
    private boolean isEmptyRow() {
        String text = page.locator("table tbody tr").first().textContent();
        return text.contains("No sales found") || text.contains("No record");
    }
}
