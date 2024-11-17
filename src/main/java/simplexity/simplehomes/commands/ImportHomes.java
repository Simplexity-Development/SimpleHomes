package simplexity.simplehomes.commands;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import simplexity.simplehomes.configs.LocaleHandler;
import simplexity.simplehomes.saving.SQLHandler;

import java.io.File;
import java.util.UUID;

public class ImportHomes implements CommandExecutor {

    Long lastUsed = null;
    String[] args = null;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendRichMessage(LocaleHandler.getInstance().getOnlyConsole());
            return true;
        }

        if (args.length == 0) {
            sender.sendRichMessage(LocaleHandler.getInstance().getImportNotEnoughArgs());
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            sender.sendRichMessage(LocaleHandler.getInstance().getImportHelp());
            return true;
        }
        if (args[0].equalsIgnoreCase("confirm")) {
            if (this.lastUsed == null || this.args == null) {
                sender.sendRichMessage(LocaleHandler.getInstance().getCannotConfirm());
                return true;
            }
            if (System.currentTimeMillis() - this.lastUsed > 15000) {
                this.lastUsed = null;
                this.args = null;
                sender.sendRichMessage(LocaleHandler.getInstance().getTimedOut());
                return true;
            }
            return executeCommand();
        }


        this.lastUsed = System.currentTimeMillis();
        this.args = args;
        sender.sendRichMessage(LocaleHandler.getInstance().getUnsupportedDestructive(),
                Placeholder.parsed("command", label));
        return true;
    }

    public boolean executeCommand() {
        String[] args = this.args;
        this.lastUsed = null;
        this.args = null;
        CommandSender sender = Bukkit.getServer().getConsoleSender();

        String playerName = args.length > 1 ? args[1] : null;

        if (args[0].equalsIgnoreCase("essentials")) {
            importEssentialsHomes(playerName);
        } else {
            sender.sendRichMessage(LocaleHandler.getInstance().getNoValidPlugin());
        }
        return true;
    }

    private void importEssentialsHomes(String playerName) {
        CommandSender sender = Bukkit.getServer().getConsoleSender();
        File userdataFolder = new File(Bukkit.getServer().getPluginsFolder() + "/Essentials/userdata");
        if (!userdataFolder.isDirectory()) {
            sender.sendRichMessage(LocaleHandler.getInstance().getEssentialsNotExist());
            return;
        }

        if (playerName != null) {
            OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerName);
            UUID uuid = player.getUniqueId();
            File playerDataFile = new File(userdataFolder, uuid + ".yml");
            if (!playerDataFile.exists()) {
                sender.sendRichMessage(LocaleHandler.getInstance().getPlayerNotExist(),
                        Placeholder.parsed("name", playerName),
                        Placeholder.parsed("uuid", uuid.toString()),
                        Placeholder.parsed("file", playerDataFile.getAbsolutePath()));
                return;
            }
            YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);
            ConfigurationSection homes = playerData.getConfigurationSection("homes");
            if (homes == null || homes.getKeys(false).isEmpty()) {
                sender.sendRichMessage(LocaleHandler.getInstance().getPlayerNotExist(),
                        Placeholder.parsed("name", playerName),
                        Placeholder.parsed("uuid", uuid.toString()),
                        Placeholder.parsed("file", playerDataFile.getAbsolutePath()));
                return;
            }
            for (String key : homes.getKeys(false)) {
                ConfigurationSection home = homes.getConfigurationSection(key);
                Location location = getLocationFromHome(home);
                if (location == null) continue;
                SQLHandler.getInstance().setHome(uuid, location, key.toLowerCase());
            }
            sender.sendRichMessage(LocaleHandler.getInstance().getImportedHomes(),
                    Placeholder.parsed("name", playerName));
            return;
        }

        File[] files = userdataFolder.listFiles();
        assert files != null; // Already did an isDirectory() check earlier.
        if (files.length == 0) {
            sender.sendRichMessage(LocaleHandler.getInstance().getNothingInsideFolder());
            return;
        }
        for (File userFile : files) {
            YamlConfiguration playerData = YamlConfiguration.loadConfiguration(userFile);
            String uuidString = userFile.getName().replace(".yml", "");
            UUID uuid = UUID.fromString(uuidString);
            ConfigurationSection homes = playerData.getConfigurationSection("homes");
            if (homes == null || homes.getKeys(false).isEmpty()) continue;
            for (String key : homes.getKeys(false)) {
                ConfigurationSection home = homes.getConfigurationSection(key);
                Location location = getLocationFromHome(home);
                if (location == null) continue;
                SQLHandler.getInstance().setHome(uuid, location, key);
            }
            OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(uuid);
            String userName = player.getName();
            if (userName == null) userName = "";
            sender.sendRichMessage(LocaleHandler.getInstance().getImportedHomes(),
                    Placeholder.parsed("name", userName));
        }
        sender.sendRichMessage(LocaleHandler.getInstance().getImportComplete());
    }

    private Location getLocationFromHome(ConfigurationSection home) {
        String worldString;
        try {
            worldString = home.getString("world");
        } catch (NullPointerException e) {
            return null;
        }
        if (worldString == null || worldString.isEmpty()) {
            return null;
        }
        UUID worldUUID = UUID.fromString(worldString);
        double x = home.getDouble("x");
        double y = home.getDouble("y");
        double z = home.getDouble("z");
        float yaw = (float) home.getDouble("yaw");
        float pitch = (float) home.getDouble("pitch");
        return new Location(Bukkit.getServer().getWorld(worldUUID), x, y, z, yaw, pitch);
    }
}
