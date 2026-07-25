package com.nikhil.qa.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found on classpath");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String getBaseUrl() {
        String envOverride = System.getenv("BASE_URL");
        return (envOverride != null && !envOverride.isBlank())
                ? envOverride
                : properties.getProperty("base.url");
    }

    public static int getTimeout() {
        return Integer.parseInt(properties.getProperty("request.timeout", "10000"));
    }

    public static String getApiKey() {
        String envOverride = System.getenv("REQRES_API_KEY");
        return (envOverride != null && !envOverride.isBlank())
                ? envOverride
                : properties.getProperty("api.key");
    }

    public static String getProjectId() {
        String envOverride = System.getenv("REQRES_PROJECT_ID");
        return (envOverride != null && !envOverride.isBlank())
                ? envOverride
                : properties.getProperty("project.id");
    }
}