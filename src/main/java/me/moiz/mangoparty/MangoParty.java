package me.moiz.mangoparty;

import me.moiz.mangoparty.commands.MangoCommand;
import me.moiz.mangoparty.commands.PartyCommand;
import me.moiz.mangoparty.config.ConfigManager;
import me.moiz.mangoparty.gui.GuiManager;
import me.moiz.mangoparty.listeners.PlayerDeathListener;
import me.moiz.mangoparty.listeners.PlayerRespawnListener;
import me.moiz.mangoparty.managers.ArenaManager;
import me.moiz.mangoparty.managers.KitManager;
import me.moiz.mangoparty.managers.MatchManager;
import me.moiz.mangoparty.managers.PartyManager;
import me.moiz.mangoparty.managers.ScoreboardManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MangoParty extends JavaPlugin {
    
    private static MangoParty instance;
    private PartyManager partyManager;
    private ArenaManager arenaManager;
    private KitManager kitManager;
    private MatchManager matchManager;
    private GuiManager guiManager;
    private ConfigManager configManager;
    private ScoreboardManager scoreboardManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize configuration
        configManager = new ConfigManager(this);
        configManager.loadConfigs();
        
        // Initialize managers
        partyManager = new PartyManager();
        arenaManager = new ArenaManager(this);
        kitManager = new KitManager(this);
        matchManager = new MatchManager(this);
        guiManager = new GuiManager(this);
        
        // Initialize scoreboard manager
        scoreboardManager = new ScoreboardManager(this);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(this), this);
        
        // Register commands
        getCommand("party").setExecutor(new PartyCommand(this));
        getCommand("mango").setExecutor(new MangoCommand(this));
        
        getLogger().info("MangoParty has been enabled!");
    }
    
    @Override
    public void onDisable() {
        // Clean up any ongoing matches
        if (matchManager != null) {
            matchManager.cleanup();
        }
        
        // Clean up scoreboards
        if (scoreboardManager != null) {
            scoreboardManager.cleanup();
        }
        
        getLogger().info("MangoParty has been disabled!");
    }
    
    public static MangoParty getInstance() {
        return instance;
    }
    
    public PartyManager getPartyManager() {
        return partyManager;
    }
    
    public ArenaManager getArenaManager() {
        return arenaManager;
    }
    
    public KitManager getKitManager() {
        return kitManager;
    }
    
    public MatchManager getMatchManager() {
        return matchManager;
    }
    
    public GuiManager getGuiManager() {
        return guiManager;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }
}
