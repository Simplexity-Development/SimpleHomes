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
import simplexity.simplehomes.configs.ConfigHandler;
import simplexity.simplehomes.configs.LocaleHandler;
import simplexity.simplehomes.saving.SQLHandler;

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
        List<Home> playerHomes = SQLHandler.getInstance().getHomes(player.getUniqueId());
        if (ConfigHandler.getInstance().isLockoutEnabled() && ConfigHandler.getInstance().isDisableDeleteHome()) {
            int maxHomeCount = Util.maxHomesPermission(player);
            if (maxHomeCount < playerHomes.size() && !player.hasPermission("homes.count.bypass")) {
                player.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getCannotUseCommand(),
                        Placeholder.parsed("value", String.valueOf(maxHomeCount)),
                        Placeholder.parsed("command", "/deletehome")));
                return false;
            }
        }
        String homeName = args[0].toLowerCase();
        if (Util.homeExists(playerHomes, homeName)) {
            Home home = SQLHandler.getInstance().getHome(player.getUniqueId(), homeName);
            if (home == null) {
                SimpleHomes.getInstance().getLogger().severe("HOME WAS NULL, RETURNING");
                return false;
            }
            Component messageToSend = LocaleHandler.getInstance().locationResolver(home, LocaleHandler.getInstance().getHomeDeleted());
            if (messageToSend == null) {
                SimpleHomes.getInstance().getLogger().warning("Unable to load 'messages.home-deleted' from locale.yml, please check your locale");
                messageToSend = miniMessage.deserialize("<yellow>Home was deleted></yellow>");
            }
            SQLHandler.getInstance().deleteHome(player.getUniqueId(), homeName);
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
            for (Home home : SQLHandler.getInstance().getHomes(player.getUniqueId())) {
                homeList.add(home.name());
            }
            return homeList;
        }
        return null;
    }


}
