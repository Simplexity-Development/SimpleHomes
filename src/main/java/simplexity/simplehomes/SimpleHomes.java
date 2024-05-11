package simplexity.simplehomes;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import simplexity.simplehomes.commands.DeleteHome;
import simplexity.simplehomes.commands.Home;
import simplexity.simplehomes.commands.HomeList;
import simplexity.simplehomes.commands.HomesReload;
import simplexity.simplehomes.commands.SetHome;
import simplexity.simplehomes.configs.ConfigHandler;
import simplexity.simplehomes.configs.LocaleHandler;
import simplexity.simplehomes.saving.SQLiteHandler;

public final class SimpleHomes extends JavaPlugin {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static SimpleHomes instance;


    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        ConfigHandler.getInstance().loadConfigValues();
        LocaleHandler.getInstance().loadLocale();
        SQLiteHandler.getInstance().init();
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
        this.getCommand("sethome").setExecutor(new SetHome());
        this.getCommand("delhome").setExecutor(new DeleteHome());
        this.getCommand("home").setExecutor(new Home());
        this.getCommand("homelist").setExecutor(new HomeList());
        this.getCommand("homesreload").setExecutor(new HomesReload());
    }
}
