package simplexity.simplehomes;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import simplexity.simplehomes.commands.DelHomeCommand;
import simplexity.simplehomes.commands.SetHomeCommand;
import simplexity.simplehomes.configs.LocaleHandler;
import simplexity.simplehomes.saving.SQLiteHandler;

public final class SimpleHomes extends JavaPlugin {
    
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static SimpleHomes instance;
    
    
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        LocaleHandler.getInstance().loadLocale();
        SQLiteHandler.getInstance().init();
        this.getCommand("sethome").setExecutor(new SetHomeCommand());
        this.getCommand("delhome").setExecutor(new DelHomeCommand());
        // Plugin startup logic
        
    }
    
    public static SimpleHomes getInstance() {
        return instance;
    }
    
    public static MiniMessage getMiniMessage(){
        return miniMessage;
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
