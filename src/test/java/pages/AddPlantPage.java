package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;

/**
 * Page Object for the Add Plant form (/ui/plants/add).
 * Encapsulates all UI interactions for creating a new plant.
 */
public class AddPlantPage extends BasePage {

    public AddPlantPage(Page page) {
        super(page);
    }

    /** Navigate directly to the add-plant form. */
    public void open() {
        navigate("/ui/plants/add");
    }

    /** @return true if the current URL indicates the add-plant page. */
    public boolean isOpen() {
        return page.url().contains("/ui/plants/add");
    }

    /**
     * Fill the plant name field.
     *
     * @param name the plant name to enter (already de-duplicated if needed)
     */
    public void fillName(String name) {
        page.fill("#name", name);
    }

    /**
     * Select a category by its display label.
     * Falls back to the space-stripped label when the original label is not found.
     *
     * @param category the category label as shown in the dropdown
     */
    public void selectCategory(String category) {
        // Category names in the app have no spaces (e.g. "Spider Aloe" is stored as "SpiderAloe").
        // Normalizing upfront avoids a 30-second Playwright timeout on the unstripped label.
        page.selectOption("#categoryId", new SelectOption().setLabel(category.replace(" ", "")));
    }

    /** Fill the price field. */
    public void fillPrice(String price) {
        page.fill("#price", price);
    }

    /** Fill the quantity field. */
    public void fillQuantity(String quantity) {
        page.fill("#quantity", quantity);
    }

    /**
     * Convenience method: fill all plant form fields at once.
     * A unique numeric suffix is appended to the name to prevent duplicate-name
     * errors, <em>unless</em> the price or quantity values indicate an intentionally
     * invalid submission (i.e. price == "0.00" or quantity == "-1").
     *
     * @param name     base plant name
     * @param category category label
     * @param price    price string
     * @param quantity quantity string
     * @return the actual name entered into the form (may have a random suffix)
     */
    public String fillForm(String name, String category, String price, String quantity) {
        String actualName = name;
        if (!price.equals("0.00") && !quantity.equals("-1")) {
            actualName = name + "_" + (int) (Math.random() * 10000);
        }
        fillName(actualName);
        selectCategory(category);
        fillPrice(price);
        fillQuantity(quantity);
        return actualName;
    }

    /**
     * Click the Save / submit button and wait briefly for the navigation/response.
     */
    public void clickSave() {
        page.locator("button:has-text('Save'), button[type='submit']").first().click();
        page.waitForTimeout(1000);
    }

    /**
     * Return all options from the category dropdown as a Playwright Locator list.
     */
    public Locator getCategoryOptions() {
        return page.locator("#categoryId option");
    }

    /**
     * Return the full page body text for assertion purposes.
     */
    public String getBodyText() {
        return page.locator("body").textContent();
    }
}
