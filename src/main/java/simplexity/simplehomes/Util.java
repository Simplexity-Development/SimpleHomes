package simplexity.simplehomes;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Util {

    private static final List<String> OVERRIDE_ARGS = List.of("-override", "-o");
    private static final List<String> PLAYER_ARGS = List.of("-player", "-p");

    public static boolean homeExists(List<Home> homes, String homeName) {
        for (Home home : homes) {
            if (home.name().equalsIgnoreCase(homeName)) {
                return true;
            }
        }
        return false;
    }

    public static int maxHomesPermission(Player player){
        int maxHomes = 0;
        for (PermissionAttachmentInfo pai : player.getEffectivePermissions()) {
            String permission = pai.getPermission();
            if (!pai.getValue()) continue;
            if (permission.length() <= 12 || !permission.startsWith("homes.count.") || permission.equals("homes.count.bypass"))
                continue;
            try {
                int homeCount = Integer.parseInt(permission.substring(12));
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

}
