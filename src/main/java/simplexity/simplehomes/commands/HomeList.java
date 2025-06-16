package simplexity.simplehomes.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simplehomes.Home;
import simplexity.simplehomes.SimpleHomes;
import simplexity.simplehomes.configs.ConfigHandler;
import simplexity.simplehomes.configs.LocaleHandler;
import simplexity.simplehomes.saving.Cache;

import java.util.List;

public class HomeList implements CommandExecutor {

    private final MiniMessage miniMessage = SimpleHomes.getMiniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage(LocaleHandler.getInstance().getMustBePlayer());
            return false;
        }
        List<Home> playerHomes = Cache.getInstance().getPlayerHomes(player.getUniqueId());
        //Check for lockout
        if (CommandUtils.isLockedOut(player)) {
            player.sendRichMessage(LocaleHandler.getInstance().getCannotUseCommand(),
                    Placeholder.parsed("value", String.valueOf(CommandUtils.maxHomesPermission(player))),
                    Placeholder.parsed("command", "/homelist"));
            return false;
        }
        Home bedHome = null;
        Location potentialBedLocation = player.getPotentialBedLocation();
        if (potentialBedLocation != null) {
            bedHome = new Home(ConfigHandler.getInstance().getBedHomesName(), potentialBedLocation);
        }
        Component listMessage = createHomesListMessage(playerHomes, bedHome);
        player.sendMessage(listMessage);
        return true;
    }

    private Component createHomesListMessage(List<Home> homesList, Home bedHome) {
        Component messageToSend = miniMessage.deserialize(LocaleHandler.getInstance().getListHeader());
        if (homesList.isEmpty()) {
            messageToSend = messageToSend
                    .appendNewline()
                    .append(miniMessage.deserialize(LocaleHandler.getInstance().getListNoHomes()));
            return messageToSend;
        }
        for (Home home : homesList) {
            messageToSend = messageToSend
                    .appendNewline()
                    .append(LocaleHandler.getInstance().homeComponent(home, LocaleHandler.getInstance().getListItem()));
        }
        if (bedHome != null) {
            messageToSend = messageToSend
                    .appendNewline()
                    .append(LocaleHandler.getInstance().bedHomeComponent(bedHome, LocaleHandler.getInstance().getListItem()));
        }
        return messageToSend;
    }
}
