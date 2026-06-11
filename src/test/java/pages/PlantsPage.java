package pages;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Page Object for the Plants List page (/ui/plants).
 * Encapsulates all UI interactions on the plants list: navigation, search,
 * row inspection, and delete actions.
 */
public class PlantsPage extends BasePage {

    public PlantsPage(Page page) {
        super(page);
    }

    /** Navigate to the plants list page. */
    public void open() {
        navigate("/ui/plants");
    }

    /** @return total number of rows currently rendered in the plants table body. */
    public int getPlantRowCount() {
        return page.locator("table tbody tr").count();
    }

    /**
     * Type a name into the search box and click Search.
     * Waits 1 s for results to load.
     */
    public void searchByName(String name) {
        page.fill("input[name='name']", name);
        page.locator("button:has-text('Search')").first().click();
        page.waitForTimeout(1000);
    }

    /**
     * Return the first table row that contains the given text, or null if not found.
     */
    public Locator findRowContaining(String text) {
        Locator row = page.locator("table tbody tr")
                .filter(new Locator.FilterOptions().setHasText(text))
                .first();
        return row.count() > 0 ? row : null;
    }

    /**
     * Try to locate a plant row using the given name, falling back to the
     * space-stripped version of the name if the first attempt fails.
     * Returns the matching row locator, or null if not found.
     */
    public Locator findRowForPlant(String plantName) {
        searchByName(plantName);
        Locator row = findRowContaining(plantName);
        if (row == null) {
            // Fallback: spaces may be stripped in storage (e.g. "Test Plant" -> "TestPlant")
            String noSpace = plantName.replace(" ", "");
            searchByName(noSpace);
            row = findRowContaining(noSpace);
        }
        return row;
    }

    /**
     * Click the delete button on the given row and auto-accept the confirm dialog.
     * Waits 1500 ms after clicking for the page to refresh.
     */
    public void deleteRow(Locator row) {
        page.onceDialog(Dialog::accept);
        row.locator("button.btn-outline-danger, button[title='Delete'], button:has(i.bi-trash)")
                .first()
                .click();
        page.waitForTimeout(1500);
    }

    /**
     * Check whether the "Add a Plant" button/link is visible on the page.
     */
    public boolean isAddPlantButtonVisible() {
        Locator btn = page.locator("a:has-text('Add a Plant'), a[href='/ui/plants/add']");
        for (int i = 0; i < btn.count(); i++) {
            if (btn.nth(i).isVisible()) return true;
        }
        return false;
    }

    /**
     * Click the "Add a Plant" navigation link/button.
     */
    public void clickAddPlant() {
        page.locator("a:has-text('Add a Plant'), a[href='/ui/plants/add']").first().click();
    }

    /**
     * Return whether any delete button is visible inside the plants table rows.
     */
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

    /**
     * Return the full text of the page body (lower-cased).
     */
    public String getBodyTextLower() {
        return page.locator("body").textContent().toLowerCase();
    }

    /**
     * Return the full text of the page body (original case).
     */
    public String getBodyText() {
        return page.locator("body").textContent();
    }

    /** @return true if the current URL contains the given path fragment. */
    public boolean isOnPath(String pathFragment) {
        return page.url().contains(pathFragment);
    }
}
