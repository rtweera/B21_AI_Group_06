package pages;

import com.microsoft.playwright.Page;
import utils.ConfigReader;

public abstract class BasePage {

    protected final Page page;
    protected final String baseUrl;

    protected BasePage(Page page) {
        this.page = page;
        this.baseUrl = ConfigReader.getBaseUrl();
    }

    protected void navigate(String path) {
        page.navigate(baseUrl + path);
    }

    public String getCurrentUrl() {
        return page.url();
    }

    public void clearCookies() {
        page.context().clearCookies();
    }
}