package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.SelectOption;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CategoriesPage {

    private Page page;

    public CategoriesPage(Page page) {
        this.page = page;
    }

    public void goToCategories() {
        page.click("text=Categories");
    }

    public void clickAddCategory() {
        page.click("text=Add A Category");
    }

    public void enterCategoryName(String name) {
        page.fill("input[name='name']", name);
    }

    public void clickSave() {
        page.click("text=Save");
    }

    public void clickCancel() {
        page.click("text=Cancel");
    }

    public void verifyCategoryVisible(String categoryName) {
        assertThat(
                page.locator("table tbody tr")
                        .filter(new Locator.FilterOptions().setHasText(categoryName))
                        .first()
        ).isVisible();
    }

    public void verifyAddCategoryNotVisible() {
        assertThat(page.locator("text=Add A Category")).not().isVisible();
    }
    public void verifyCategoryNameEntered(String expectedName) {
        String actualValue = page.locator("input[name='name']").inputValue();

        if (!actualValue.equals(expectedName)) {
            throw new AssertionError(
                    "Expected category name: " + expectedName +
                            " but found: " + actualValue
            );
        }
    }

    public void selectParentCategory(String parentCategory) {
        page.locator("select").selectOption(new SelectOption().setLabel(parentCategory));
    }

    public void verifySubCategoryWithParent(String subCategory, String parentCategory) {
        Locator row = page.locator("table tbody tr")
                .filter(new Locator.FilterOptions().setHasText(subCategory))
                .filter(new Locator.FilterOptions().setHasText(parentCategory))
                .first();

        assertThat(row).isVisible();
    }

    public void verifyNoCategoryFoundMessage(String expectedMessage) {
        assertThat(page.locator("text=" + expectedMessage)).isVisible();
    }

    public void verifyEditAndDeleteButtonsNotClickable() {
        Locator editButtons = page.locator("button:has-text('Edit'), a:has-text('Edit')");
        Locator deleteButtons = page.locator("button:has-text('Delete'), a:has-text('Delete')");

        int editCount = editButtons.count();
        int deleteCount = deleteButtons.count();

        for (int i = 0; i < editCount; i++) {
            assertThat(editButtons.nth(i)).isDisabled();
        }

        for (int i = 0; i < deleteCount; i++) {
            assertThat(deleteButtons.nth(i)).isDisabled();
        }
    }
}