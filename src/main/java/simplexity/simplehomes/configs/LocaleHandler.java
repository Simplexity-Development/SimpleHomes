package simplexity.simplehomes.configs;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import simplexity.simplehomes.SimpleHomes;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class LocaleHandler {
    
    private static LocaleHandler instance;
    private final String fileName = "locale.yml";
    private final File localeFile = new File(SimpleHomes.getInstance().getDataFolder(), fileName);
    private final FileConfiguration localeConfig = new YamlConfiguration();
    private final Logger logger = SimpleHomes.getInstance().getLogger();
    //---------
    private String mustBePlayer, provideHomeName, homeAlreadyExists, homeNotFound, nullHome;
    private String homeSet, homeDeleted, homeTeleported;
    
    private LocaleHandler(){
        if (!localeFile.exists()) {
            SimpleHomes.getInstance().saveResource(fileName, false);
        }
    }
    
    public static LocaleHandler getInstance() {
        if (instance == null) instance = new LocaleHandler();
        return instance;
    }
    
    public FileConfiguration getLocaleConfig() {
        return localeConfig;
    }
    
    public void loadLocale(){
        try {
            localeConfig.load(localeFile);
        } catch (IOException | InvalidConfigurationException e) {
            logger.severe("Issue loading locale.yml");
            e.printStackTrace();
        }
        mustBePlayer = localeConfig.getString("errors.must-be-player");
        provideHomeName = localeConfig.getString("errors.provide-home-name");
        homeAlreadyExists = localeConfig.getString("errors.home-already-exists");
        homeNotFound = localeConfig.getString("errors.home-not-found");
        nullHome = localeConfig.getString("errors.null-home");
        homeSet = localeConfig.getString("messages.home-set");
        homeDeleted = localeConfig.getString("messages.home-deleted");
        homeTeleported = localeConfig.getString("messages.home-teleported");
        
    }
    
    
    public String getMustBePlayer() {
        return mustBePlayer;
    }
    public String getProvideHomeName() {
        return provideHomeName;
    }
    public String getHomeNotFound() {
        return homeNotFound;
    }
    public String getHomeExists() {
        return homeAlreadyExists;
    }
    public String getNullHome() {
        return nullHome;
    }
    public String getHomeSet() {
        return homeSet;
    }
    public String getHomeDeleted() {
        return homeDeleted;
    }
    
    public String getHomeTeleported() {
        return homeTeleported;
    }
}
