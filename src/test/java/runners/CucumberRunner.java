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
        tags = "@UI_SLS_ADM_001 or @UI_SLS_ADM_002 or @UI_SLS_ADM_003 or " +
                "@UI_SLS_ADM_004 or @UI_SLS_ADM_005 or @UI_SLS_USR_001 or " +
                "@UI_SLS_USR_002 or @UI_SLS_USR_003"                 // ONLY run this one test for now
)
public class CucumberRunner extends AbstractTestNGCucumberTests {
}