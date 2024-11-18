package simplexity.simplehomes.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import simplexity.simplehomes.configs.ConfigHandler;

public class BedEnterListener implements Listener {
    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent enterEvent) {
        PlayerBedEnterEvent.BedEnterResult result = enterEvent.getBedEnterResult();
        if (!ConfigHandler.getInstance().getAllowedResults().contains(result)) return;
        Player player = enterEvent.getPlayer();
        if (!player.hasPermission("homes.bed")) return;
        //Todo - figure out table stuff for beds vs normal homes
    }
}
