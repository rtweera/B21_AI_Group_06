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
}
