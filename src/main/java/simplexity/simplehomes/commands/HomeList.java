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

import java.util.List;

public class HomeList implements TabExecutor {

    private final MiniMessage miniMessage = SimpleHomes.getMiniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage(LocaleHandler.getInstance().getMustBePlayer());
            return false;
        }
        List<Home> playerHomes = SQLHandler.getInstance().getHomes(player);
        if (ConfigHandler.getInstance().isLockoutEnabled() && ConfigHandler.getInstance().isDisableHomeList()) {
            int maxHomeCount = Util.maxHomesPermission(player);
            if (maxHomeCount < playerHomes.size() && !player.hasPermission("homes.count.bypass")) {
                player.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getCannotUseCommand(),
                        Placeholder.parsed("value", String.valueOf(maxHomeCount)),
                        Placeholder.parsed("command", "/homelist")));
                return false;
            }
        }
        Component messageToSend = miniMessage.deserialize(LocaleHandler.getInstance().getListHeader());
        if (playerHomes.isEmpty()) {
            messageToSend = messageToSend
                    .appendNewline()
                    .append(miniMessage.deserialize(LocaleHandler.getInstance().getListNoHomes()));
            player.sendMessage(messageToSend);
            return true;
        }
        if (args.length < 1) {
            for (Home home : playerHomes) {
                Component listComponent = LocaleHandler.getInstance().locationResolver(home, LocaleHandler.getInstance().getListItem());
                if (listComponent == null) continue;
                messageToSend = messageToSend.appendNewline().append(listComponent);
            }
            player.sendMessage(messageToSend);
            return true;
        }
        String homeName = args[0];
        Home home = SQLHandler.getInstance().getHome(player, homeName);
        Component listComponent = LocaleHandler.getInstance().locationResolver(home, LocaleHandler.getInstance().getListItem());
        if (listComponent == null) {
            player.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getHomeNotFound(),
                    Placeholder.unparsed("name", homeName)));
            return false;
        }
        messageToSend = messageToSend.appendNewline().append(listComponent);
        player.sendMessage(messageToSend);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return null;
    }
}
