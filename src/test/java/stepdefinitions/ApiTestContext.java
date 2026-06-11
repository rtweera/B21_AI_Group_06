package stepdefinitions;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import utils.ConfigReader;

import java.util.Map;

public class ApiTestContext {
    private static final ThreadLocal<State> CURRENT = new ThreadLocal<>();

    static State context() {
        State state = CURRENT.get();
        if (state == null) {
            throw new IllegalStateException("API test context is not initialized");
        }
        return state;
    }

    @Before("@215527A and @api")
    public void setUpApiContext() {
        initContext();
    }

    @After("@215527A and @api")
    public void tearDownApiContext() {
        destroyContext();
    }

    // -----------------------------------------------------------------------
    // Tester 215565L – @API-tagged scenarios
    // -----------------------------------------------------------------------

    @Before("@215565L and @API")
    public void setUpApiContext215565L() {
        initContext();
    }

    @After("@215565L and @API")
    public void tearDownApiContext215565L() {
        destroyContext();
    }

    private void initContext() {
        State state = new State();
        state.playwright = Playwright.create();
        state.api = state.playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(baseUrl())
                        .setExtraHTTPHeaders(Map.of("Content-Type", "application/json")));
        CURRENT.set(state);
    }

    private void destroyContext() {
        State state = CURRENT.get();
        if (state != null) {
            if (state.api != null) state.api.dispose();
            if (state.playwright != null) state.playwright.close();
        }
        CURRENT.remove();
    }

    private String baseUrl() {
        String configured = System.getProperty("base.url");
        if (configured != null && !configured.isBlank()) {
            return trimTrailingSlash(configured);
        }
        try {
            return trimTrailingSlash(ConfigReader.getBaseUrl());
        } catch (RuntimeException exception) {
            return "http://localhost:8080";
        }
    }

    private String trimTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    static final class State {
        Playwright playwright;
        APIRequestContext api;
        APIResponse lastResponse;
        String lastBody;
        String adminToken;
        String userToken;
        Long createdCategoryId;
        String createdCategoryName;
        // Fields used by 215565L plant/sale tests
        Long createdPlantId;
        Long createdSaleId;
    }
}
