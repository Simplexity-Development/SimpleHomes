package simplexity.simplehomes.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;
import simplexity.simplehomes.commands.Home;
import simplexity.simplehomes.configs.ConfigHandler;
import simplexity.simplehomes.configs.LocaleHandler;

public class PlayerMoveListener implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent moveEvent) {
        if (!ConfigHandler.getInstance().isDelayEnabled() || !ConfigHandler.getInstance().isCancelOnMove()) return;
        Player player = moveEvent.getPlayer();
        Location originalLocation = Home.teleportRequests.get(player);
        if (originalLocation == null) return;
        if (originalLocation.distance(player.getLocation()) < ConfigHandler.getInstance().getBufferMovement()) return;
        player.sendRichMessage(LocaleHandler.getInstance().getYouMoved());
        Home.teleportRequests.remove(player);
        BukkitTask task = Home.teleportTasks.get(player);
        task.cancel();
    }
}
