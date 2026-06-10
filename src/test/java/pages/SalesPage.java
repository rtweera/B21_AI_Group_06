package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;

// This class knows WHERE things are on the website
public class SalesPage {

    private Page page;
    private String baseUrl = "http://localhost:8080";

    // When created, we hand it the browser tab
    public SalesPage(Page page) {
        this.page = page;
    }

    // Go to login page
    public void goToLogin() {
        page.navigate(baseUrl + "/ui/login");
    }

    // Type username, password, click login
    public void login(String username, String password) {
        page.fill("[name='username']", username);
        page.fill("[name='password']", password);
        page.locator("button[type='submit'], button:has-text('Login')").first().click();
        page.waitForURL("**/dashboard");
    }

    // Go to plants page
    public void goToPlantsPage() {
        page.navigate(baseUrl + "/ui/plants");
    }

    // Count rows in a table (to check plants/sales exist)
    public int getPlantsRowCount() {
        return page.locator("table tbody tr").count();
    }

    // Go to the sell plant page
    public void goToSellPlantPage() {
        page.navigate(baseUrl + "/ui/sales/new");
    }

    // Pick the first plant in the dropdown (index 1, because index 0 is usually blank)
    public void selectFirstPlant() {
        page.locator("select").first().selectOption(new SelectOption().setIndex(1));
    }

    // Type the quantity
    public void enterQuantity(String quantity) {
        page.fill("input[name='quantity'], #quantity", quantity);
    }

    // Click the Sell button (tries multiple ways to find it)
    public void clickSellButton() {
        page.locator("button:has-text('Sell'), input[type='submit'], button[type='submit']")
                .first().click();
    }

    // Get the current URL (to check where we ended up)
    public String getCurrentUrl() {
        return page.url();
    }

    // Check if there's at least one sale in the table
    public boolean isSaleRecordVisible() {
        return page.locator("table tbody tr").count() > 0;
    }

    // Check if an error message about quantity is shown anywhere on the page
    public boolean isQuantityErrorVisible() {
        String body = page.locator("body").textContent().toLowerCase();
        return body.contains("quantity") || body.contains("greater than");
    }

    // Check if we are still on the Sell Plant page (URL contains /sales/new)
    public boolean isOnSellPlantPage() {
        return page.url().contains("/sales/new");
    }

    // Go to the sales list page
    public void goToSalesListPage() {
        page.navigate(baseUrl + "/ui/sales");
    }

    // Count how many sales rows are in the table
    public int getSalesRowCount() {
        return page.locator("table tbody tr").count();
    }

    // Delete the first sale and automatically accept the confirmation popup
    public void deleteFirstSaleAndConfirm() {
        // This tells the browser: "when a confirm popup appears, click OK automatically"
        page.onceDialog(dialog -> dialog.accept());

        // Now click the Delete button on the first row
        page.locator("table tbody tr").first()
                .locator("button:has-text('Delete'), a:has-text('Delete')")
                .first().click();

        page.waitForTimeout(1500);  // wait for the list to refresh
    }

    // Check if we are on the sales list page
    public boolean isOnSalesListPage() {
        return page.url().contains("/ui/sales");
    }

    // Get the "Sold At" date text from a specific row (rows start at 0)
    public String getSaleDateFromRow(int rowIndex) {
        // The "Sold At" column is the 4th column (index 3): Plant, Quantity, Total Price, Sold At
        return page.locator("table tbody tr").nth(rowIndex)
                .locator("td").nth(3).textContent().trim();
    }

    // Click the Cancel button on the Sell Plant page
    public void clickCancelButton() {
        page.locator("a:has-text('Cancel'), button:has-text('Cancel')").first().click();
        page.waitForTimeout(1000);
    }

    // Clear all cookies (so the browser is "logged out")
    public void clearCookies() {
        page.context().clearCookies();
    }

    // Go to any page path (e.g. "/ui/categories")
    public void goToPath(String path) {
        page.navigate(baseUrl + path);
        page.waitForTimeout(1000);
    }

    // Check if we are on the login page
    public boolean isOnLoginPage() {
        return page.url().contains("/login");
    }
}