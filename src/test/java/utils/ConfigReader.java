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
            throw new RuntimeException(e);
        }
    }

    public static String getBaseUrl() {
        return props.getProperty("api.base-url").trim();
    }
}
