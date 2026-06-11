package pages;

import com.microsoft.playwright.Page;

public class LoginPage extends BasePage{
    public LoginPage(Page page) {
        super(page);
    }

    public void open() {
        navigate("/ui/login");
    }

    public void login(String username, String password) {
        page.fill("[name='username']", username);
        page.fill("[name='password']", password);
        page.locator("button[type='submit']").click();
        page.waitForURL("**/dashboard");
    }

    public boolean isDisplayed() {
        return page.url().contains("/login");
    }

    // Click the Logout button/link
    public void clickLogout() {
        page.locator("a:has-text('Logout'), button:has-text('Logout')")
                .first()
                .click();
        page.waitForTimeout(1000);
    }

    // Check if a logout success message is shown anywhere on the page
    public boolean isLogoutMessageVisible() {
        String body = page.locator("body")
                .textContent()
                .toLowerCase();
        return body.contains("logged out")
                || body.contains("logout")
                || body.contains("successfully");
    }
}
