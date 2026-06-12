package pages;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;

// This class knows WHERE things are on the website
public class SalesPage extends BasePage {

    // When created, we hand it the browser tab
    public SalesPage(Page page) {
        super(page);
    }

    // Go to the sales list page
    public void goToSalesListPage() {
        navigate("/ui/sales");
    }

    // Go to the sell plant page
    public void openNewSalePage() {
        navigate("/ui/sales/new");
    }

    // Pick the first plant in the dropdown (index 1, because index 0 is usually blank)
    public void selectFirstPlant() {
        page.locator("select")
                .first()
                .selectOption(new SelectOption().setIndex(1));
    }

    // Type the quantity
    public void enterQuantity(String quantity) {
        page.fill(
                "input[name='quantity'], #quantity",
                quantity);
    }

    // Click the Sell button (tries multiple ways to find it)
    public void clickSellButton() {
        page.locator("button:has-text('Sell'), input[type='submit'], button[type='submit']")
                .first()
                .click();
    }

    // Check if there's at least one sale in the table
    public boolean isSaleRecordVisible() {
        return page.locator("table tbody tr")
                .count() > 0;
    }

    // Check if an error message about quantity is shown anywhere on the page
    public boolean isQuantityErrorVisible() {
        String body = page.locator("body")
                .textContent()
                .toLowerCase();
        return body.contains("quantity")
                || body.contains("greater than");
    }

    // Check if we are still on the Sell Plant page (URL contains /sales/new)
    public boolean isOnSellPlantPage() {
        return page.url().contains("/sales/new");
    }

    public void openSalesListPage() {
        navigate("/ui/sales");
    }

    // Count how many sales rows are in the table
    public int getSalesRowCount() {
        return page.locator("table tbody tr")
                .count();
    }

    // Delete the first sale and automatically accept the confirmation popup
    public void deleteFirstSaleAndConfirm() {
        // This tells the browser: "when a confirm popup appears, click OK automatically"
        page.onceDialog(Dialog::accept);

        // Now click the Delete button on the first row
        page.locator("table tbody tr")
                .first()
                .locator("button:has-text('Delete'), a:has-text('Delete')")
                .first()
                .click();

        page.waitForTimeout(1500);
    }

    // Check if we are on the sales list page
    public boolean isOnSalesListPage() {
        return page.url().contains("/ui/sales");
    }

    // Get the "Sold At" date text from a specific row (rows start at 0)
    public String getSaleDateFromRow(int rowIndex) {
        // The "Sold At" column is the 4th column (index 3): Plant, Quantity, Total Price, Sold At
        return page.locator("table tbody tr")
                .nth(rowIndex)
                .locator("td")
                .nth(3)
                .textContent()
                .trim();
    }

    // Click the Cancel button on the Sell Plant page
    public void clickCancelButton() {
        page.locator("a:has-text('Cancel'), button:has-text('Cancel')")
                .first()
                .click();
        page.waitForTimeout(1000);
    }
}