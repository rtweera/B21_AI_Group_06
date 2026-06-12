package stepdefinitions.ui;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.testng.Assert.assertTrue;

/**
 * Login, logout, navigation, and dashboard UI steps.
 * Covers 215527A auth/dashboard scenarios and shared cross-feature login steps.
 */
public class AuthUiSteps extends UiStepSupport {

    // ── Login page setup ─────────────────────────────────────────────────────

    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        loginPage().open();
    }

    // ── Admin login ──────────────────────────────────────────────────────────

    @Given("I am logged in as an admin user")
    public void iAmLoggedInAsAdminUser() {
        loginPage().open();
        loginPage().login(adminUsername(), adminPassword());
        dashboardPage().assertNavigationVisible();
    }

    @Given("I am logged in as admin")
    public void iAmLoggedInAsAdmin() {
        System.out.println("[STEP] Logging in as admin...");
        loginPage().open();
        loginPage().login(adminUsername(), adminPassword());
        assertTrue(page().url().contains("/dashboard"), "Should be on dashboard");
        System.out.println("[PASS] Logged in as admin");
    }

    // ── User login ───────────────────────────────────────────────────────────

    @Given("I am logged in as a normal user")
    public void iAmLoggedInAsNormalUser() {
        loginPage().open();
        loginPage().login(userUsername(), userPassword());
        dashboardPage().assertNavigationVisible();
    }

    @Given("I am logged in as user")
    public void iAmLoggedInAsUser() {
        System.out.println("[STEP] Logging in as user...");
        loginPage().open();
        loginPage().login(userUsername(), userPassword());
        assertTrue(page().url().contains("/dashboard"), "Should be on dashboard");
        System.out.println("[PASS] Logged in as user");
    }

    // ── Not logged in ────────────────────────────────────────────────────────

    @Given("I am not logged in")
    public void iAmNotLoggedIn() {
        System.out.println("[STEP] Clearing cookies (not logged in)...");
        salesPage().clearCookies();
        System.out.println("[PASS] Cookies cleared - user is logged out");
    }

    // ── Login form interactions ──────────────────────────────────────────────

    @When("I log in with username {string} and password {string}")
    public void iLogInWithUsernameAndPassword(String username, String password) {
        loginPage().fillUsername(username);
        loginPage().fillPassword(password);
        loginPage().submit();
    }

    @When("I attempt login with username {string} and an empty password")
    public void iAttemptLoginWithUsernameAndEmptyPassword(String username) {
        loginPage().fillUsername(username);
        loginPage().fillPassword("");
        loginPage().submit();
    }

    @When("I attempt login with an empty username and password {string}")
    public void iAttemptLoginWithEmptyUsernameAndPassword(String password) {
        loginPage().fillUsername("");
        loginPage().fillPassword(password);
        loginPage().submit();
    }

    // ── Logout ───────────────────────────────────────────────────────────────

    @When("I click the Logout button")
    public void iClickLogoutButton() {
        System.out.println("[STEP] Clicking Logout button...");
        loginPage().clickLogout();
        System.out.println("[PASS] Clicked Logout");
    }

    @Then("I should see a logout success message")
    public void iShouldSeeLogoutMessage() {
        System.out.println("[STEP] Checking logout success message...");
        assertTrue(loginPage().isLogoutMessageVisible(), "Logout success message should be visible");
        System.out.println("[PASS] Logout message shown");
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    @When("I navigate to the page {string}")
    public void iNavigateToThePage(String path) {
        System.out.println("[STEP] Navigating to " + path + "...");
        salesPage().goToPath(path);
        System.out.println("[PASS] Navigation attempted to " + path);
    }

    // ── Redirect assertions ──────────────────────────────────────────────────

    @Then("I should be redirected to the login page")
    public void iShouldBeRedirectedToLoginPage() {
        System.out.println("[STEP] Checking redirect to login...");
        assertTrue(salesPage().isOnLoginPage(),
                "Should be redirected to login. Actual: " + salesPage().getCurrentUrl());
        System.out.println("[PASS] Redirected to login: " + salesPage().getCurrentUrl());
    }

    // ── Login error assertions ───────────────────────────────────────────────

    @Then("I should see the login error {string}")
    public void iShouldSeeTheLoginError(String message) {
        loginPage().assertLoginError(message);
    }

    @Then("I should see the password validation message {string} in red")
    public void iShouldSeeThePasswordValidationMessageInRed(String message) {
        loginPage().assertFieldValidation("password", message);
    }

    @Then("I should see the username validation message {string} in red")
    public void iShouldSeeTheUsernameValidationMessageInRed(String message) {
        loginPage().assertFieldValidation("username", message);
    }

    // ── Dashboard assertions ─────────────────────────────────────────────────

    @Then("I should see the dashboard navigation")
    public void iShouldSeeTheDashboardNavigation() {
        dashboardPage().assertNavigationVisible();
    }

    @Then("the dashboard should show only the categories, plants, and sales summary cards with counts")
    public void dashboardShouldShowOnlyRequiredSummaryCardsWithCounts() {
        dashboardPage().assertOnlyRequiredSummaryCardsWithCounts();
    }

    // ── Credential helpers ───────────────────────────────────────────────────

    private String adminUsername() { return System.getProperty("admin.username", "admin"); }
    private String adminPassword() { return System.getProperty("admin.password", "admin123"); }
    private String userUsername()  { return System.getProperty("user.username", "testuser"); }
    private String userPassword()  { return System.getProperty("user.password", "test123"); }
}
