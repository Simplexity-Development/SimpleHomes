package simplexity.simplehomes.saving;

import org.bukkit.Location;
import simplexity.simplehomes.Home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Cache {

    private static Cache instance;

    public Cache() {
    }

    public static Cache getInstance() {
        if (instance == null) instance = new Cache();
        return instance;
    }


    private final HashMap<UUID, List<Home>> cachedHomes = new HashMap<>();

    public List<Home> getPlayerHomes(UUID playerUuid) {
        if (cachedHomes.containsKey(playerUuid)) return cachedHomes.get(playerUuid);
        List<Home> savedHomes = SQLHandler.getInstance().getHomes(playerUuid);
        if (savedHomes == null || savedHomes.isEmpty()) savedHomes = new ArrayList<>();
        cachedHomes.put(playerUuid, savedHomes);
        return savedHomes;
    }

    public void setPlayerHome(UUID playerUuid, Location location, String name) {
        List<Home> playerHomes = getPlayerHomes(playerUuid);
        Home existingHome = getHomeFromList(playerHomes, name);
        if (existingHome != null) {
            playerHomes.remove(existingHome);
        }
        Home newHome = new Home(name, location);
        playerHomes.add(newHome);
        cachedHomes.put(playerUuid, playerHomes);
        SQLHandler.getInstance().setHome(playerUuid, location, name);
    }

    public void removeHomeByName(UUID playerUuid, String homeName) {
        List<Home> playerHomes = getPlayerHomes(playerUuid);
        Home homeToRemove = getHomeFromList(playerHomes, homeName);
        if (homeToRemove == null) return;
        SQLHandler.getInstance().deleteHome(playerUuid, homeName);
        playerHomes.remove(homeToRemove);
        cachedHomes.put(playerUuid, playerHomes);
    }

    public void removePlayerFromCache(UUID playerUuid) {
        cachedHomes.remove(playerUuid);
    }

    @SuppressWarnings("JavaExistingMethodCanBeUsed")
    private Home getHomeFromList(List<Home> homes, String homeName) {
        for (Home home : homes) {
            if (home.name().equals(homeName)) {
                return home;
            }
        }
        return null;
    }
}
