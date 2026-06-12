package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PlantsPage {

    private Page page;

    public PlantsPage(Page page) {
        this.page = page;
    }

    public void goToPlants() {
        page.click("text=Plants");
    }

    public void searchPlant(String plantName) {
        page.fill("input[name='name']", plantName);
        page.click("button:has-text('Search')");
    }

    public void verifyPlantVisible(String plantName) {
        Locator plantRow = page.locator("table tbody tr")
                .filter(new Locator.FilterOptions().setHasText(plantName))
                .first();

        assertThat(plantRow).isVisible();
    }


    public void verifyAddPlantNotVisible() {
        assertThat(page.locator("text=Add a Plant")).not().isVisible();
    }
}