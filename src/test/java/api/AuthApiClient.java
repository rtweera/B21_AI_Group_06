package api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;

import java.util.Map;

public class AuthApiClient extends BaseApiClient {

    public AuthApiClient(APIRequestContext request) {
        super(request);
    }

    /** POST /api/auth/login */
    public APIResponse login(String username, String password) {
        return request.post("/api/auth/login",
                RequestOptions.create().setData(Map.of("username", username, "password", password)));
    }

    /** POST /api/auth/login with an empty body — used by boundary tests */
    public APIResponse loginWithEmptyBody() {
        return request.post("/api/auth/login", RequestOptions.create().setData(Map.of()));
    }

    /** Extracts the bearer token string from a login response body. */
    public String extractToken(APIResponse response) {
        String body = response.text();
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        if (json.has("token")) return json.get("token").getAsString();
        if (json.has("accessToken")) return json.get("accessToken").getAsString();
        if (json.has("content") && json.get("content").isJsonObject()) {
            JsonObject content = json.getAsJsonObject("content");
            if (content.has("token")) return content.get("token").getAsString();
        }
        throw new RuntimeException("Token not found in response: " + body);
    }
}
