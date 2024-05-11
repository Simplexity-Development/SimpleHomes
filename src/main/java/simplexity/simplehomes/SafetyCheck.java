package simplexity.simplehomes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.block.data.type.Fire;

import java.util.List;

public class SafetyCheck {

    /**
     * Checks if the block above this location is water
     * @param location the location to check
     * @return boolean
     */
    public static boolean underWater(Location location) {
        Location locationAbove = location.clone().add(0, 1, 0);
        return locationAbove.getBlock().getType() == Material.WATER;
    }

    /**
     * Checks if this block or the one above it is lava
     * @param location the location to check
     * @return boolean
     */
    public static boolean insideLava(Location location) {
        Location locationAbove = location.clone().add(0, 1, 0);
        if (location.getBlock().getType() == Material.LAVA) {
            return true;
        }
        return locationAbove.getBlock().getType() == Material.LAVA;
    }

    /**
     * Checks if the block below the provided location is air or is otherwise empty
     * @param location the location to check
     * @return boolean
     */
    public static boolean willFall(Location location) {
        Location locationBelow = location.clone().add(0, -1, 0);
        if (locationBelow.getBlock().isEmpty()) return true;
        if (locationBelow.getBlock().getType().isEmpty()) return true;
        return locationBelow.getBlock().getType().equals(Material.AIR);
    }

    /**
     * Checks if the block located at this position is a fire, or campfire
     * @param location the location to check
     * @return boolean
     */
    public static boolean insideFire(Location location) {
        BlockData blockData = location.getBlock().getBlockData();
        if (blockData instanceof Fire) {
            return true;
        }
        if (blockData instanceof Campfire campfire) {
            return campfire.isLit();
        }
        return false;
    }

    /**
     * Checks if the block located at this position is solid
     * @param location the location to check
     * @return boolean
     */
    public static boolean insideSolidBlocks(Location location) {
        Location locationAbove = location.clone().add(0, 1, 0);
        if (locationAbove.getBlock().isSolid()) return true;
        return false;
    }

    /**
     * Checks if the block located at this position, or the block above, are blacklisted
     * @param location the location to check
     * @param materialList the list of blacklisted materials
     * @return boolean
     */

    public static boolean insideBlacklistedBlocks(Location location, List<Material> materialList) {
        Location locationAbove = location.clone().add(0, 1, 0);
        if (materialList.contains(locationAbove.getBlock().getType())) return true;
        return materialList.contains(location.getBlock().getType());
    }
}
