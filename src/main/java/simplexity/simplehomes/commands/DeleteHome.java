package simplexity.simplehomes.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simplexity.simplehomes.Home;
import simplexity.simplehomes.configs.LocaleHandler;
import simplexity.simplehomes.saving.SQLHandler;

import java.util.ArrayList;
import java.util.List;


public class DeleteHome implements TabExecutor {

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
        List<Home> playerHomesList = SQLHandler.getInstance().getHomes(player.getUniqueId());
        if (CommandUtils.isLockedOut(player)) {
            player.sendRichMessage(LocaleHandler.getInstance().getCannotUseCommand(),
                    Placeholder.parsed("value", String.valueOf(CommandUtils.maxHomesPermission(player))),
                    Placeholder.parsed("command", "/delhome"));
            return false;
        }
        String homeName = args[0].toLowerCase();
        Home homeRequested = CommandUtils.getHomeFromList(playerHomesList, homeName);
        if (!shouldDelete(homeRequested, player, homeName)) return false;
        //Need to parse the message before deleting the home otherwise there's no home to send into the method. At least that's what happened originally.
        //Probably had to do with how I was originally putting it in but whatever, I'm doing it here now.
        Component parsedHomeDeleteMessage = LocaleHandler.getInstance().locationResolver(homeRequested,
                LocaleHandler.getInstance().getHomeDeleted());
        SQLHandler.getInstance().deleteHome(player.getUniqueId(), homeName);
        player.sendMessage(parsedHomeDeleteMessage);
        return true;

    }



    //Handle logic to get the home requested in the arguments
    private boolean shouldDelete(Home homeRequested, Player player, String homeName) {
        if (homeRequested == null) {
            player.sendRichMessage(LocaleHandler.getInstance().getHomeNotFound(),
                    Placeholder.unparsed("name", homeName));
            return false;
        }
        return true;
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
