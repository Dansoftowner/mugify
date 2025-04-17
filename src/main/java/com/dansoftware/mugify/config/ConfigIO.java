package com.dansoftware.mugify.config;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigIO {
    private static final String APP_NAME = "Mugify";
    private static final String CONFIG_FILE = "preferences.json";
    private static final Gson gson = new Gson();

    private ConfigIO() {
    }

    private static String getConfigPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String configDir;

        if (os.contains("win")) {
            configDir = Path.of(System.getenv("APPDATA"), APP_NAME).toAbsolutePath().toString();
        } else {
            configDir = Path.of(System.getProperty("user.home"), ".myapp").toAbsolutePath().toString();
        }

        new File(configDir).mkdirs();
        return Path.of(configDir, CONFIG_FILE).toAbsolutePath().toString();
    }

    public static synchronized void save(Preferences preferences) throws IOException {
        try (FileWriter writer = new FileWriter(getConfigPath())) {
            gson.toJson(preferences, writer);
        }
    }

    public static synchronized Preferences load() throws IOException {
        File configFile = new File(getConfigPath());
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                Preferences preferences = gson.fromJson(reader, Preferences.class);
                if (preferences != null)
                    return preferences;
            }
        }
        return new Preferences();
    }
}