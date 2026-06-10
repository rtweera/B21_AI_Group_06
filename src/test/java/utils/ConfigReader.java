package utils;


import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static final Properties props = new Properties();

    // Run only once when the class is loaded into mem by JVM
    static {
        try (InputStream is = new FileInputStream("app/application.properties")) {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load application.properties", e);
        }
    }

    public static String getBaseUrl() {
        String url = props.getProperty("api.base-url");
        if (url == null) {
            throw new RuntimeException("api.base-url is missing");
        }
        return url.trim();
    }
}
