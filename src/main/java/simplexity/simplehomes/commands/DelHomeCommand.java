package simplexity.simplehomes.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simplexity.simplehomes.Home;
import simplexity.simplehomes.configs.LocaleHandler;
import simplexity.simplehomes.saving.SQLiteHandler;

import java.util.List;

public class DelHomeCommand implements TabExecutor {
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage(LocaleHandler.getInstance().getMustBePlayer());
            return false;
        }
        List<Home> playerHomes = SQLiteHandler.getInstance().getHomes(player);
        if (args.length < 1) {
            sender.sendRichMessage(LocaleHandler.getInstance().getProvideHomeName());
            return false;
        }
        String homeName = args[0];
        if (homeExists(playerHomes, homeName)) {
            SQLiteHandler.getInstance().deleteHome(player, homeName);
            player.sendMessage(LocaleHandler.getInstance().getHomeDeleted());
        } else {
            player.sendMessage(LocaleHandler.getInstance().getHomeNotFound());
        }
        return false;
    }
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return null;
    }
    
    private boolean homeExists(List<Home> homes, String homeName) {
        for (Home home : homes) {
            if (home.getName().equalsIgnoreCase(homeName)) {
                return true;
            }
        }
        return false;
    }
}
