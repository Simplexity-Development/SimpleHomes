package simplexity.simplehomes.commands;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simplexity.simplehomes.Home;
import simplexity.simplehomes.SafetyCheck;
import simplexity.simplehomes.SafetyFlags;
import simplexity.simplehomes.SimpleHomes;
import simplexity.simplehomes.configs.ConfigHandler;
import simplexity.simplehomes.configs.LocaleHandler;
import simplexity.simplehomes.saving.Cache;
import simplexity.simplehomes.saving.SQLHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeCommand implements TabExecutor {

    private static final String SAFETY_BYPASS = "homes.safety.bypass";
    private static final String DELAY_BYPASS = "homes.delay.bypass";
    public static HashMap<Player, Location> teleportRequests = new HashMap<>();
    public static HashMap<Player, BukkitTask> teleportTasks = new HashMap<>();


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage(LocaleHandler.getInstance().getMustBePlayer());
            return false;
        }
        List<Home> playerHomesList = Cache.getInstance().getPlayerHomes(player.getUniqueId());
        //Check for lockout
        if (CommandUtils.isLockedOut(player)) {
            player.sendRichMessage(LocaleHandler.getInstance().getCannotUseCommand(),
                    Placeholder.parsed("value", String.valueOf(CommandUtils.maxHomesPermission(player))),
                    Placeholder.parsed("command", "/home"));
            return false;
        }
        // Get the home to teleport the player to
        Home playerHome;
        if (args.length == 0) {
            playerHome = handleNoArgs(player, playerHomesList);
            if (playerHome == null) {
                player.sendRichMessage(LocaleHandler.getInstance().getListNoHomes());
                return false;
            }
        } else {
            playerHome = handleHomeSelection(player, playerHomesList, args[0]);
            if (playerHome == null) {
                player.sendRichMessage(LocaleHandler.getInstance().getHomeNotFound(),
                        Placeholder.parsed("name", args[0]));
                return false;
            }
        }
        // Check that it's safe
        if (!shouldTeleport(player, args, playerHome)) return false;
        handleTeleport(player, playerHome);
        return true;
    }

    // If player has a bed home and supplied no args, return a new home from that location and the configured bed home name.
    // Otherwise, if they only have one home, return that one.
    // Otherwise, return null

    private Home handleNoArgs(Player player, List<Home> homesList) {
        Location bedHome = getBedLocation(player);
        if (bedHome != null) return new Home(ConfigHandler.getInstance().getBedHomesName(), bedHome);
        if (homesList.size() == 1) {
            return homesList.get(0);
        }
        return null;
    }

    private Home handleHomeSelection(Player player, List<Home> homesList, String suppliedName) {
        Location bedLocation = getBedLocation(player);
        if (suppliedName.equalsIgnoreCase(ConfigHandler.getInstance().getBedHomesName()) && bedLocation != null) {
            return new Home(ConfigHandler.getInstance().getBedHomesName(), bedLocation);
        }
        return CommandUtils.getHomeFromList(homesList, suppliedName);
    }

    // Do config, permission, and API checks for bed location
    private Location getBedLocation(Player player) {
        if (player.getPotentialBedLocation() == null) return null;
        if (!ConfigHandler.getInstance().areBedHomesEnabled()) return null;
        if (!player.hasPermission(CommandUtils.BED_PERMISSION)) return null;
        return player.getPotentialBedLocation();
    }

    // Safety Check
    private boolean shouldTeleport(Player player, String[] args, Home home) {
        if (player.hasPermission(SAFETY_BYPASS)) return true;
        if (CommandUtils.shouldOverride(args)) return true;
        int safetyFlags = SafetyCheck.checkSafetyFlags(home.location(), ConfigHandler.getInstance().getBlacklistedBlocks());
        if (safetyFlags == 0) return true;
        String safetyWarning = getSafetyWarning(safetyFlags);
        if (safetyWarning == null) {
            player.sendRichMessage(LocaleHandler.getInstance().getErrorHasOccurred());
            return false;
        }
        player.sendRichMessage(safetyWarning);
        return false;
    }

    // Gets the configured messages for the safety warnings
    private String getSafetyWarning(int safetyFlags) {
        String warning = "";
        if (safetyFlags == 0) return null;
        if (SafetyFlags.DAMAGE_RISK.matches(safetyFlags)) {
            warning = LocaleHandler.getInstance().getBlacklistedWarning();
        }
        if (SafetyFlags.FALLING.matches(safetyFlags)) {
            warning = LocaleHandler.getInstance().getVoidWarning();
        }
        if (SafetyFlags.FIRE.matches(safetyFlags)) {
            warning = LocaleHandler.getInstance().getFireWarning();
        }
        if (SafetyFlags.LAVA.matches(safetyFlags)) {
            warning = LocaleHandler.getInstance().getLavaWarning();
        }
        if (SafetyFlags.NOT_SOLID.matches(safetyFlags)) {
            warning = LocaleHandler.getInstance().getVoidWarning();
        }
        if (SafetyFlags.SUFFOCATION.matches(safetyFlags)) {
            warning = LocaleHandler.getInstance().getBlocksWarning();
        }
        if (SafetyFlags.UNDERWATER.matches(safetyFlags)) {
            warning = LocaleHandler.getInstance().getWaterWarning();
        }
        if (SafetyFlags.UNSTABLE.matches(safetyFlags)) {
            warning = LocaleHandler.getInstance().getVoidWarning();
        }
        warning = warning + LocaleHandler.getInstance().getInsertOverride();
        return warning;
    }

    private void handleTeleport(Player player, Home home) {
        if (!ConfigHandler.getInstance().isDelayEnabled()) {
            normalTeleport(player, home);
            return;
        }
        if (player.hasPermission(DELAY_BYPASS)) {
            normalTeleport(player, home);
            return;
        }
        delayTeleport(player, home);
    }

    private void normalTeleport(Player player, Home home) {
        player.teleportAsync(home.location());
        player.sendRichMessage(LocaleHandler.getInstance().getHomeTeleported(),
                Placeholder.parsed("name", home.name()));
    }

    // Runnable that allows delaying the teleport, tied into the player move listener
    private void delayTeleport(Player player, Home home) {
        player.sendRichMessage(LocaleHandler.getInstance().getPleaseWait(),
                Placeholder.parsed("value", String.valueOf(ConfigHandler.getInstance().getTimeInSeconds())));
        teleportRequests.put(player, player.getLocation());
        BukkitTask teleportTask = Bukkit.getScheduler().runTaskLater(SimpleHomes.getInstance(), () -> {
            if (!teleportRequests.containsKey(player)) return;
            player.teleportAsync(home.location());
            teleportRequests.remove(player);
            player.sendRichMessage(LocaleHandler.getInstance().getHomeTeleported(),
                    Placeholder.parsed("name", home.name()));
        }, ConfigHandler.getInstance().getTimeInSeconds() * 20L);
        teleportTasks.put(player, teleportTask);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) return List.of();
        List<Home> homesList = SQLHandler.getInstance().getHomes(player.getUniqueId());
        List<String> stringList = new ArrayList<>();
        for (Home home : homesList) {
            stringList.add(home.name());
        }
        if (player.hasPermission(CommandUtils.BED_PERMISSION) && getBedLocation(player) != null) {
            stringList.add(ConfigHandler.getInstance().getBedHomesName());
        }
        return stringList;
    }
}