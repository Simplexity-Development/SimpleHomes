package simplexity.simplehomes.configs;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.PlayerBedEnterEvent;
import simplexity.simplehomes.SimpleHomes;

import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {

    private static ConfigHandler instance;

    private final ArrayList<Material> blacklistedBlocks = new ArrayList<>();
    private final ArrayList<PlayerBedEnterEvent.BedEnterResult> allowedResults = new ArrayList<>();
    private boolean creativeBypass, invulnerableBypass, mysql, lockoutEnabled, disableHome, disableHomeList,
            disableDeleteHome, delayEnabled, cancelOnMove, bedHomesEnabled;
    private String ip, name, username, password;
    private int timeInSeconds;
    private double bufferMovement;

    public static ConfigHandler getInstance() {
        if (instance == null) instance = new ConfigHandler();
        return instance;
    }

    public void loadConfigValues() {
        SimpleHomes.getInstance().reloadConfig();
        FileConfiguration config = SimpleHomes.getInstance().getConfig();
        List<String> blockList = config.getStringList("blacklisted-blocks");
        List<String> resultList = config.getStringList("bed-home.allowed-results");
        creativeBypass = config.getBoolean("safety-bypass.creative", true);
        invulnerableBypass = config.getBoolean("safety-bypass.invulnerable", true);
        lockoutEnabled = config.getBoolean("lockout.enabled", false);
        disableHome = config.getBoolean("lockout.home", false);
        disableHomeList = config.getBoolean("lockout.homelist", false);
        disableDeleteHome = config.getBoolean("lockout.deletehome", false);
        delayEnabled = config.getBoolean("delay.enabled", false);
        cancelOnMove = config.getBoolean("delay.cancel-on-move", true);
        timeInSeconds = config.getInt("delay.time-in-seconds", 5);
        bufferMovement = config.getDouble("delay.buffer-movement", 0.5);
        bedHomesEnabled = config.getBoolean("bed-home.enabled", true);
        fillList(blockList);
        verifyEnterEventResults(resultList);
        mysql = config.getBoolean("mysql.enabled", false);
        ip = config.getString("mysql.ip");
        name = config.getString("mysql.name");
        username = config.getString("mysql.username");
        password = config.getString("mysql.password");
    }

    private void fillList(List<String> stringList) {
        blacklistedBlocks.clear();
        for (String string : stringList) {
            Material material = Material.matchMaterial(string);
            if (material == null) {
                SimpleHomes.getInstance().getLogger().warning(string + " is not a valid material. Please check your config");
                continue;
            }
            blacklistedBlocks.add(material);
        }
    }

    private void verifyEnterEventResults(List<String> stringList) {
        allowedResults.clear();
        if (stringList.isEmpty()) {
            SimpleHomes.getInstance().getLogger().warning(stringList + " is empty. Please check your config, setting default configuration");
            allowedResults.add(PlayerBedEnterEvent.BedEnterResult.NOT_POSSIBLE_NOW);
            allowedResults.add(PlayerBedEnterEvent.BedEnterResult.NOT_SAFE);
            allowedResults.add(PlayerBedEnterEvent.BedEnterResult.OK);
            return;
        }
        for (String string : stringList) {
            PlayerBedEnterEvent.BedEnterResult result;
            try {
                result = PlayerBedEnterEvent.BedEnterResult.valueOf(string);
            } catch (IllegalArgumentException e) {
                SimpleHomes.getInstance().getLogger().warning(string + " is not a valid event result, please check your config");
                continue;
            }
            allowedResults.add(result);
        }
    }


    public ArrayList<Material> getBlacklistedBlocks() {
        return blacklistedBlocks;
    }

    public ArrayList<PlayerBedEnterEvent.BedEnterResult> getAllowedResults() {
        return allowedResults;
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

    public boolean isLockoutEnabled() {
        return lockoutEnabled;
    }

    public boolean isDisableHome() {
        return disableHome;
    }

    public boolean isDisableHomeList() {
        return disableHomeList;
    }

    public boolean isDisableDeleteHome() {
        return disableDeleteHome;
    }

    public boolean isDelayEnabled() {
        return delayEnabled;
    }

    public boolean isCancelOnMove() {
        return cancelOnMove;
    }

    public int getTimeInSeconds() {
        return timeInSeconds;
    }

    public double getBufferMovement() {
        return bufferMovement;
    }
}
