package me.nyaruko166.michosauto.config;

import com.google.gson.Gson;
import de.marhali.json5.Json5;
import de.marhali.json5.Json5Object;
import de.marhali.json5.Json5Options;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Config {

    private static final Logger log = LogManager.getLogger(Config.class);
    private static final Gson gson = new Gson();
    private static final Json5 json5 = new Json5();
    private static final Json5Options json5Options =
            new Json5Options(true, true, false, 2);
    private static final File configFile = new File("./libs/configTest.json5");

    // Eager Singleton instance
    private static final Config instance = new Config();
    private static AppConfig appConfig;

    // Private constructor
    private Config() {
        if (!configFile.exists()) {
            log.info("Config file is missing.");
//            log.info("Please navigate");
            System.exit(0);
        }
        loadConfig();
    }

    // Method to load the configuration
    private static void loadConfig() {
        try {
            String configJson5 = json5
                    .parse(new FileReader(configFile))
                    .toString(json5Options);

            appConfig = gson.fromJson(configJson5, AppConfig.class);
        } catch (FileNotFoundException e) {
            log.error("Error when loading config file", e);
        }
    }

    // Method to update the configuration file
    public static void updateRawConfig(Json5Object configJson5) {
        try {
            FileUtils.writeStringToFile(configFile, configJson5.toString(json5Options), "UTF-8");
//            loadConfig();
            log.info("Configuration updated successfully.");
        } catch (IOException e) {
            log.error("Failed to update config file", e);
        }
    }

    // Combined method to access the AppConfig properties
    public static AppConfig getProperty() {
        return appConfig;
    }

    @SneakyThrows //Sheesh that never gonna happen
    public static Json5Object getRawProperty() {
        return json5.parse(new FileReader(configFile)).getAsJson5Object();
    }


}
