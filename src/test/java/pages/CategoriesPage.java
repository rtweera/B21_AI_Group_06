package pages;


// import com.microsoft.playwright.Page;
// import com.microsoft.playwright.Locator;
// import com.microsoft.playwright.options.SelectOption;
// import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

// public class CategoriesPage {

//     private Page page;

//     public CategoriesPage(Page page) {
//         this.page = page;
//     }

//     public void goToCategories() {
//         page.click("text=Categories");
//     }

//     public void clickAddCategory() {
//         page.click("text=Add A Category");
//     }

//     public void enterCategoryName(String name) {
//         page.fill("input[name='name']", name);
//     }

//     public void clickSave() {
//         page.click("text=Save");
//     }

//     public void clickCancel() {
//         page.click("text=Cancel");
//     }

//     public void verifyCategoryVisible(String categoryName) {
//         assertThat(
//                 page.locator("table tbody tr")
//                         .filter(new Locator.FilterOptions().setHasText(categoryName))
//                         .first()
//         ).isVisible();
//     }

//     public void verifyAddCategoryNotVisible() {
//         assertThat(page.locator("text=Add A Category")).not().isVisible();
//     }
//     public void verifyCategoryNameEntered(String expectedName) {
//         String actualValue = page.locator("input[name='name']").inputValue();

//         if (!actualValue.equals(expectedName)) {
//             throw new AssertionError(
//                     "Expected category name: " + expectedName +
//                             " but found: " + actualValue
//             );
//         }
//     }

//     public void selectParentCategory(String parentCategory) {
//         page.locator("select").selectOption(new SelectOption().setLabel(parentCategory));
//     }

//     public void verifySubCategoryWithParent(String subCategory, String parentCategory) {
//         Locator row = page.locator("table tbody tr")
//                 .filter(new Locator.FilterOptions().setHasText(subCategory))
//                 .filter(new Locator.FilterOptions().setHasText(parentCategory))
//                 .first();

//         assertThat(row).isVisible();
//     }

//     public void verifyNoCategoryFoundMessage(String expectedMessage) {
//         assertThat(page.locator("text=" + expectedMessage)).isVisible();
//     }

//     public void verifyEditAndDeleteButtonsNotClickable() {
//         Locator editButtons = page.locator("button:has-text('Edit'), a:has-text('Edit')");
//         Locator deleteButtons = page.locator("button:has-text('Delete'), a:has-text('Delete')");

//         int editCount = editButtons.count();
//         int deleteCount = deleteButtons.count();

//         for (int i = 0; i < editCount; i++) {
//             assertThat(editButtons.nth(i)).isDisabled();
//         }

//         for (int i = 0; i < deleteCount; i++) {
//             assertThat(deleteButtons.nth(i)).isDisabled();
//         }
//     }
// }

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
