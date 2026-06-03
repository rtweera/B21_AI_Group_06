package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

public class DemoSteps {

    @Given("user opens login page")
    public void userOpensLoginPage() {
        System.out.println("[STEP] Given user opens login page");
        // e.g. open browser/navigation placeholder
    }

    @When("user enters correct username and password")
    public void userEntersCorrectUsernameAndPassword() {
        System.out.println("[STEP] When user enters correct username and password");
        // e.g. type username/password placeholder
    }

    @When("user enters incorrect username and password")
    public void userEntersIncorrectUsernameAndPassword() {
        System.out.println("[STEP] When user enters incorrect username and password");
        // e.g. type wrong username/password placeholder
    }

    @And("click login button")
    public void clickLoginButton() {
        System.out.println("[STEP] And click login button");
        // e.g. click button placeholder
    }

    @Then("user should navigate to home page")
    public void userShouldNavigateToHomePage() {
        System.out.println("[STEP] Then user should navigate to home page");
        // e.g. assert navigation placeholder
    }

    @Then("user should see error message")
    public void userShouldSeeErrorMessage() {
        System.out.println("[STEP] Then user should see error message");
        // e.g. assert error message placeholder
    }
}