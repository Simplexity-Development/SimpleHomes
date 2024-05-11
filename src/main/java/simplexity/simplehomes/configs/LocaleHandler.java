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
    private String insertName, insertWorld, insertXLoc, insertYLoc, insertZLoc, insertOverride;
    private String homeSet, homeDeleted, homeTeleported, pluginReloaded, listHeader, listItem, listNoHomes;
    private String blacklistedWarning, voidWarning, fireWarning, blocksWarning, lavaWarning, waterWarning, notSolidWarning;
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
        mustBePlayer = localeConfig.getString("errors.must-be-player", "<red>Sorry, only a player can run that command!</red>");
        provideHomeName = localeConfig.getString("errors.provide-home-name", "<gray>Please provide a home name</gray>");
        homeAlreadyExists = localeConfig.getString("errors.home-already-exists", "<red>Sorry, you already have a home with that name!</red> <gray>Use <yellow>/sethome <name> -o</yellow> to overwrite a home</gray>");
        homeNotFound = localeConfig.getString("errors.home-not-found", "<red>Home <yellow><name></yellow> not found</red>");
        nullHome = localeConfig.getString("errors.null-home", "<red>Home '<name>' does not exist</red>");
        cannotSetMoreHomes = localeConfig.getString("errors.cannot-set-more-homes", "<red>You have already set <dark_red><bold><value></bold></dark_red> homes- you cannot set any more. Delete one of your current homes to set a new one.</red>");
        insertName = localeConfig.getString("inserts.name", "<yellow><name></yellow>");
        insertWorld = localeConfig.getString("inserts.world", "<yellow><world></yellow>");
        insertXLoc = localeConfig.getString("inserts.x-loc", "<yellow><x-loc>x</yellow>,");
        insertYLoc = localeConfig.getString("inserts.y-loc", "<yellow><y-loc>y</yellow>,");
        insertZLoc = localeConfig.getString("inserts.z-loc", "<yellow><z-loc>z</yellow>");
        insertOverride = localeConfig.getString("inserts.override", "<gray>You can type <yellow>/home <name> -o</yellow> in order to teleport anyways</gray>");
        listHeader = localeConfig.getString("messages.list-header", "<white><bold>[</bold><aqua>Homes</aqua><bold>]</bold></white>");
        listItem = localeConfig.getString("messages.list-item", "<gray>  - <name> in <world> at <x-loc> <y-loc> <z-loc>");
        listNoHomes = localeConfig.getString("messages.list-no-homes", "<gray>You have no homes set</gray>");
        homeSet = localeConfig.getString("messages.home-set", "<green>Home <name> has been set</green>");
        homeDeleted = localeConfig.getString("messages.home-deleted", "<gray>Home <name> in <world> at <x-loc> <y-loc> <z-loc> has been deleted</gray>");
        homeTeleported = localeConfig.getString("messages.home-teleported", "<gray>Teleported successfully to <yellow><name></yellow></gray>");
        pluginReloaded = localeConfig.getString("messages.plugin-reloaded", "<gold>SimpleHomes has been reloaded</gold>");
        blacklistedWarning = localeConfig.getString("warnings.blacklisted", "<red>There is a <block> blocking your teleport location.</red> ");
        voidWarning = localeConfig.getString("warnings.void", "<red>Your home is currently above air.</red> ");
        fireWarning = localeConfig.getString("warnings.fire", "<red>Your home is currently on fire</red> ");
        blocksWarning = localeConfig.getString("warnings.blocks", "<red>Your home is currently encased in blocks</red> ");
        lavaWarning = localeConfig.getString("warnings.lava", "<red>Your home is currently inside of lava</red> ");
        waterWarning = localeConfig.getString("warnings.water", "<red>Your home is currently under water</red> ");
        notSolidWarning = localeConfig.getString("warnings.not-solid", "<red>Your home is currently not on a solid block</red> ");
        
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
                Placeholder.unparsed("name", home.name()));
        Component worldComponent = miniMessage.deserialize(insertWorld,
                Placeholder.unparsed("world", home.location().getWorld().getName()));
        Component xComponent = miniMessage.deserialize(insertXLoc,
                Placeholder.unparsed("x-loc", String.valueOf(home.location().getX())));
        Component yComponent = miniMessage.deserialize(insertYLoc,
                Placeholder.unparsed("y-loc", String.valueOf(home.location().getY())));
        Component zComponent = miniMessage.deserialize(insertZLoc,
                Placeholder.unparsed("z-loc", String.valueOf(home.location().getZ())));
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

    public String getBlacklistedWarning() {
        return blacklistedWarning;
    }

    public String getVoidWarning() {
        return voidWarning;
    }

    public String getFireWarning() {
        return fireWarning;
    }

    public String getBlocksWarning() {
        return blocksWarning;
    }

    public String getLavaWarning() {
        return lavaWarning;
    }

    public String getWaterWarning() {
        return waterWarning;
    }

    public String getInsertOverride() {
        return insertOverride;
    }

    public String getNotSolidWarning() {
        return notSolidWarning;
    }
}
