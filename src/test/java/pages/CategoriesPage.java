package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoriesPage extends BasePage {
    public CategoriesPage(Page page) {
        super(page);
    }

    public CategoriesPage(Page page, String baseUrl) {
        this(page);
    }

    public void open() {
        navigate("/ui/categories");
        page.locator("table tbody").waitFor();
    }

    public void assertAddCategoryVisible() {
        Assert.assertTrue(page.locator("a[href='/ui/categories/add']").isVisible(),
                "Expected Add A Category button to be visible for admin");
    }

    public void assertEveryRowHasEditAndDeleteActions() {
        List<Locator> rows = visibleDataRows();
        Assert.assertFalse(rows.isEmpty(), "Expected at least one category row");
        for (Locator row : rows) {
            Assert.assertTrue(row.locator("a[title='Edit'], a[href*='/ui/categories/edit']").first().isVisible(),
                    "Expected visible Edit action in row: " + row.innerText());
            Assert.assertTrue(row.locator("button[title='Delete'], form[action*='/ui/categories/delete'] button").first().isVisible(),
                    "Expected visible Delete action in row: " + row.innerText());
        }
    }

    public void assertNoManagementControlsVisible() {
        Assert.assertEquals(visibleCount("a[href='/ui/categories/add']"), 0,
                "Add A Category should not be visible for normal users");
        Assert.assertEquals(visibleCount("a[title='Edit'], a[href*='/ui/categories/edit']"), 0,
                "Edit actions should not be visible for normal users");
        Assert.assertEquals(visibleCount("button[title='Delete'], form[action*='/ui/categories/delete'] button"), 0,
                "Delete actions should not be visible for normal users");
    }

    public String firstCategoryName() {
        List<Locator> rows = visibleDataRows();
        Assert.assertFalse(rows.isEmpty(), "Expected at least one category row to search");
        return rows.get(0).locator("td").nth(1).innerText().trim();
    }

    public void searchByName(String name) {
        page.locator("input[name='name']").fill(name);
        page.locator("button[type='submit']").filter(new Locator.FilterOptions().setHasText("Search")).click();
        page.locator("table tbody").waitFor();
    }

    public void assertOnlyRowsContaining(String name) {
        List<Locator> rows = visibleDataRows();
        Assert.assertFalse(rows.isEmpty(), "Expected category search to return at least one row");
        String expected = name.toLowerCase(Locale.ROOT);
        for (Locator row : rows) {
            String rowName = row.locator("td").nth(1).innerText().trim().toLowerCase(Locale.ROOT);
            Assert.assertTrue(rowName.contains(expected),
                    "Expected visible row name to contain '" + name + "', but was: " + rowName);
        }
    }

    private List<Locator> visibleDataRows() {
        Locator rows = page.locator("table tbody tr");
        List<Locator> visibleRows = new ArrayList<>();
        for (int index = 0; index < rows.count(); index++) {
            Locator row = rows.nth(index);
            if (row.isVisible() && !row.innerText().contains("No category found")) {
                visibleRows.add(row);
            }
        }
        return visibleRows;
    }

    private int visibleCount(String selector) {
        Locator locator = page.locator(selector);
        int visible = 0;
        for (int index = 0; index < locator.count(); index++) {
            if (locator.nth(index).isVisible()) {
                visible++;
            }
        }
        return visible;
    }
}
