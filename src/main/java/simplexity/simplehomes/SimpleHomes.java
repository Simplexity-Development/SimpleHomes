package simplexity.simplehomes;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import simplexity.simplehomes.commands.DeleteHome;
import simplexity.simplehomes.commands.HomeCommand;
import simplexity.simplehomes.commands.HomeList;
import simplexity.simplehomes.commands.HomesReload;
import simplexity.simplehomes.commands.ImportHomes;
import simplexity.simplehomes.commands.SetHome;
import simplexity.simplehomes.configs.ConfigHandler;
import simplexity.simplehomes.configs.LocaleHandler;
import simplexity.simplehomes.listeners.PlayerLeaveListener;
import simplexity.simplehomes.listeners.PlayerMoveListener;
import simplexity.simplehomes.saving.SQLHandler;

import java.util.Objects;

public final class SimpleHomes extends JavaPlugin {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static SimpleHomes instance;



    @Override
    public void onEnable() {
        instance = this;
        handleConfig();
        SQLHandler.getInstance().init();
        registerCommands();
        registerListeners();
    }

    public static SimpleHomes getInstance() {
        return instance;
    }

    public static MiniMessage getMiniMessage() {
        return miniMessage;
    }

    private void registerCommands() {
        Objects.requireNonNull(this.getCommand("sethome")).setExecutor(new SetHome());
        Objects.requireNonNull(this.getCommand("delhome")).setExecutor(new DeleteHome());
        Objects.requireNonNull(this.getCommand("home")).setExecutor(new HomeCommand());
        Objects.requireNonNull(this.getCommand("homelist")).setExecutor(new HomeList());
        Objects.requireNonNull(this.getCommand("homesreload")).setExecutor(new HomesReload());
        Objects.requireNonNull(this.getCommand("importhomes")).setExecutor(new ImportHomes());
    }

    private void registerListeners(){
        this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerLeaveListener(), this);
    }

    private void handleConfig(){
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        ConfigHandler.getInstance().loadConfigValues();
        LocaleHandler.getInstance().loadLocale();
    }
}
