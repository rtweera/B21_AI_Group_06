package stepdefinitions.api;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class ApiStepSupport {
    protected ApiTestContext.State ctx() {
        return ApiTestContext.context();
    }

    protected APIResponse login(String username, String password) {
        return ApiTestContext.context().auth.login(username, password);
    }

    protected RequestOptions bearer(String token) {
        return RequestOptions.create().setHeader("Authorization", "Bearer " + token);
    }

    protected void remember(APIResponse response) {
        ApiTestContext.State state = ApiTestContext.context();
        state.lastResponse = response;
        state.lastBody = response.text();
    }

    protected String lastBody() {
        String body = ApiTestContext.context().lastBody;
        return body == null ? "" : body;
    }

    protected String extractToken() {
        String token = findJsonString("token", lastBody());
        if (token == null) token = findJsonString("accessToken", lastBody());
        return token;
    }

    protected Long extractId() {
        Matcher matcher = Pattern.compile("\"id\"\\s*:\\s*(\\d+)").matcher(lastBody());
        return matcher.find() ? Long.parseLong(matcher.group(1)) : null;
    }

    protected String uniqueCategoryName() {
        String suffix = Long.toString(System.nanoTime(), 36);
        return "Cat" + suffix.substring(Math.max(0, suffix.length() - 6));
    }

    protected String adminUsername() { return System.getProperty("admin.username", "admin"); }
    protected String adminPassword() { return System.getProperty("admin.password", "admin123"); }
    protected String userUsername()  { return System.getProperty("user.username", "testuser"); }
    protected String userPassword()  { return System.getProperty("user.password", "test123"); }

    private String findJsonString(String field, String body) {
        Matcher m = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*\"([^\"]+)\"").matcher(body);
        return m.find() ? m.group(1) : null;
    }
}
