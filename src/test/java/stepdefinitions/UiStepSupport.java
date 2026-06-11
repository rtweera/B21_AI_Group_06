package stepdefinitions;

import com.microsoft.playwright.Page;
import pages.CategoriesPage;
import pages.DashboardPage;
import pages.LoginPage;
import utils.PlaywrightFactory;

abstract class UiStepSupport {
    protected Page page() {
        return PlaywrightFactory.getPage();
    }

    protected LoginPage loginPage() {
        return new LoginPage(page());
    }

    protected DashboardPage dashboardPage() {
        return new DashboardPage(page());
    }

    protected CategoriesPage categoriesPage() {
        return new CategoriesPage(page());
    }
}
