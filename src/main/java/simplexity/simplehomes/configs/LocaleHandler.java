package simplexity.simplehomes.configs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import simplexity.simplehomes.Home;
import simplexity.simplehomes.SimpleHomes;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class LocaleHandler {
    
    private final MiniMessage miniMessage = SimpleHomes.getMiniMessage();
    private static LocaleHandler instance;
    private final String fileName = "locale.yml";
    private final File localeFile = new File(SimpleHomes.getInstance().getDataFolder(), fileName);
    private final FileConfiguration localeConfig = new YamlConfiguration();
    private final Logger logger = SimpleHomes.getInstance().getLogger();
    //---------
    private String mustBePlayer, provideHomeName, homeAlreadyExists, homeNotFound, nullHome, cannotSetMoreHomes;
    private String insertName, insertWorld, insertXLoc, insertYLoc, insertZLoc;
    private String homeSet, homeDeleted, homeTeleported, pluginReloaded, listHeader, listItem, listNoHomes;
    
    private LocaleHandler() {
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
    
    public void loadLocale() {
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
        cannotSetMoreHomes = localeConfig.getString("errors.cannot-set-more-homes");
        insertName = localeConfig.getString("inserts.name");
        insertWorld = localeConfig.getString("inserts.world");
        insertXLoc = localeConfig.getString("inserts.x-loc");
        insertYLoc = localeConfig.getString("inserts.y-loc");
        insertZLoc = localeConfig.getString("inserts.z-loc");
        listHeader = localeConfig.getString("messages.list-header");
        listItem = localeConfig.getString("messages.list-item");
        listNoHomes = localeConfig.getString("messages.list-no-homes");
        homeSet = localeConfig.getString("messages.home-set");
        homeDeleted = localeConfig.getString("messages.home-deleted");
        homeTeleported = localeConfig.getString("messages.home-teleported");
        pluginReloaded = localeConfig.getString("messages.plugin-reloaded");
        
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
    
    public String getCannotSetMoreHomes() {
        return cannotSetMoreHomes;
    }
    
    public String getPluginReloaded() {
        return pluginReloaded;
    }
    
    public String getInsertName() {
        return insertName;
    }
    
    public String getInsertWorld() {
        return insertWorld;
    }
    
    public String getInsertXLoc() {
        return insertXLoc;
    }
    
    public String getInsertYLoc() {
        return insertYLoc;
    }
    
    public String getInsertZLoc() {
        return insertZLoc;
    }
    
    public String getListHeader() {
        return listHeader;
    }
    
    public String getListItem() {
        return listItem;
    }
    
    public Component locationResolver(Home home, String message) {
        if (home == null) {
            return null;
        }
        Component nameComponent = miniMessage.deserialize(insertName,
                Placeholder.unparsed("name", home.getName()));
        Component worldComponent = miniMessage.deserialize(insertWorld,
                Placeholder.unparsed("world", home.getLocation().getWorld().getName()));
        Component xComponent = miniMessage.deserialize(insertXLoc,
                Placeholder.unparsed("x-loc", String.valueOf(home.getLocation().getX())));
        Component yComponent = miniMessage.deserialize(insertYLoc,
                Placeholder.unparsed("y-loc", String.valueOf(home.getLocation().getY())));
        Component zComponent = miniMessage.deserialize(insertZLoc,
                Placeholder.unparsed("z-loc", String.valueOf(home.getLocation().getZ())));
        return miniMessage.deserialize(message,
                Placeholder.component("name", nameComponent),
                Placeholder.component("world", worldComponent),
                Placeholder.component("x-loc", xComponent),
                Placeholder.component("y-loc", yComponent),
                Placeholder.component("z-loc", zComponent));
    }
    
    public String getListNoHomes() {
        return listNoHomes;
    }
}
