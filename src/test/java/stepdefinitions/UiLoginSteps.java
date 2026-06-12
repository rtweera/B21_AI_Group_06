package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UiLoginSteps extends UiStepSupport {
    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        loginPage().open();
    }

    @Given("I am logged in as an admin user")
    public void iAmLoggedInAsAdminUser() {
        loginPage().open();
        loginPage().login(adminUsername(), adminPassword());
        dashboardPage().assertNavigationVisible();
    }

    @Given("I am logged in as a normal user")
    public void iAmLoggedInAsNormalUser() {
        loginPage().open();
        loginPage().login(userUsername(), userPassword());
        dashboardPage().assertNavigationVisible();
    }

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

    private String adminUsername() {
        return System.getProperty("admin.username", "admin");
    }

    private String adminPassword() {
        return System.getProperty("admin.password", "admin123");
    }

    private String userUsername() {
        return System.getProperty("user.username", "testuser");
    }

    private String userPassword() {
        return System.getProperty("user.password", "test123");
    }
}
