package simplexity.simplehomes.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simplexity.simplehomes.Home;
import simplexity.simplehomes.SimpleHomes;
import simplexity.simplehomes.configs.LocaleHandler;
import simplexity.simplehomes.saving.SQLiteHandler;

import java.util.ArrayList;
import java.util.List;

public class DelHomeCommand implements TabExecutor {
    MiniMessage miniMessage = SimpleHomes.getMiniMessage();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage(LocaleHandler.getInstance().getMustBePlayer());
            return false;
        }
        if (args.length < 1) {
            sender.sendRichMessage(LocaleHandler.getInstance().getProvideHomeName());
            return false;
        }
        List<Home> playerHomes = SQLiteHandler.getInstance().getHomes(player);
        String homeName = args[0].toLowerCase();
        if (homeExists(playerHomes, homeName)) {
            Home home = SQLiteHandler.getInstance().getHome(player, homeName);
            if (home == null) {
                SimpleHomes.getInstance().getLogger().severe("HOME WAS NULL, RETURNING");
                return false;
            }
            String deletedHomeName = home.getName();
            String worldName = home.getLocation().getWorld().getName();
            String xLocation = String.valueOf(home.getLocation().getX());
            String yLocation = String.valueOf(home.getLocation().getY());
            String zLocation = String.valueOf(home.getLocation().getZ());
            SQLiteHandler.getInstance().deleteHome(player, homeName);
            player.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getHomeDeleted(),
                    Placeholder.unparsed("name", deletedHomeName),
                    Placeholder.unparsed("world", worldName),
                    Placeholder.unparsed("x_loc", xLocation),
                    Placeholder.unparsed("y_loc", yLocation),
                    Placeholder.unparsed("z_loc", zLocation)));
        } else {
            player.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getHomeNotFound(),
                    Placeholder.unparsed("name", homeName)));
        }
        return false;
    }
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length < 2 && sender instanceof Player player) {
            List<String> homeList = new ArrayList<>();
            for (Home home : SQLiteHandler.getInstance().getHomes(player)) {
                homeList.add(home.getName());
            }
            return homeList;
        }
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
