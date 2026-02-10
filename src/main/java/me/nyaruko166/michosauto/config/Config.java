package me.nyaruko166.michosauto.config;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Log4j2
public class Config {

    private final String configPath = "./libs/config.json";

    Gson gson = new Gson();

    private static Config instance;

    private AppConfig appConfig;

    private Config() {
        try {
            File configFile = new File(configPath);
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                FileUtils.writeStringToFile(configFile, gson.toJson(AppConfig.configTemplate()), "UTF-8");
                log.info("Config file created, please add your discord token in /libs/config.json");
                System.exit(0);
            }
            appConfig = gson.fromJson(new FileReader(configFile), AppConfig.class);
            if (appConfig.getDiscord_token().isBlank()) {
                log.error("Discord token is blank, please add your discord token in /libs/config.json");
                System.exit(0);
            }
            log.info("Config loaded successfully");

        } catch (IOException e) {
            log.error(e);
        }
    }

    private static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public static AppConfig getProperty() {
        return getInstance().appConfig;
    }

    public void updateConfig(AppConfig appConfig) {
        try {
            FileUtils.writeStringToFile(new File(configPath), gson.toJson(appConfig), "UTF-8");
        } catch (IOException e) {
            log.error("Error writing to config file", e);
        }
    }

}
