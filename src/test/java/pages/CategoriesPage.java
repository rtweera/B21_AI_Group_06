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
        Locator editButtons = page.locator("a[title='Edit'], button[title='Edit']");
        Locator deleteButtons = page.locator("button[title='Delete'], a[title='Delete']");

        int editCount = editButtons.count();
        int deleteCount = deleteButtons.count();

        for (int i = 0; i < editCount; i++) {
            Locator btn = editButtons.nth(i);
            String tagName = btn.evaluate("el => el.tagName").toString().toLowerCase();
            if (tagName.equals("a")) {
                String disabledAttr = btn.getAttribute("disabled");
                if (disabledAttr == null || (!disabledAttr.equals("disabled") && !disabledAttr.equals("true") && !disabledAttr.isEmpty())) {
                    throw new AssertionError("Anchor edit button is not disabled in HTML");
                }
            } else {
                assertThat(btn).isDisabled();
            }
        }

        for (int i = 0; i < deleteCount; i++) {
            assertThat(deleteButtons.nth(i)).isDisabled();
        }
    }
}