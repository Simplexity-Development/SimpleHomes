package simplexity.simplehomes.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simplexity.simplehomes.SafetyCheck;
import simplexity.simplehomes.SimpleHomes;
import simplexity.simplehomes.Util;
import simplexity.simplehomes.configs.ConfigHandler;
import simplexity.simplehomes.configs.LocaleHandler;
import simplexity.simplehomes.saving.SQLHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Home implements TabExecutor {
    MiniMessage miniMessage = SimpleHomes.getMiniMessage();
    public static HashMap<Player, Location> teleportRequests = new HashMap<>();
    public static HashMap<Player, BukkitTask> teleportTasks = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage(LocaleHandler.getInstance().getMustBePlayer());
            return false;
        }
        if (args.length < 1) {
            sender.sendRichMessage(LocaleHandler.getInstance().getProvideHomeName());
            return false;
        }
        List<simplexity.simplehomes.Home> playerHomes = SQLHandler.getInstance().getHomes(player.getUniqueId());
        if (ConfigHandler.getInstance().isLockoutEnabled() && ConfigHandler.getInstance().isDisableHome()) {
            int maxHomeCount = Util.maxHomesPermission(player);
            if (maxHomeCount < playerHomes.size() && !player.hasPermission("homes.count.bypass")) {
                player.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getCannotUseCommand(),
                        Placeholder.parsed("value", String.valueOf(maxHomeCount)),
                        Placeholder.parsed("command", "/home")));
                return false;
            }
        }
        String homeName = args[0].toLowerCase();
        simplexity.simplehomes.Home home = null;
        if (Util.homeExists(playerHomes, homeName)) {
            home = SQLHandler.getInstance().getHome(player.getUniqueId(), homeName);
        }
        if (home == null) {
            player.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNullHome(), Placeholder.parsed("name", homeName)));
            return false;
        }
        Location homeLocation = home.location();
        boolean bypass = false;
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("-o")) {
                bypass = true;
            }
        }
        if (!safeTeleport(homeLocation, bypass, player)) return false;
        delayTeleport(homeLocation, player, homeName);
        return false;
    }

    private boolean safeTeleport(Location location, boolean bypass, Player player) {
        if (bypassSafetyChecks(player, bypass)) {
            return true;
        }
        if (SafetyCheck.willFall(location) && !player.isFlying()) {
            player.sendRichMessage(LocaleHandler.getInstance().getVoidWarning() + LocaleHandler.getInstance().getInsertOverride());
            return false;
        }
        if (SafetyCheck.insideFire(location) && !player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
            player.sendRichMessage(LocaleHandler.getInstance().getFireWarning() + LocaleHandler.getInstance().getInsertOverride());
            return false;
        }
        if (SafetyCheck.insideLava(location) && !player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
            player.sendRichMessage(LocaleHandler.getInstance().getLavaWarning() + LocaleHandler.getInstance().getInsertOverride());
            return false;
        }
        if (SafetyCheck.insideSolidBlocks(location)) {
            player.sendRichMessage(LocaleHandler.getInstance().getBlocksWarning() + LocaleHandler.getInstance().getInsertOverride());
            return false;
        }
        if (SafetyCheck.insideBlacklistedBlocks(location, ConfigHandler.getInstance().getBlacklistedBlocks())) {
            player.sendRichMessage(LocaleHandler.getInstance().getBlacklistedWarning() + LocaleHandler.getInstance().getInsertOverride());
            return false;
        }
        if (SafetyCheck.underWater(location)) {
            player.sendRichMessage(LocaleHandler.getInstance().getWaterWarning() + LocaleHandler.getInstance().getInsertOverride());
            return false;
        }
        return true;
    }

    private void delayTeleport(Location location, Player player, String homeName) {
        if (player.hasPermission("homes.delay.bypass")) {
            player.teleportAsync(location);
            return;
        }
        if (!ConfigHandler.getInstance().isDelayEnabled()) {
            player.teleportAsync(location);
            return;
        }
        player.sendRichMessage(LocaleHandler.getInstance().getPleaseWait(),
                Placeholder.parsed("value", String.valueOf(ConfigHandler.getInstance().getTimeInSeconds())));
        teleportRequests.put(player, player.getLocation());
        BukkitTask teleportTask = Bukkit.getScheduler().runTaskLater(SimpleHomes.getInstance(), () -> {
            if (!teleportRequests.containsKey(player)) return;
            player.teleportAsync(location);
            teleportRequests.remove(player);
            player.sendRichMessage(LocaleHandler.getInstance().getHomeTeleported(),
                    Placeholder.parsed("name", homeName));
        }, ConfigHandler.getInstance().getTimeInSeconds() * 20L);
        teleportTasks.put(player, teleportTask);
    }


    private boolean bypassSafetyChecks(Player player, boolean bypass) {
        if (bypass) return true;
        if (player.hasPermission("homes.safety.bypass")) return true;
        if (player.getGameMode().equals(GameMode.SPECTATOR)) return true;
        if (ConfigHandler.getInstance().doCreativeBypass() && player.getGameMode().equals(GameMode.CREATIVE))
            return true;
        if (ConfigHandler.getInstance().doInvulnerableBypass() && player.isInvulnerable()) return true;
        return false;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length < 2 && sender instanceof Player player) {
            List<String> homeList = new ArrayList<>();
            for (simplexity.simplehomes.Home home : SQLHandler.getInstance().getHomes(player.getUniqueId())) {
                homeList.add(home.name());
            }
            return homeList;
        }
        return null;
    }
}
