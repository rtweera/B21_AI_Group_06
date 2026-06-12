package api;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;

import java.util.HashMap;
import java.util.Map;

/** Covers all /api/categories/* endpoints from the OpenAPI spec. */
public class CategoriesApiClient extends BaseApiClient {

    public CategoriesApiClient(APIRequestContext request) {
        super(request);
    }

    /** GET /api/categories */
    public APIResponse getAll(String token) {
        return request.get("/api/categories", bearer(token));
    }

    /** GET /api/categories — no auth header, for 401 boundary tests */
    public APIResponse getAllNoAuth() {
        return request.get("/api/categories", noAuth());
    }

    /** GET /api/categories/{id} */
    public APIResponse getById(long id, String token) {
        return request.get("/api/categories/" + id, bearer(token));
    }

    /** GET /api/categories/main */
    public APIResponse getMain(String token) {
        return request.get("/api/categories/main", bearer(token));
    }

    /** GET /api/categories/sub-categories */
    public APIResponse getSubCategories(String token) {
        return request.get("/api/categories/sub-categories", bearer(token));
    }

    /** GET /api/categories/page?page=&size= */
    public APIResponse getPaged(int page, int size, String token) {
        return request.get("/api/categories/page?page=" + page + "&size=" + size, bearer(token));
    }

    /** POST /api/categories — creates a top-level category */
    public APIResponse create(String name, String token) {
        return request.post("/api/categories", bearer(token).setData(Map.of("name", name)));
    }

    /** POST /api/categories — creates a sub-category under the given parent */
    public APIResponse createSub(String name, long parentId, String token) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("parent", Map.of("id", parentId));
        return request.post("/api/categories", bearer(token).setData(body));
    }

    /** POST /api/categories — sends an arbitrary body; useful for validation/error tests */
    public APIResponse createWithBody(Object body, String token) {
        return request.post("/api/categories", bearer(token).setData(body));
    }

    /** PUT /api/categories/{id} */
    public APIResponse update(long id, String name, String token) {
        return request.put("/api/categories/" + id, bearer(token).setData(Map.of("name", name)));
    }

    /** PUT /api/categories/{id} — includes optional parentId remap */
    public APIResponse update(long id, String name, Long parentId, String token) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        if (parentId != null) body.put("parentId", parentId);
        return request.put("/api/categories/" + id, bearer(token).setData(body));
    }

    /** DELETE /api/categories/{id} */
    public APIResponse delete(long id, String token) {
        return request.delete("/api/categories/" + id, bearer(token));
    }
}
