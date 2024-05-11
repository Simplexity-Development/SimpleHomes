package simplexity.simplehomes.saving;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import simplexity.simplehomes.Home;

import java.util.List;

public abstract class SaveHandler {
    
    public abstract void init();
    public abstract List<Home> getHomes(OfflinePlayer player);
    public abstract Home getHome(OfflinePlayer player, String homeName);
    public abstract boolean deleteHome(OfflinePlayer player, String homeName);
    public abstract boolean setHome(OfflinePlayer player, String homeName, Player onlinePlayer, boolean overwrite);

}
