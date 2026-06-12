package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties props = new Properties();

    static {
        try (InputStream is = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config/config.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config/config.properties", e);
        }
    }

    public static String getBaseUrl() {
        String sysProp = System.getProperty("base.url");
        if (sysProp != null && !sysProp.isBlank()) {
            return trimSlash(sysProp);
        }
        return trimSlash(props.getProperty("base.url", "http://localhost:8080"));
    }

    public static String getAdminUsername() {
        return System.getProperty("admin.username",
                props.getProperty("admin.username", "admin"));
    }

    public static String getAdminPassword() {
        return System.getProperty("admin.password",
                props.getProperty("admin.password", "admin123"));
    }

    public static String getUserUsername() {
        return System.getProperty("user.username",
                props.getProperty("user.username", "testuser"));
    }

    public static String getUserPassword() {
        return System.getProperty("user.password",
                props.getProperty("user.password", "test123"));
    }

    public static boolean isHeadless() {
        String sysProp = System.getProperty("headless");
        if (sysProp != null) return Boolean.parseBoolean(sysProp);
        return Boolean.parseBoolean(props.getProperty("headless", "false"));
    }

    private static String trimSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
