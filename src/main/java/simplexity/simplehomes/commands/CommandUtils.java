package simplexity.simplehomes.commands;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import simplexity.simplehomes.Home;
import simplexity.simplehomes.SimpleHomes;
import simplexity.simplehomes.configs.ConfigHandler;
import simplexity.simplehomes.saving.SQLHandler;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CommandUtils {

    private static final List<String> OVERRIDE_ARGS = List.of("-override", "-o");
    private static final List<String> PLAYER_ARGS = List.of("-player", "-p");
    public static final String COUNT_BYPASS = "homes.count.bypass";
    public static final String COUNT_BASE = "homes.count.";
    public static final String BED_PERMISSION = "homes.bed";

    public static Home getHomeFromList(List<Home> homes, String homeName) {
        for (Home home : homes) {
            if (home.name().equalsIgnoreCase(homeName)) {
                return home;
            }
        }
        return null;
    }

    public static boolean hasMoreHomesThanAllowed(Player player){
        UUID playerUUID = player.getUniqueId();
        List<Home> playerHomeList = SQLHandler.getInstance().getHomes(playerUUID);
        return playerHomeList.size() > maxHomesPermission(player);
    }

    public static int maxHomesPermission(Player player) {
        int maxHomes = 0;
        for (PermissionAttachmentInfo pai : player.getEffectivePermissions()) {
            String permission = pai.getPermission();
            if (!pai.getValue()) continue; //if the permission is set false, skip it
            if (!pai.getValue() || !permission.startsWith(COUNT_BASE))
                continue; // if the permission is set false, if it isn't ours, skip it
            permission = permission.replace(COUNT_BASE, ""); //
            try {
                int homeCount = Integer.parseInt(permission);
                if (maxHomes < homeCount) maxHomes = homeCount;
            } catch (NumberFormatException e) {
                SimpleHomes.getInstance().getLogger().warning("Found homes permission with invalid number format: " + permission);
            }
        }
        return maxHomes;
    }

    // check for override arguments anywhere in the args
    public static boolean shouldOverride(String[] args) {
        return Arrays.stream(args).anyMatch(arg -> OVERRIDE_ARGS.stream().anyMatch(arg::equalsIgnoreCase));
    }

    // This is going to hopefully be more useful the more flags are added
    public static List<String> getSanitizedArgsList(String[] args){
        List<String> argsList = Arrays.asList(args);
        argsList.removeIf(OVERRIDE_ARGS::contains);
        return argsList;
    }

    // Check if they should be locked out lol
    public static boolean isLockedOut(Player player) {
        if (player.hasPermission(CommandUtils.COUNT_BYPASS)) return false;
        if (!ConfigHandler.getInstance().isLockoutEnabled()) return false;
        if (!ConfigHandler.getInstance().isDisableDeleteHome()) return false;
        return hasMoreHomesThanAllowed(player);
    }

}
