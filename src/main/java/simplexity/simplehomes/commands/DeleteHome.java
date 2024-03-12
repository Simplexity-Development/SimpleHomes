package simplexity.simplehomes.commands;

import net.kyori.adventure.text.Component;
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
import simplexity.simplehomes.Util;
import simplexity.simplehomes.configs.LocaleHandler;
import simplexity.simplehomes.saving.SQLiteHandler;

import java.util.ArrayList;
import java.util.List;

public class DeleteHome implements TabExecutor {
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
        if (Util.homeExists(playerHomes, homeName)) {
            Home home = SQLiteHandler.getInstance().getHome(player, homeName);
            if (home == null) {
                SimpleHomes.getInstance().getLogger().severe("HOME WAS NULL, RETURNING");
                return false;
            }
            Component messageToSend = LocaleHandler.getInstance().locationResolver(home, LocaleHandler.getInstance().getHomeDeleted());
            if (messageToSend == null) {
                SimpleHomes.getInstance().getLogger().warning("Unable to load 'messages.home-deleted' from locale.yml, please check your locale");
                messageToSend = miniMessage.deserialize("<yellow>Home was deleted></yellow>");
            }
            SQLiteHandler.getInstance().deleteHome(player, homeName);
            player.sendMessage(messageToSend);
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
    
    
}
