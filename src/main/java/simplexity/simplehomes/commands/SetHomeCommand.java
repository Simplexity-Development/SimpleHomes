package simplexity.simplehomes.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
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

public class SetHomeCommand implements TabExecutor {
    
    MiniMessage miniMessage = SimpleHomes.getMiniMessage();
    // /sethome name
    /*
    /sethome - no args, if has no home set, will set first home, if has home, will ask for additional arguments
    /sethome <name> - check if another home has that name, if it does, return and let player know
    /sethome <name> -o - overwrites the previous home named that if one existed
    each home set checks on how many homes the person has permissions to have, returns if the person has too many
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage(LocaleHandler.getInstance().getMustBePlayer());
            return false;
        }
        List<Home> playerHomes = SQLiteHandler.getInstance().getHomes(player);
        if (args.length < 1 && !playerHomes.isEmpty()) {
            sender.sendRichMessage(LocaleHandler.getInstance().getProvideHomeName());
            return false;
        }
        Location playerLocation = player.getLocation().toCenterLocation();
        String homeName = args[0];
        boolean overwrite = false;
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("-o")) {
                overwrite = true;
            }
        }
        if (homeExists(playerHomes, homeName) && !overwrite) {
            player.sendRichMessage(LocaleHandler.getInstance().getHomeExists());
            return false;
        }
        SQLiteHandler.getInstance().setHome(player, homeName, playerLocation, overwrite);
        player.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getHomeSet(),
                Placeholder.unparsed("name", homeName)));
        return true;
    }
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return List.of("");
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
