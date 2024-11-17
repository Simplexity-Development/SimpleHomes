package simplexity.simplehomes.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;

public class ImportHomes implements CommandExecutor {

    Long lastUsed = null;
    String[] args = null;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendRichMessage("<red>This command can only be used by the Console.");
            return true;
        }

        if (args.length == 0) {
            sender.sendRichMessage("<red>Not enough arguments.");
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendRichMessage("<aqua>This is a Console exclusive command used to import homes to SimpleHomes from other plugins.");
                sender.sendRichMessage("<aqua>Not all home plugins are supported, to add support submit an issue on GitHub");
                sender.sendRichMessage("  <aqua>https://github.com/Simplexity-Development/SimpleHomes/issues");
                sender.sendRichMessage("<gold>This command is <red>DESTRUCTIVE</red> and will overwrite homes with the same name.");
                sender.sendRichMessage("<gold>This command is <red>UNSUPPORTED</red> and may <red>CORRUPT SAVE DATA</red>.");
                sender.sendRichMessage("<red>>>>>> USE AT YOUR OWN RISK <<<<<</red>.");
                sender.sendRichMessage("<aqua>Usage:</aqua> <yellow>/importhomes <plugin> [username]");
                sender.sendRichMessage("  <yellow>username <aqua>is an optional argument to import specifically that user's homes.");
                sender.sendRichMessage("<green>Valid Plugins: Essentials");
                return true;
            }
            if (args[0].equalsIgnoreCase("confirm")) {
                if (this.lastUsed == null || this.args == null) {
                    sender.sendRichMessage("<red>No command was executed recently to confirm.");
                    return true;
                }
                if (System.currentTimeMillis() - this.lastUsed > 15000) {
                    this.lastUsed = null;
                    this.args = null;
                    sender.sendRichMessage("<red>Command timed out, please run again.");
                    return true;
                }
                return executeCommand();
            }
        }

        this.lastUsed = System.currentTimeMillis();
        this.args = args;
        sender.sendRichMessage("<gold>You are about to run: <yellow>/" + label + " " + String.join(" ", args));
        sender.sendRichMessage("<red><bold>THIS ACTION IS DESTRUCTIVE AND WILL OVERWRITE EXISTING HOMES IN SIMPLEHOMES.");
        sender.sendRichMessage("<red><bold>THIS ACTION IS NOT SUPPORTED AND MAY CORRUPT SAVE DATA, USE AT YOUR OWN RISK.");
        sender.sendRichMessage("<gold>Please confirm this command using <yellow>/" + label + " confirm");
        return true;
    }

    public boolean executeCommand() {
        String[] args = this.args;
        this.lastUsed = null;
        this.args = null;
        CommandSender sender = Bukkit.getServer().getConsoleSender();

        String playerName = args.length > 1 ? args[1] : null;

        switch (args[0].toLowerCase()) {
            case "essentials" -> importEssentialsHomes(playerName);
            default -> sender.sendRichMessage("<red>No valid plugin was found.");
        }

        return true;
    }

    private void importEssentialsHomes(String playerName) {
        CommandSender sender = Bukkit.getServer().getConsoleSender();
        File userdataFolder = new File(Bukkit.getServer().getPluginsFolder() + "/Essentials/userdata");
        if (!userdataFolder.isDirectory()) {
            sender.sendRichMessage("<gold>There is no /plugins/Essentials/userdata folder!");
            return;
        }

        if (playerName != null) {
            OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerName);
            UUID uuid = player.getUniqueId();
            File playerDataFile = new File(userdataFolder, uuid + ".yml");
            if (!playerDataFile.exists()) {
                sender.sendRichMessage("<gold>This player does not exist or does not have an Essentials/userdata file!");
                sender.sendRichMessage("<yellow>Player Name: </yellow>" + playerName);
                sender.sendRichMessage("<yellow>Retrieved UUID: </yellow>" + uuid);
                sender.sendRichMessage("<yellow>Searched File: </yellow>" + playerDataFile);
                return;
            }
            YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);
            ConfigurationSection homes = playerData.getConfigurationSection("homes");
            if (homes == null || homes.getKeys(false).isEmpty()) {
                sender.sendRichMessage("<gold>This player does not have any homes in their Essentials/userdata file!");
                sender.sendRichMessage("<yellow>Player Name: </yellow>" + playerName);
                sender.sendRichMessage("<yellow>Retrieved UUID: </yellow>" + uuid);
                sender.sendRichMessage("<yellow>Searched File: </yellow>" + playerDataFile);
                return;
            }
            for (String key : homes.getKeys(false)) {
                // TODO: Rewrite SQLHandler to support this.
                ConfigurationSection home = homes.getConfigurationSection(key);
                sender.sendRichMessage("Found Home: " + key + " " + home.getInt("x") + " " + home.getInt("z")); // TODO: Remove after testing.
            }
            return;
        }

        File[] files = userdataFolder.listFiles();
        assert files != null; // Already did an isDirectory() check earlier.
        if (files.length == 0) {
            sender.sendRichMessage("<gold>There is nothing inside of the /plugins/Essentials/userdata folder.");
            return;
        }
        for (File userFile : files) {
            YamlConfiguration playerData = YamlConfiguration.loadConfiguration(userFile);
            sender.sendRichMessage("Found File: " + userFile.getName()); // TODO: Remove after testing.
            sender.sendRichMessage("Found Name: " + playerData.getString("last-account-name")); // TODO: Remove after testing.
            ConfigurationSection homes = playerData.getConfigurationSection("homes");
            if (homes == null || homes.getKeys(false).isEmpty()) continue;
            for (String key : homes.getKeys(false)) {
                // TODO: Rewrite SQLHandler to support this.
                ConfigurationSection home = homes.getConfigurationSection(key);
                sender.sendRichMessage("Found Home: " + key + " " + home.getInt("x") + " " + home.getInt("z")); // TODO: Remove after testing.
            }
        }
    }
}
