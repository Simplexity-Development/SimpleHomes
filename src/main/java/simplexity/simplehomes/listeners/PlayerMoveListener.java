package simplexity.simplehomes.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;
import simplexity.simplehomes.commands.HomeCommand;
import simplexity.simplehomes.configs.ConfigHandler;
import simplexity.simplehomes.configs.LocaleHandler;

public class PlayerMoveListener implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent moveEvent) {
        if (!ConfigHandler.getInstance().isDelayEnabled() || !ConfigHandler.getInstance().isCancelOnMove()) return;
        Player player = moveEvent.getPlayer();
        Location originalLocation = HomeCommand.teleportRequests.get(player);
        if (originalLocation == null) return;
        if (originalLocation.distance(player.getLocation()) < ConfigHandler.getInstance().getBufferMovement()) return;
        player.sendRichMessage(LocaleHandler.getInstance().getYouMoved());
        HomeCommand.teleportRequests.remove(player);
        BukkitTask task = HomeCommand.teleportTasks.get(player);
        task.cancel();
    }
}
