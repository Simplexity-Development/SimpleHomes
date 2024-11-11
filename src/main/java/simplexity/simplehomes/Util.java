package simplexity.simplehomes;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.List;

public class Util {
    
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

}
