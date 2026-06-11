package pages;

import com.microsoft.playwright.Page;

public class PlantsPage extends BasePage {
    public PlantsPage(Page page) {
        super(page);
    }

    public void open() {
        navigate("/ui/plants");
    }

    public int getPlantRowCount() {
        return page.locator("table tbody tr").count();
    }

    // Click the Price column header to sort
    // Click the Price column header link to sort
    public void clickPriceColumnHeader() {
        // Click the <a> link inside the Price <th> header
        page.locator("th a:has-text('Price')")
                .first()
                .click();
        // Wait for the page to reload with sorted data
        page.waitForLoadState();
        page.waitForTimeout(1500);
    }

    public String getPriceFromRow(int rowIndex) {
        // Adjust the nth() index if Price is in a different column
        return page.locator("table tbody tr")
                .nth(rowIndex)
                .locator("td")
                .nth(2)   // 3rd column (index 2) - adjust if needed
                .textContent()
                .trim();
    }

    public String getAllPricesAsString() {
        int rows = getPlantRowCount();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            sb.append(getPriceFromRow(i)).append(",");
        }
        return sb.toString();
    }
}
