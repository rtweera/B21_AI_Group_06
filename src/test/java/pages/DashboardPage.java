package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.testng.Assert;

import java.util.regex.Pattern;

public class DashboardPage extends BasePage {
    public DashboardPage(Page page) {
        super(page);
    }

    public void assertNavigationVisible() {
        page.waitForURL(Pattern.compile(".*/ui/dashboard.*"));
        Assert.assertTrue(page.locator(".sidebar a[href='/ui/dashboard']").isVisible(),
                "Expected dashboard link in the navigation pane");
        Assert.assertTrue(page.locator(".sidebar a[href='/ui/categories']").isVisible(),
                "Expected categories link in the navigation pane");
    }

    public void assertOnlyRequiredSummaryCardsWithCounts() {
        Locator cards = page.locator(".dashboard-card");
        Assert.assertEquals(cards.count(), 3,
                "Dashboard should contain exactly 3 summary cards: Categories, Plants, and Sales");

        assertCardHasNumber(cards, "Categories");
        assertCardHasNumber(cards, "Plants");
        assertCardHasNumber(cards, "Sales");
        Assert.assertEquals(cards.filter(new Locator.FilterOptions().setHasText("Inventory")).count(), 0,
                "Inventory is not part of the required dashboard summary cards");
    }

    private void assertCardHasNumber(Locator cards, String title) {
        Locator card = cards.filter(new Locator.FilterOptions().setHasText(title)).first();
        Assert.assertTrue(card.isVisible(), "Expected dashboard card: " + title);
        Assert.assertTrue(Pattern.compile("\\d").matcher(card.innerText()).find(),
                "Expected card to show a numeric count: " + title);
    }
}
