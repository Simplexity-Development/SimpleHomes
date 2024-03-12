package simplexity.simplehomes.configs;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import simplexity.simplehomes.SimpleHomes;

import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {
    
    private static final ArrayList<Material> waterDangers = new ArrayList<>();
    private static final ArrayList<Material> fireDangers = new ArrayList<>();
    private static final ArrayList<Material> earthDangers = new ArrayList<>();
    private static final ArrayList<Material> airDangers = new ArrayList<>();
    
    public static void loadConfigValues() {
        SimpleHomes.getInstance().reloadConfig();
        FileConfiguration config = SimpleHomes.getInstance().getConfig();
        List<String> waterList = config.getStringList("dangerous-blocks.water");
        List<String> fireList = config.getStringList("dangerous-blocks.fire");
        List<String> earthList = config.getStringList("dangerous-blocks.earth");
        List<String> airList = config.getStringList("dangerous-blocks.air");
        fillList(waterList, waterDangers);
        fillList(fireList, fireDangers);
        fillList(earthList, earthDangers);
        fillList(airList, airDangers);
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
    
    
    public static ArrayList<Material> getWaterDangers() {
        return waterDangers;
    }
    
    public static ArrayList<Material> getFireDangers() {
        return fireDangers;
    }
    
    public static ArrayList<Material> getEarthDangers() {
        return earthDangers;
    }
    
    public static ArrayList<Material> getAirDangers() {
        return airDangers;
    }
}
