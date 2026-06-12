package stepdefinitions;

import io.cucumber.java.en.Then;

public class UiDashboardSteps extends UiStepSupport {
    @Then("I should see the dashboard navigation")
    public void iShouldSeeTheDashboardNavigation() {
        dashboardPage().assertNavigationVisible();
    }

    @Then("the dashboard should show only the categories, plants, and sales summary cards with counts")
    public void dashboardShouldShowOnlyRequiredSummaryCardsWithCounts() {
        dashboardPage().assertOnlyRequiredSummaryCardsWithCounts();
    }
}
