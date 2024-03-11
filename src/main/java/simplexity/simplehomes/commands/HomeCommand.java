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

import java.util.List;

public class HomeCommand implements TabExecutor {
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
        String homeName = args[0];
        Home home = null;
        if (homeExists(playerHomes, homeName)) {
            home = SQLiteHandler.getInstance().getHome(player, homeName);
        }
        if (home == null) {
            SimpleHomes.getInstance().getLogger().severe(player.getName() + " tried to access home " + homeName
                    + " but it was null");
            player.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNullHome(),
                    Placeholder.parsed("name", homeName)));
            return false;
        }
        player.teleport(home.getLocation());
        player.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getHomeTeleported(),
                Placeholder.parsed("name", homeName)));
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
