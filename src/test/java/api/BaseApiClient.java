package api;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.options.RequestOptions;

public abstract class BaseApiClient {
    protected final APIRequestContext request;

    protected BaseApiClient(APIRequestContext request) {
        this.request = request;
    }

    protected RequestOptions bearer(String token) {
        return RequestOptions.create().setHeader("Authorization", "Bearer " + token);
    }

    protected RequestOptions noAuth() {
        return RequestOptions.create();
    }
}
