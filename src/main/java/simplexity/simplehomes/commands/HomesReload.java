package simplexity.simplehomes.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import simplexity.simplehomes.configs.ConfigHandler;
import simplexity.simplehomes.configs.LocaleHandler;
import simplexity.simplehomes.saving.SQLHandler;

public class HomesReload implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        LocaleHandler.getInstance().loadLocale();
        ConfigHandler.getInstance().loadConfigValues();
        SQLHandler.getInstance().init();
        sender.sendRichMessage(LocaleHandler.getInstance().getPluginReloaded());
        return false;
    }
}
