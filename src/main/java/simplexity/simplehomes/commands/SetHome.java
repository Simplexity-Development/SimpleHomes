package simplexity.simplehomes.commands;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simplehomes.Home;
import simplexity.simplehomes.configs.ConfigHandler;
import simplexity.simplehomes.configs.LocaleHandler;
import simplexity.simplehomes.saving.SQLHandler;

import java.util.List;

public class SetHome implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage(LocaleHandler.getInstance().getMustBePlayer());
            return false;
        }
        List<Home> playerHomes = SQLHandler.getInstance().getHomes(player.getUniqueId());

        if (args.length == 0) {
            handleNoArgs(player, playerHomes);
            return true;
        }
        String homeName = getHomeName(args);
        boolean shouldOverride = CommandUtils.shouldOverride(args);
        if (CommandUtils.getHomeFromList(playerHomes, homeName) != null && !shouldOverride) {
            player.sendRichMessage(LocaleHandler.getInstance().getHomeExists());
            return false;
        }
        handleSetHome(player, homeName);
        return true;
    }

    private void handleNoArgs(Player player, List<Home> homesList) {
        if (!homesList.isEmpty()) {
            player.sendRichMessage(LocaleHandler.getInstance().getProvideHomeName());
            return;
        }
        handleSetHome(player, ConfigHandler.getInstance().getDefaultHomeName());
    }

    private String getHomeName(String[] args) {
        List<String> sanitizedArgs = CommandUtils.getSanitizedArgsList(args);
        if (sanitizedArgs.isEmpty()) {
            return null;
        }
        return sanitizedArgs.get(0);
    }

    private void handleSetHome(Player player, String homeName) {
        SQLHandler.getInstance().setHome(player.getUniqueId(), player.getLocation(), homeName);
        player.sendRichMessage(LocaleHandler.getInstance().getHomeSet(), Placeholder.unparsed("name", homeName));
    }
}
