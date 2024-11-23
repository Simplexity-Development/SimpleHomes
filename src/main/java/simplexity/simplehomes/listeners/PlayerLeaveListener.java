package simplexity.simplehomes.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import simplexity.simplehomes.saving.SQLHandler;

import java.util.UUID;

public class PlayerLeaveListener implements Listener {
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        SQLHandler.getInstance().removePlayerFromCache(uuid);
    }
}
