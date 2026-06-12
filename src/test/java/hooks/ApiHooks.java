package hooks;

import api.PlaywrightApiContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class ApiHooks {

    @Before("@API")
    public void setupApi() {
        PlaywrightApiContext.init();
    }

    @After("@API")
    public void tearDownApi() {
        PlaywrightApiContext.close();
    }
}