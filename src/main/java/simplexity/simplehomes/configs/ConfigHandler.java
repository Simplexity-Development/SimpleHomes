package simplexity.simplehomes.configs;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import simplexity.simplehomes.SimpleHomes;

import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {

    private static ConfigHandler instance;

    private final ArrayList<Material> blacklistedBlocks = new ArrayList<>();
    private boolean creativeBypass, invulnerableBypass, mysql;
    private String ip, name, username, password;

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
        mysql = config.getBoolean("mysql.enabled");
        ip = config.getString("mysql.ip");
        name = config.getString("mysql.name");
        username = config.getString("mysql.username");
        password = config.getString("mysql.password");
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

    public String getIp() {
        return ip;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isUsingMysql() {
        return mysql;
    }
}
