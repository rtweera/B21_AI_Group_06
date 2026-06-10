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
}
