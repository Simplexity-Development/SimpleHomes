package simplexity.simplehomes.configs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
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
    private String mustBePlayer, provideHomeName, homeAlreadyExists, homeNotFound, nullHome, cannotSetMoreHomes,
            cannotUseCommand, errorHasOccurred, noPermission;
    private String insertName, insertWorld, insertXLoc, insertYLoc, insertZLoc, insertOverride, insertBedName;
    private String homeSet, homeDeleted, homeTeleported, pluginReloaded, listHeader, listItem, listNoHomes;
    private String blacklistedWarning, voidWarning, fireWarning, blocksWarning, lavaWarning, waterWarning;
    private String unsupportedDestructive, importHelp, importNotEnoughArgs, onlyConsole, cannotConfirm, timedOut, noValidPlugin,
            essentialsNotExist, nothingInsideFolder, playerNotExist, importComplete, importedHomes;
    private String pleaseWait, youMoved;

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
        cannotUseCommand = localeConfig.getString("errors.cannot-use-command", "<red>You currently have more than <dark_red><bold><value></bold></dark_red> homes. In order to use <gray><command></gray> you must first delete some homes.</red>");
        errorHasOccurred = localeConfig.getString("errors.error-has-occurred", "<red>An error has occurred while running this command. Please contact the server staff to let them know (-SimpleHomes Plugin)</red>");
        noPermission = localeConfig.getString("errors.no-permission", "<red>You do not have permission to use <value></red>");
        insertName = localeConfig.getString("inserts.name", "<yellow><name></yellow>");
        insertBedName = localeConfig.getString("inserts.bed-name", "<dark_gray><name></dark_gray>");
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
        unsupportedDestructive = localeConfig.getString("console.unsupported-destructive", """
                <reset>------------------------------------------------------------------------
                <gold>You are about to run: <yellow>/<command>
                
                <red><bold>THIS ACTION IS DESTRUCTIVE AND WILL OVERWRITE EXISTING HOMES IN SIMPLEHOMES.
                <red><bold>THIS ACTION IS NOT SUPPORTED AND MAY CORRUPT SAVE DATA, USE AT YOUR OWN RISK.
                
                <gold>Please confirm this command using <yellow>/<command> confirm
                <reset>-----------------------------------------------------------------------------------------""");
        importHelp = localeConfig.getString("console.import-help", """
                ------------------Import Homes Help------------------------------------
                
                <gold>This command is <red>DESTRUCTIVE</red> and will overwrite homes already set on SimpleHomes
                <gold>This command is <red>UNSUPPORTED</red> and may <red>CORRUPT SAVE DATA</red>.
                              <red>>>>>> USE AT YOUR OWN RISK <<<<<</red>.
                
                <aqua>Usage:</aqua> <yellow>/importhomes <plugin> [username]
                <yellow>[username] <aqua>is an optional argument to import specifically that user's homes.
                
                <green>Valid Plugins: Essentials
                
                <gray>Not all home plugins are supported, to add support submit an issue on GitHub - https://github.com/Simplexity-Development/SimpleHomes/issues
                <reset>-----------------------------------------------------------------------------------------""");
        importNotEnoughArgs = localeConfig.getString("console.import-not-enough-args", """
                <red>Not enough arguments.</red>
                <gray>Try <yellow>importhomes help</yellow></gray>""");
        onlyConsole = localeConfig.getString("console.only-console", "<red>This command can only be used by the Console.");
        cannotConfirm = localeConfig.getString("console.cannot-confirm", "<red>No command was executed recently to confirm.");
        timedOut = localeConfig.getString("console.timed-out", "<red>Command timed out, please run again.");
        noValidPlugin = localeConfig.getString("console.no-valid-plugin", "<red>No valid plugin was found.");
        essentialsNotExist = localeConfig.getString("console.essentials.not-exist", "<gold>There is no /plugins/Essentials/userdata folder!");
        nothingInsideFolder = localeConfig.getString("console.essentials.nothing-inside", "<gold>There is nothing inside of the /plugins/Essentials/userdata folder.");
        playerNotExist = localeConfig.getString("console.essentials.player-not-exist", """
                <gold>This player does not exist or does not have an Essentials/userdata file!");
                <yellow>Player Name: </yellow><name>
                <yellow>Retrieved UUID: </yellow><uuid>
                <yellow>Searched File: </yellow><file>""");
        importComplete = localeConfig.getString("console.import-finished", "<green>Import complete</green>");
        importedHomes = localeConfig.getString("console.saved-homes", "<yellow>Imported all homes for <name></yellow>");
        pleaseWait = localeConfig.getString("delay.please-wait", "<green>Teleporting! Please wait <value> seconds!</green>");
        youMoved = localeConfig.getString("delay.you-moved", "<gray>You moved, teleportation has been cancelled</gray>");
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

    public String getListHeader() {
        return listHeader;
    }

    public String getListItem() {
        return listItem;
    }

    public Component homeComponent(@NotNull Home home, String message) {
        Component nameComponent = miniMessage.deserialize(insertName,
                Placeholder.unparsed("name", home.name()));
        Component worldComponent = miniMessage.deserialize(insertWorld,
                Placeholder.unparsed("world", home.location().getWorld().getName()));
        Component xComponent = miniMessage.deserialize(insertXLoc,
                Placeholder.unparsed("x-loc", String.valueOf(home.location().getBlockX())));
        Component yComponent = miniMessage.deserialize(insertYLoc,
                Placeholder.unparsed("y-loc", String.valueOf(home.location().getBlockY())));
        Component zComponent = miniMessage.deserialize(insertZLoc,
                Placeholder.unparsed("z-loc", String.valueOf(home.location().getBlockZ())));
        return miniMessage.deserialize(message,
                Placeholder.component("name", nameComponent),
                Placeholder.component("world", worldComponent),
                Placeholder.component("x-loc", xComponent),
                Placeholder.component("y-loc", yComponent),
                Placeholder.component("z-loc", zComponent));
    }

    public Component bedHomeComponent(@NotNull Home home, String message) {
        Component nameComponent = miniMessage.deserialize(insertBedName,
                Placeholder.unparsed("name", ConfigHandler.getInstance().getBedHomesName()));
        Component worldComponent = miniMessage.deserialize(insertWorld,
                Placeholder.unparsed("world", home.location().getWorld().getName()));
        Component xComponent = miniMessage.deserialize(insertXLoc,
                Placeholder.unparsed("x-loc", String.valueOf(home.location().getBlockX())));
        Component yComponent = miniMessage.deserialize(insertYLoc,
                Placeholder.unparsed("y-loc", String.valueOf(home.location().getBlockY())));
        Component zComponent = miniMessage.deserialize(insertZLoc,
                Placeholder.unparsed("z-loc", String.valueOf(home.location().getBlockZ())));
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

    public String getCannotUseCommand() {
        return cannotUseCommand;
    }

    public String getErrorHasOccurred() {
        return errorHasOccurred;
    }

    public String getUnsupportedDestructive() {
        return unsupportedDestructive;
    }

    public String getImportHelp() {
        return importHelp;
    }

    public String getImportNotEnoughArgs() {
        return importNotEnoughArgs;
    }

    public String getOnlyConsole() {
        return onlyConsole;
    }

    public String getCannotConfirm() {
        return cannotConfirm;
    }

    public String getTimedOut() {
        return timedOut;
    }

    public String getNoValidPlugin() {
        return noValidPlugin;
    }


    public String getPlayerNotExist() {
        return playerNotExist;
    }

    public String getNothingInsideFolder() {
        return nothingInsideFolder;
    }

    public String getEssentialsNotExist() {
        return essentialsNotExist;
    }

    public String getImportComplete() {
        return importComplete;
    }

    public String getImportedHomes() {
        return importedHomes;
    }

    public String getPleaseWait() {
        return pleaseWait;
    }

    public String getYouMoved() {
        return youMoved;
    }

    public String getNoPermission() {
        return noPermission;
    }

    public String getInsertBedName() {
        return insertBedName;
    }
}
