package simplexity.simplehomes.configs;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import simplexity.simplehomes.SimpleHomes;

import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {

    private static ConfigHandler instance;

    private final ArrayList<Material> blacklistedBlocks = new ArrayList<>();
    private boolean creativeBypass, invulnerableBypass;

    public static ConfigHandler getInstance() {
        if (instance == null) instance = new ConfigHandler();
        return instance;
    }

    public void loadConfigValues() {
        SimpleHomes.getInstance().reloadConfig();
        FileConfiguration config = SimpleHomes.getInstance().getConfig();
        List<String> blockList = config.getStringList("blacklisted-blocks");
        creativeBypass = config.getBoolean("safety-bypass.creative");
        invulnerableBypass = config.getBoolean("safety-bypass.invulnerable");
        fillList(blockList, blacklistedBlocks);
    }

    private void fillList(List<String> stringList, ArrayList<Material> materialList) {
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


    public ArrayList<Material> getBlacklistedBlocks() {
        return blacklistedBlocks;
    }

    public boolean doCreativeBypass() {
        return creativeBypass;
    }

    public boolean doInvulnerableBypass() {
        return invulnerableBypass;
    }
}
