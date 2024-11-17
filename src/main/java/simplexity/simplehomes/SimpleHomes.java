package simplexity.simplehomes;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import simplexity.simplehomes.commands.*;
import simplexity.simplehomes.commands.Home;
import simplexity.simplehomes.configs.ConfigHandler;
import simplexity.simplehomes.configs.LocaleHandler;
import simplexity.simplehomes.saving.SQLHandler;

import java.util.Objects;

public final class SimpleHomes extends JavaPlugin {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static SimpleHomes instance;



    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        ConfigHandler.getInstance().loadConfigValues();
        LocaleHandler.getInstance().loadLocale();
        SQLHandler.getInstance().init();
        registerCommands();
    }

    public static SimpleHomes getInstance() {
        return instance;
    }

    public static MiniMessage getMiniMessage() {
        return miniMessage;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerCommands() {
        Objects.requireNonNull(this.getCommand("sethome")).setExecutor(new SetHome());
        Objects.requireNonNull(this.getCommand("delhome")).setExecutor(new DeleteHome());
        Objects.requireNonNull(this.getCommand("home")).setExecutor(new Home());
        Objects.requireNonNull(this.getCommand("homelist")).setExecutor(new HomeList());
        Objects.requireNonNull(this.getCommand("homesreload")).setExecutor(new HomesReload());
        Objects.requireNonNull(this.getCommand("importhomes")).setExecutor(new ImportHomes());
    }
}
