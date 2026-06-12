package hooks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import stepdefinitions.api.ApiTestContext;
import utils.ConfigReader;

public class ApiHooks {

    @Before(value = "@API", order = 0)
    public void initApiContext(Scenario scenario) {
        System.out.println("\n=== STARTING API: " + scenario.getName() + " ===");
        ApiTestContext.initContext();
        setupTestSubCategory();
    }

    @After(value = "@API", order = 0)
    public void destroyApiContext(Scenario scenario) {
        ApiTestContext.destroyContext();
        System.out.println("=== DONE API: " + scenario.getName() + " ===\n");
    }

    private void setupTestSubCategory() {
        try {
            ApiTestContext.State state = ApiTestContext.context();
            String token = loginAdmin(state);
            state.adminToken = token;
            int subCatId = getOrCreateSubCategory(state, token, "ApiTestCat", "ApiTestSub");
            state.testSubCategoryId = subCatId;
            System.out.println("[SETUP] testSubCategoryId=" + subCatId);
        } catch (Exception e) {
            System.out.println("[WARN] setupTestSubCategory failed: " + e.getMessage());
        }
    }

    private String loginAdmin(ApiTestContext.State state) {
        String body = "{\"username\":\"" + ConfigReader.getAdminUsername()
                + "\",\"password\":\"" + ConfigReader.getAdminPassword() + "\"}";
        APIResponse res = state.api.post("/api/auth/login",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(body));
        JsonObject json = JsonParser.parseString(res.text()).getAsJsonObject();
        return json.has("token") ? json.get("token").getAsString()
                : json.getAsJsonObject("content").get("token").getAsString();
    }

    private int getOrCreateSubCategory(ApiTestContext.State state, String token,
                                        String parentName, String subName) {
        APIResponse getCats = state.api.get("/api/categories",
                RequestOptions.create().setHeader("Authorization", "Bearer " + token));
        int parentId = 0;
        int subId = 0;
        if (getCats.status() == 200) {
            JsonArray cats = JsonParser.parseString(getCats.text()).getAsJsonArray();
            for (JsonElement el : cats) {
                JsonObject c = el.getAsJsonObject();
                String name = c.get("name").getAsString();
                if (parentName.equals(name)) parentId = c.get("id").getAsInt();
                if (subName.equals(name))   subId   = c.get("id").getAsInt();
            }
        }
        if (parentId == 0) {
            APIResponse res = state.api.post("/api/categories",
                    RequestOptions.create()
                            .setHeader("Authorization", "Bearer " + token)
                            .setHeader("Content-Type", "application/json")
                            .setData("{\"name\":\"" + parentName + "\"}"));
            if (res.status() == 200 || res.status() == 201) {
                JsonObject j = JsonParser.parseString(res.text()).getAsJsonObject();
                parentId = j.has("id") ? j.get("id").getAsInt()
                        : j.getAsJsonObject("content").get("id").getAsInt();
            }
        }
        if (subId == 0 && parentId > 0) {
            APIResponse res = state.api.post("/api/categories",
                    RequestOptions.create()
                            .setHeader("Authorization", "Bearer " + token)
                            .setHeader("Content-Type", "application/json")
                            .setData("{\"name\":\"" + subName + "\",\"parent\":{\"id\":" + parentId + "}}"));
            if (res.status() == 200 || res.status() == 201) {
                JsonObject j = JsonParser.parseString(res.text()).getAsJsonObject();
                subId = j.has("id") ? j.get("id").getAsInt()
                        : j.getAsJsonObject("content").get("id").getAsInt();
            }
        }
        return subId;
    }
}
