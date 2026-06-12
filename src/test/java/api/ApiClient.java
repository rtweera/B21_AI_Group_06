package api;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

// This class handles all API calls (login, create, get, delete)
public class ApiClient {

    private APIRequestContext request;
    private String token;

    public ApiClient(APIRequestContext request) {
        this.request = request;
    }

    // ─── LOGIN: get a token ───────────────────────────────
    public String login(String username, String password) {
        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);

        APIResponse response = request.post("/api/auth/login",
                RequestOptions.create().setData(body));

        JsonObject json = JsonParser.parseString(response.text()).getAsJsonObject();
        if (json.has("token")) {
            this.token = json.get("token").getAsString();
        } else if (json.has("accessToken")) {
            this.token = json.get("accessToken").getAsString();
        }
        return this.token;
    }

    // Build request options with the auth token header
    private RequestOptions withAuth() {
        RequestOptions options = RequestOptions.create();
        if (token != null) {
            options.setHeader("Authorization", "Bearer " + token);
        }
        return options;
    }

    // ─── GENERIC REQUESTS ─────────────────────────────────
    public APIResponse get(String endpoint) {
        return request.get(endpoint, withAuth());
    }

    public APIResponse getNoAuth(String endpoint) {
        return request.get(endpoint);
    }

    public APIResponse post(String endpoint, Object body) {
        return request.post(endpoint, withAuth().setData(body));
    }

    public APIResponse postNoBody(String endpoint) {
        return request.post(endpoint, withAuth());
    }

    public APIResponse delete(String endpoint) {
        return request.delete(endpoint, withAuth());
    }

    public APIResponse put(String endpoint, Object body) {
        return request.put(endpoint, withAuth().setData(body));
    }

    // ─── HELPERS TO CREATE TEST DATA ──────────────────────
    public int createCategory(String name) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        APIResponse res = post("/api/categories", body);

        // Debug: print what the server returned
        System.out.println("[DEBUG] Create category status: " + res.status());
        System.out.println("[DEBUG] Create category response: " + res.text());

        return JsonParser.parseString(res.text()).getAsJsonObject().get("id").getAsInt();
    }

    public int createPlant(int categoryId, String name, int price, int quantity) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("price", price);
        body.put("quantity", quantity);
        APIResponse res = post("/api/plants/category/" + categoryId, body);
        return JsonParser.parseString(res.text()).getAsJsonObject().get("id").getAsInt();
    }

    public int getPlantStock(int plantId) {
        APIResponse res = get("/api/plants/" + plantId);
        return JsonParser.parseString(res.text()).getAsJsonObject().get("quantity").getAsInt();
    }

    public void setPlantStock(int plantId, int newQuantity) {
        APIResponse getRes = get("/api/plants/" + plantId);
        JsonObject plantJson = JsonParser.parseString(getRes.text()).getAsJsonObject();

        JsonObject putBody = new JsonObject();
        putBody.addProperty("id", plantId);
        putBody.addProperty("name", plantJson.get("name").getAsString());
        putBody.addProperty("price", plantJson.get("price").getAsDouble());
        putBody.addProperty("quantity", newQuantity);

        JsonObject categoryObj = new JsonObject();
        if (plantJson.has("categoryId")) {
            categoryObj.addProperty("id", plantJson.get("categoryId").getAsInt());
        } else if (plantJson.has("category")) {
            categoryObj.addProperty("id", plantJson.getAsJsonObject("category").get("id").getAsInt());
        }
        putBody.add("category", categoryObj);

        put("/api/plants/" + plantId, putBody);
    }

    // Create a sale and return its id
    public int createSale(int plantId, int quantity) {
        APIResponse res = postNoBody("/api/sales/plant/" + plantId + "?quantity=" + quantity);
        return JsonParser.parseString(res.text()).getAsJsonObject().get("id").getAsInt();
    }
}