package stepdefinitions.ui;

import com.microsoft.playwright.Page;
import pages.AddPlantPage;
import pages.CategoriesPage;
import pages.DashboardPage;
import pages.LoginPage;
import pages.PlantsPage;
import pages.SalesPage;
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

    protected PlantsPage plantsPage() {
        return new PlantsPage(page());
    }

    protected AddPlantPage addPlantPage() {
        return new AddPlantPage(page());
    }

    protected SalesPage salesPage() {
        return new SalesPage(page());
    }
}
