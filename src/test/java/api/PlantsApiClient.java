package api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;

import java.util.Map;

/** Covers all /api/plants/* endpoints from the OpenAPI spec. */
public class PlantsApiClient extends BaseApiClient {

    public PlantsApiClient(APIRequestContext request) {
        super(request);
    }

    /** GET /api/plants */
    public APIResponse getAll(String token) {
        return request.get("/api/plants", bearer(token));
    }

    /** GET /api/plants/{id} */
    public APIResponse getById(long id, String token) {
        return request.get("/api/plants/" + id, bearer(token));
    }

    /** GET /api/plants/category/{categoryId} */
    public APIResponse getByCategory(long categoryId, String token) {
        return request.get("/api/plants/category/" + categoryId, bearer(token));
    }

    /** GET /api/plants/paged?sort=price,asc (or any sort expression) */
    public APIResponse getPaged(String sort, String token) {
        return request.get("/api/plants/paged?sort=" + sort, bearer(token));
    }

    /** GET /api/plants/summary */
    public APIResponse getSummary(String token) {
        return request.get("/api/plants/summary", bearer(token));
    }

    /** POST /api/plants/category/{categoryId} with standard fields */
    public APIResponse create(long categoryId, String name, double price, int quantity, String token) {
        return request.post("/api/plants/category/" + categoryId,
                bearer(token).setData(Map.of("name", name, "price", price, "quantity", quantity)));
    }

    /**
     * POST /api/plants/category/{categoryId} with a raw JSON string body.
     * Used by Swagger contract tests that need to send the exact documented example.
     */
    public APIResponse createWithRawBody(long categoryId, String jsonBody, String token) {
        return request.post("/api/plants/category/" + categoryId,
                bearer(token).setHeader("Content-Type", "application/json").setData(jsonBody));
    }

    /** PUT /api/plants/{id} */
    public APIResponse update(long id, String name, double price, int quantity, String token) {
        return request.put("/api/plants/" + id,
                bearer(token).setData(Map.of("name", name, "price", price, "quantity", quantity)));
    }

    /** DELETE /api/plants/{id} */
    public APIResponse delete(long id, String token) {
        return request.delete("/api/plants/" + id, bearer(token));
    }

    /**
     * Reads the current quantity for a plant.
     * Uses GET /api/plants/{id} which returns PlantEditResponseDTO.
     */
    public int getQuantity(long id, String token) {
        APIResponse res = getById(id, token);
        return JsonParser.parseString(res.text()).getAsJsonObject().get("quantity").getAsInt();
    }

    /**
     * Updates only the quantity field of a plant by first fetching current name/price,
     * then issuing a PUT with the new quantity.
     */
    public void setQuantity(long id, int newQuantity, String token) {
        APIResponse getRes = getById(id, token);
        JsonObject plant = JsonParser.parseString(getRes.text()).getAsJsonObject();
        update(id,
                plant.get("name").getAsString(),
                plant.get("price").getAsDouble(),
                newQuantity,
                token);
    }
}
