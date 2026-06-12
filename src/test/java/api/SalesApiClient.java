package api;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;

/** Covers all /api/sales/* endpoints from the OpenAPI spec. */
public class SalesApiClient extends BaseApiClient {

    public SalesApiClient(APIRequestContext request) {
        super(request);
    }

    /** POST /api/sales/plant/{plantId}?quantity={quantity} */
    public APIResponse sell(long plantId, int quantity, String token) {
        return request.post("/api/sales/plant/" + plantId + "?quantity=" + quantity, bearer(token));
    }

    /** GET /api/sales/{id} */
    public APIResponse getById(long id, String token) {
        return request.get("/api/sales/" + id, bearer(token));
    }

    /** GET /api/sales/page?sort={sort} (e.g. sort="soldAt,desc") */
    public APIResponse getPaged(String sort, String token) {
        return request.get("/api/sales/page?sort=" + sort, bearer(token));
    }

    /** DELETE /api/sales/{id} */
    public APIResponse delete(long id, String token) {
        return request.delete("/api/sales/" + id, bearer(token));
    }

    /** GET /api/sales */
    public APIResponse getAll(String token) {
        return request.get("/api/sales", bearer(token));
    }
}
