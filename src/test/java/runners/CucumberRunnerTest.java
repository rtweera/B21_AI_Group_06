package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",   // where the English tests are
        glue = {"stepdefinitions", "hooks"},          // where the Java code is
        plugin = {
                "pretty",                             // nice console output
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"  // for Allure report
        },
        tags = "@215552U or @215565L"                 // runs both testers' tests
)
public class CucumberRunnerTest extends AbstractTestNGCucumberTests {
}