package simplexity.simplehomes.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simplexity.simplehomes.SafetyCheck;
import simplexity.simplehomes.SimpleHomes;
import simplexity.simplehomes.Util;
import simplexity.simplehomes.configs.ConfigHandler;
import simplexity.simplehomes.configs.LocaleHandler;
import simplexity.simplehomes.saving.SQLiteHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Home implements TabExecutor {
    MiniMessage miniMessage = SimpleHomes.getMiniMessage();

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
        List<simplexity.simplehomes.Home> playerHomes = SQLiteHandler.getInstance().getHomes(player);
        String homeName = args[0].toLowerCase();
        simplexity.simplehomes.Home home = null;
        if (Util.homeExists(playerHomes, homeName)) {
            home = SQLiteHandler.getInstance().getHome(player, homeName);
        }
        if (home == null) {
            player.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNullHome(), Placeholder.parsed("name", homeName)));
            return false;
        }
        Location homeLocation = home.getLocation();
        boolean bypass = false;
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("-o")) {
                bypass = true;
            }
        }
        if (!safeTeleport(homeLocation, bypass, player)) return false;
        player.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getHomeTeleported(), Placeholder.parsed("name", homeName)));
        return false;
    }

    private boolean safeTeleport(Location location, boolean bypass, Player player) {
        if (bypass) {
            player.teleport(location.toCenterLocation());
            return true;
        }
        boolean adjustTeleportLocation = false;
        String overrideString = LocaleHandler.getInstance().getInsertOverride();
        if (!SafetyCheck.teleportingOntoFullBlock(location)) {
            adjustTeleportLocation = true;
            location.add(0, 1, 0);
        }
        if (!SafetyCheck.teleportingOntoSolidBlock(location) &&
                !(playerBypass(player) || player.isFlying())) {
            player.sendRichMessage(LocaleHandler.getInstance().getNotSolidWarning() + overrideString);
            return false;
        }
        if (SafetyCheck.teleportingIntoVoid(location) &&
                !(playerBypass(player) || player.isFlying())) {
            player.sendRichMessage(LocaleHandler.getInstance().getVoidWarning() + overrideString);
            return false;
        }
        if (SafetyCheck.teleportingIntoFire(location) &&
                !playerBypass(player, List.of(PotionEffectType.FIRE_RESISTANCE))) {
            player.sendRichMessage(LocaleHandler.getInstance().getFireWarning() + overrideString);
            return false;
        }
        if (SafetyCheck.teleportingIntoSolidBlocks(location) &&
                !playerBypass(player)) {
            player.sendRichMessage(LocaleHandler.getInstance().getBlocksWarning() + overrideString);
            return false;
        }
        if (SafetyCheck.teleportingIntoLava(location) &&
                !playerBypass(player, List.of(PotionEffectType.FIRE_RESISTANCE))) {
            player.sendRichMessage(LocaleHandler.getInstance().getLavaWarning() + overrideString);
            return false;
        }
        if (SafetyCheck.teleportingUnderWater(location) &&
                !playerBypass(player, List.of(PotionEffectType.WATER_BREATHING, PotionEffectType.CONDUIT_POWER))) {
            player.sendRichMessage(LocaleHandler.getInstance().getWaterWarning() + overrideString);
            return false;
        }
        if (SafetyCheck.teleportingIntoBlacklistedBlocks(location, ConfigHandler.getBlacklistedBlocks()) &&
                !playerBypass(player)) {
            player.sendRichMessage(LocaleHandler.getInstance().getBlacklistedWarning() + overrideString, Placeholder.parsed("block", location.getBlock().getType().toString().toLowerCase(Locale.ROOT)));
            return false;
        }
        if (adjustTeleportLocation) {
            Location adjustedLocation = location.clone().add(0, 1, 0).toCenterLocation();
            player.teleport(adjustedLocation);
            return true;
        }
        player.teleport(location.toCenterLocation());
        return true;
    }

    private boolean playerBypass(Player player) {
        if (player.hasPermission("homes.bypass.safety")) return true;
        if (player.getGameMode().equals(GameMode.SPECTATOR)) return true;
        if (player.getGameMode().equals(GameMode.CREATIVE)) return true;
        if (player.isInvulnerable()) return true;
        return false;
    }

    private boolean playerBypass(Player player, List<PotionEffectType> potionEffects) {
        if (playerBypass(player)) return true;
        for (PotionEffectType potionEffect : potionEffects) {
            if (player.hasPotionEffect(potionEffect)) return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length < 2 && sender instanceof Player player) {
            List<String> homeList = new ArrayList<>();
            for (simplexity.simplehomes.Home home : SQLiteHandler.getInstance().getHomes(player)) {
                homeList.add(home.getName());
            }
            return homeList;
        }
        return null;
    }
}
