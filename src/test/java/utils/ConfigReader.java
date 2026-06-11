package utils;


import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ConfigReader {
    private static final Properties props = new Properties();

    // Run only once when the class is loaded into mem by JVM
    static {
        Path configPath = Path.of("app", "application.properties");
        if (Files.exists(configPath)) {
            try (InputStream is = new FileInputStream(configPath.toFile())) {
                props.load(is);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load " + configPath, e);
            }
        }
    }

    public static String getBaseUrl() {
        String url = props.getProperty("api.base-url", "http://localhost:8080");
        return url.trim();
    }
}
