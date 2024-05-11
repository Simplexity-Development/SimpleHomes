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
        Location homeLocation = home.location();
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
        if (bypassSafetyChecks(player, bypass)) {
            player.teleport(location);
            return true;
        }
        if (homeAboveAir(player, location)) {
            return false;
        }
        if (homeOnFire(player, location)) {
            return false;
        }
        if (homeInBlocks(player, location)) {
            return false;
        }
        if (homeUnderWater(player, location)) {
            return false;
        }
        player.teleport(location);
        return true;
    }


    private boolean checkPotionEffects(Player player, List<PotionEffectType> potionEffects) {
        for (PotionEffectType potionEffect : potionEffects) {
            if (player.hasPotionEffect(potionEffect)) return true;
        }
        return false;
    }

    private boolean homeAboveAir(Player player, Location location) {
        if (player.isFlying()) return false;
        if (SafetyCheck.willFall(location)) {
            player.sendRichMessage(LocaleHandler.getInstance().getVoidWarning() + LocaleHandler.getInstance().getInsertOverride());
            return true;
        }
        return false;
    }

    private boolean homeOnFire(Player player, Location location) {
        if (checkPotionEffects(player, List.of(PotionEffectType.FIRE_RESISTANCE))) return false;
        if (SafetyCheck.insideFire(location)) {
            player.sendRichMessage(LocaleHandler.getInstance().getFireWarning() + LocaleHandler.getInstance().getInsertOverride());
            return true;
        }
        if (SafetyCheck.insideLava(location)) {
            player.sendRichMessage(LocaleHandler.getInstance().getLavaWarning() + LocaleHandler.getInstance().getInsertOverride());
            return true;
        }
        return false;
    }

    private boolean homeInBlocks(Player player, Location location) {
        if (SafetyCheck.insideSolidBlocks(location)) {
            player.sendRichMessage(LocaleHandler.getInstance().getBlocksWarning() + LocaleHandler.getInstance().getInsertOverride());
            return true;
        }
        if (SafetyCheck.insideBlacklistedBlocks(location, ConfigHandler.getInstance().getBlacklistedBlocks())) {
            player.sendRichMessage(LocaleHandler.getInstance().getBlacklistedWarning() + LocaleHandler.getInstance().getInsertOverride());
            return true;
        }
        return false;
    }

    private boolean homeUnderWater(Player player, Location location) {
        if (SafetyCheck.underWater(location)) {
            player.sendRichMessage(LocaleHandler.getInstance().getWaterWarning() + LocaleHandler.getInstance().getInsertOverride());
            return true;
        }
        return false;
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
            for (simplexity.simplehomes.Home home : SQLiteHandler.getInstance().getHomes(player)) {
                homeList.add(home.name());
            }
            return homeList;
        }
        return null;
    }
}
