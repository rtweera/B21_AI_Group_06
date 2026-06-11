package stepdefinitions;

import io.cucumber.java.en.Then;

import static org.testng.Assert.assertEquals;

public class ApiResponseSteps {
    @Then("the API response status should be {int}")
    public void theApiResponseStatusShouldBe(int expectedStatus) {
        assertEquals(ApiTestContext.context().lastResponse.status(), expectedStatus);
    }
}
