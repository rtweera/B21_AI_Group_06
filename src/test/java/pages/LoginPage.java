package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.testng.Assert;

public class LoginPage extends BasePage{
    public LoginPage(Page page) {
        super(page);
    }

    public LoginPage(Page page, String baseUrl) {
        this(page);
    }

    public void open() {
        navigate("/ui/login");
    }

    public void fillUsername(String username) {
        page.fill("[name='username']", username);
    }

    public void fillPassword(String password) {
        page.fill("[name='password']", password);
    }

    public void submit() {
        page.locator("button[type='submit']").click();
    }

    public void login(String username, String password) {
        fillUsername(username);
        fillPassword(password);
        submit();
        page.waitForURL("**/dashboard");
    }

    public boolean isDisplayed() {
        return page.url().contains("/login");
    }

    public void assertLoginError(String message) {
        Locator error = page.locator(".alert-danger").filter(new Locator.FilterOptions().setHasText(message));
        error.waitFor();
        Assert.assertTrue(error.isVisible(), "Expected login error: " + message);
    }

    public void assertFieldValidation(String fieldName, String message) {
        Locator input = page.locator("input[name='" + fieldName + "']");
        Locator feedback = page.locator("input[name='" + fieldName + "'] + .invalid-feedback");
        Assert.assertTrue(input.evaluate("el => el.classList.contains('is-invalid')").equals(Boolean.TRUE),
                "Expected " + fieldName + " field to be marked invalid");
        Assert.assertEquals(feedback.innerText().trim(), message);
        Assert.assertTrue(feedback.isVisible(), "Expected validation feedback to be visible");
    }

    public void navigateToLogin() {
        open();
    }

    public void clickLogout() {
        page.locator("a:has-text('Logout'), button:has-text('Logout'), [href*='logout']").first().click();
        page.waitForURL("**/login");
    }

    public boolean isLogoutMessageVisible() {
        String body = page.locator("body").textContent().toLowerCase();
        return body.contains("logout") || body.contains("logged out") || body.contains("signed out")
                || page.locator(".alert-success, .alert-info, #logout-message").count() > 0;
    }
}
