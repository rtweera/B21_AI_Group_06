package pages;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.testng.Assert;

public class PlantsPage extends BasePage {

    public PlantsPage(Page page) {
        super(page);
    }

    public void open() {
        navigate("/ui/plants");
    }

    public void goToPlants() {
        open();
        page.waitForTimeout(500);
    }

    public int getPlantRowCount() {
        return page.locator("table tbody tr").count();
    }

    public void searchPlant(String plantName) {
        page.fill("input[name='name']", plantName);
        page.locator("button:has-text('Search')").first().click();
        page.waitForTimeout(1000);
    }

    public void searchByName(String name) {
        page.fill("input[name='name']", name);
        page.locator("button:has-text('Search')").first().click();
        page.waitForTimeout(1000);
    }

    public void verifyPlantVisible(String plantName) {
        Locator row = page.locator("table tbody tr")
                .filter(new Locator.FilterOptions().setHasText(plantName))
                .first();
        Assert.assertTrue(row.isVisible(), "Expected plant '" + plantName + "' to be visible");
    }

    public void verifyAddPlantNotVisible() {
        Locator btn = page.locator("a:has-text('Add a Plant'), a[href='/ui/plants/add']");
        int visible = 0;
        for (int i = 0; i < btn.count(); i++) {
            if (btn.nth(i).isVisible()) visible++;
        }
        Assert.assertEquals(visible, 0, "Add a Plant button should not be visible for non-admin");
    }

    public String getAllPricesAsString() {
        Locator priceCol = page.locator("table tbody tr td:nth-child(3)");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < priceCol.count(); i++) {
            sb.append(priceCol.nth(i).textContent().trim()).append(",");
        }
        return sb.toString();
    }

    public void clickPriceColumnHeader() {
        page.locator("th a:has-text('Price')")
                .first()
                .click();
        page.waitForLoadState();
        page.waitForTimeout(2000);
    }

    public Locator findRowContaining(String text) {
        Locator row = page.locator("table tbody tr")
                .filter(new Locator.FilterOptions().setHasText(text))
                .first();
        return row.count() > 0 ? row : null;
    }

    public Locator findRowForPlant(String plantName) {
        searchByName(plantName);
        Locator row = findRowContaining(plantName);
        if (row == null) {
            String noSpace = plantName.replace(" ", "");
            searchByName(noSpace);
            row = findRowContaining(noSpace);
        }
        return row;
    }

    public void deleteRow(Locator row) {
        page.onceDialog(Dialog::accept);
        row.locator("button.btn-outline-danger, button[title='Delete'], button:has(i.bi-trash)")
                .first().click();
        page.waitForTimeout(1500);
    }

    public boolean isAddPlantButtonVisible() {
        Locator btn = page.locator("a:has-text('Add a Plant'), a[href='/ui/plants/add']");
        for (int i = 0; i < btn.count(); i++) {
            if (btn.nth(i).isVisible()) return true;
        }
        return false;
    }

    public void clickAddPlant() {
        page.locator("a:has-text('Add a Plant'), a[href='/ui/plants/add']").first().click();
    }

    public boolean areDeleteButtonsVisible() {
        Locator btns = page.locator(
                "table tbody tr button.btn-outline-danger, " +
                "table tbody tr button[title='Delete'], " +
                "table tbody tr button:has(i.bi-trash)");
        for (int i = 0; i < btns.count(); i++) {
            if (btns.nth(i).isVisible()) return true;
        }
        return false;
    }

    public String getBodyTextLower() {
        return page.locator("body").textContent().toLowerCase();
    }

    public String getBodyText() {
        return page.locator("body").textContent();
    }

    public boolean isOnPath(String pathFragment) {
        return page.url().contains(pathFragment);
    }

    public boolean arePricesSortedAscending() {
        int rows = getPlantRowCount();
        double previous = -1;
        for (int i = 0; i < rows; i++) {
            String priceText = getPriceFromRow(i).replaceAll("[^0-9.]", "");
            if (priceText.isEmpty()) continue;
            double current = Double.parseDouble(priceText);
            if (current < previous) return false;
            previous = current;
        }
        return true;
    }

    public String getPriceFromRow(int rowIndex) {
        return page.locator("table tbody tr")
                .nth(rowIndex)
                .locator("td")
                .nth(2)
                .textContent()
                .trim();
    }
}
