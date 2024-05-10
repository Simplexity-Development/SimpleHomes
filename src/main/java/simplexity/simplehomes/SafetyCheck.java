package simplexity.simplehomes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.*;
import simplexity.simplehomes.configs.ConfigHandler;

import java.util.List;

public class SafetyCheck {

    public static boolean teleportingUnderWater(Location location) {
        Location locationAbove = location.clone().add(0, 1, 0);
        return locationAbove.getBlock().getType() == Material.WATER;
    }

    public static boolean teleportingIntoLava(Location location) {
        Location locationAbove = location.clone().add(0, 1, 0);
        if (location.getBlock().getType() == Material.LAVA) {
            return true;
        }
        return locationAbove.getBlock().getType() == Material.LAVA;
    }

    public static boolean teleportingIntoVoid(Location location) {
        Location locationBelow = location.clone().add(0, -1, 0);
        if (locationBelow.getBlock().isEmpty()) return true;
        if (locationBelow.getBlock().getType().isEmpty()) return true;
        return locationBelow.getBlock().getType().equals(Material.AIR);
    }

    public static boolean teleportingIntoFire(Location location) {
        Location locationBelow = location.clone().add(0, -1, 0);
        if (location.getBlock().getBlockData() instanceof Fire) {
            return true;
        }
        if (locationBelow.getBlock().getBlockData() instanceof Campfire campfire) {
            return campfire.isLit();
        }
        return false;
    }

    public static boolean teleportingIntoSolidBlocks(Location location) {
        Location locationAbove = location.clone().add(0, 1, 0);
        if (locationAbove.getBlock().isSolid()) return true;
        return location.getBlock().isSolid();
    }

    public static boolean teleportingIntoBlacklistedBlocks(Location location, List<Material> materialList) {
        Location locationAbove = location.clone().add(0, 1, 0);
        if (materialList.contains(locationAbove.getBlock().getType())) return true;
        return materialList.contains(location.getBlock().getType());
    }

    public static boolean teleportingOntoFullBlock(Location location) {
        Block block = location.getBlock();
        BlockData blockData = block.getBlockData();
        if (blockData instanceof TrapDoor) return false;
        if (blockData instanceof Slab) return false;
        if (blockData instanceof Stairs) return false;
        if (blockData instanceof Wall) return false;
        if (blockData instanceof Fence) return false;
        if (blockData instanceof Gate) return false;
        if (blockData instanceof Chest) return false;
        if (blockData instanceof Bed) return false;
        if (blockData instanceof Campfire) return false;
        if (blockData instanceof Lantern) return false;
        if (ConfigHandler.getNonFullBlocks().contains(block.getType())) return false;
        return true;
    }

    public static boolean teleportingOntoSolidBlock(Location location) {
        Location locationBelow = location.clone().add(0, -1, 0);
        Block block = locationBelow.getBlock();
        return block.isSolid();
    }

}
