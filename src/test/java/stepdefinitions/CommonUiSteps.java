package stepdefinitions;

import com.microsoft.playwright.Page;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.LoginPage;
import pages.PlantsPage;
import pages.SalesPage;
import utils.PlaywrightFactory;

import static org.testng.Assert.assertTrue;

// Shared steps: login, logout state, navigation, redirect checks
// These are used across multiple feature files
public class CommonUiSteps {

    private Page page;
    private SalesPage salesPage;
    private LoginPage loginPage;
    private PlantsPage plantsPage;

    @Before("@UI")
    public void initializePages() {
        page = PlaywrightFactory.getPage();
        salesPage = new SalesPage(page);
        loginPage = new LoginPage(page);
        plantsPage = new PlantsPage(page);
    }

    // ─── LOGIN STEPS ──────────────────────────────────────

    @Given("I am logged in as admin")
    public void iAmLoggedInAsAdmin() {
        System.out.println("[STEP] Logging in as admin...");
        loginPage.open();
        loginPage.login("admin", "admin123");
        assertTrue(page.url().contains("/dashboard"), "Should be on dashboard");
        System.out.println("[PASS] Logged in as admin");
    }

    @Given("I am not logged in")
    public void iAmNotLoggedIn() {
        System.out.println("[STEP] Clearing cookies (not logged in)...");
        salesPage.clearCookies();
        System.out.println("[PASS] Cookies cleared - user is logged out");
    }

    // ─── SHARED NAVIGATION ────────────────────────────────

    @When("I navigate to the page {string}")
    public void iNavigateToThePage(String path) {
        System.out.println("[STEP] Navigating to " + path + "...");
        salesPage.goToPath(path);
        System.out.println("[PASS] Navigation attempted to " + path);
    }

    // ─── SHARED REDIRECT CHECK ────────────────────────────

    @Then("I should be redirected to the login page")
    public void iShouldBeRedirectedToLoginPage() {
        System.out.println("[STEP] Checking redirect to login...");
        assertTrue(salesPage.isOnLoginPage(),
                "Should be redirected to login. Actual: " + salesPage.getCurrentUrl());
        System.out.println("[PASS] Redirected to login: " + salesPage.getCurrentUrl());
    }

    // ─── LOGIN AS USER ────────────────────────────────────

    @Given("I am logged in as user")
    public void iAmLoggedInAsUser() {
        System.out.println("[STEP] Logging in as user...");
        loginPage.open();
        loginPage.login("testuser", "test123");
        assertTrue(page.url().contains("/dashboard"), "Should be on dashboard");
        System.out.println("[PASS] Logged in as user");
    }

    // ─── LOGOUT STEPS ─────────────────────────────────────

    @When("I click the Logout button")
    public void iClickLogoutButton() {
        System.out.println("[STEP] Clicking Logout button...");
        loginPage.clickLogout();
        System.out.println("[PASS] Clicked Logout");
    }

    @Then("I should see a logout success message")
    public void iShouldSeeLogoutMessage() {
        System.out.println("[STEP] Checking logout success message...");
        assertTrue(loginPage.isLogoutMessageVisible(),
                "Logout success message should be visible");
        System.out.println("[PASS] Logout message shown");
    }
}