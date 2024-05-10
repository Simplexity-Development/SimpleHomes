package simplexity.simplehomes.configs;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import simplexity.simplehomes.SimpleHomes;

import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {
    
    private static final ArrayList<Material> blacklistedBlocks = new ArrayList<>();
    private static final ArrayList<Material> nonFullBlocks = new ArrayList<>();
    public static void loadConfigValues() {
        SimpleHomes.getInstance().reloadConfig();
        FileConfiguration config = SimpleHomes.getInstance().getConfig();
        List<String> blockList = config.getStringList("blacklisted-blocks");
        List<String> nonFullBlockList = config.getStringList("non-full-blocks");
        fillList(blockList, blacklistedBlocks);
        fillList(nonFullBlockList, nonFullBlocks);
    }
    
    private static void fillList(List<String> stringList, ArrayList<Material> materialList) {
        materialList.clear();
        for (String string : stringList) {
            Material material = Material.matchMaterial(string);
            if (material == null) {
                SimpleHomes.getInstance().getLogger().warning(string + " is not a valid material. Please check your config");
                continue;
            }
            materialList.add(material);
        }
    }
    
    
    public static ArrayList<Material> getBlacklistedBlocks() {
        return blacklistedBlocks;
    }
    public static ArrayList<Material> getNonFullBlocks() {
        return nonFullBlocks;
    }

}
