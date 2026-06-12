package stepdefinitions.api;

import api.AuthApiClient;
import api.CategoriesApiClient;
import api.PlantsApiClient;
import api.SalesApiClient;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import utils.ConfigReader;

import java.util.Map;

public class ApiTestContext {
    private static final ThreadLocal<State> CURRENT = new ThreadLocal<>();

    public static State context() {
        State state = CURRENT.get();
        if (state == null) {
            throw new IllegalStateException("API test context not initialised — ensure @API tag is present");
        }
        return state;
    }

    public static void initContext() {
        State state = new State();
        state.playwright = Playwright.create();
        state.api = state.playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(ConfigReader.getBaseUrl())
                        .setExtraHTTPHeaders(Map.of("Content-Type", "application/json")));
        state.auth = new AuthApiClient(state.api);
        state.categories = new CategoriesApiClient(state.api);
        state.plants = new PlantsApiClient(state.api);
        state.sales = new SalesApiClient(state.api);
        CURRENT.set(state);
    }

    public static void destroyContext() {
        State state = CURRENT.get();
        if (state != null) {
            if (state.api != null) state.api.dispose();
            if (state.playwright != null) state.playwright.close();
        }
        CURRENT.remove();
    }

    public static final class State {
        public Playwright playwright;
        public APIRequestContext api;
        public AuthApiClient auth;
        public CategoriesApiClient categories;
        public PlantsApiClient plants;
        public SalesApiClient sales;
        public APIResponse lastResponse;
        public String lastBody;
        public String adminToken;
        public String userToken;
        public String activeToken;
        public Long createdCategoryId;
        public String createdCategoryName;
        public Long createdPlantId;
        public Long createdSaleId;
        public int storedPlantId;
        public int stockBeforeSale;
        public int storedSaleId;
        public int testSubCategoryId;
    }
}
