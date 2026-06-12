package api;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;

import java.util.HashMap;
import java.util.Map;

public class PlaywrightApiContext {

    public static Playwright playwright;
    public static APIRequestContext request;

    public static void init() {
        playwright = Playwright.create();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        request = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("http://localhost:8080")
                        .setExtraHTTPHeaders(headers)
        );
    }

    public static void close() {
        if (request != null) {
            request.dispose();
        }

        if (playwright != null) {
            playwright.close();
        }
    }
}