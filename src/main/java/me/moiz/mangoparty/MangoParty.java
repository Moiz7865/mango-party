package me.moiz.mangoparty;

import me.moiz.mangoparty.commands.MangoCommand;
import me.moiz.mangoparty.commands.PartyCommand;
import me.moiz.mangoparty.commands.SpectateCommand;
import me.moiz.mangoparty.commands.MangoTabCompleter;
import me.moiz.mangoparty.commands.PartyTabCompleter;
import me.moiz.mangoparty.commands.SpectateTabCompleter;
import me.moiz.mangoparty.config.ConfigManager;
import me.moiz.mangoparty.gui.GuiManager;
import me.moiz.mangoparty.gui.ArenaEditorGui;
import me.moiz.mangoparty.gui.KitEditorGui;
import me.moiz.mangoparty.listeners.PlayerDeathListener;
import me.moiz.mangoparty.listeners.PlayerRespawnListener;
import me.moiz.mangoparty.listeners.KitRulesListener;
import me.moiz.mangoparty.listeners.SpectatorListener;
import me.moiz.mangoparty.listeners.PlayerConnectionListener;
import me.moiz.mangoparty.managers.ArenaManager;
import me.moiz.mangoparty.managers.KitManager;
import me.moiz.mangoparty.managers.MatchManager;
import me.moiz.mangoparty.managers.PartyManager;
import me.moiz.mangoparty.managers.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    private ArenaEditorGui arenaEditorGui;
    private KitEditorGui kitEditorGui;
    private SpectatorListener spectatorListener;
    private Location spawnLocation;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize configuration
        configManager = new ConfigManager(this);
        configManager.loadConfigs();
        
        // Load spawn location
        loadSpawnLocation();
        
        // Initialize managers
        partyManager = new PartyManager();
        arenaManager = new ArenaManager(this);
        kitManager = new KitManager(this);
        matchManager = new MatchManager(this);
        guiManager = new GuiManager(this);
        
        // Initialize scoreboard manager
        scoreboardManager = new ScoreboardManager(this);
        
        // Initialize GUI managers
        arenaEditorGui = new ArenaEditorGui(this);
        kitEditorGui = new KitEditorGui(this);
        
        // Initialize spectator listener
        spectatorListener = new SpectatorListener(this);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(this), this);
        getServer().getPluginManager().registerEvents(new KitRulesListener(this), this);
        getServer().getPluginManager().registerEvents(spectatorListener, this);
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        
        // Register commands with tab completers
        getCommand("party").setExecutor(new PartyCommand(this));
        getCommand("party").setTabCompleter(new PartyTabCompleter(this));

        getCommand("mango").setExecutor(new MangoCommand(this));
        getCommand("mango").setTabCompleter(new MangoTabCompleter(this));

        getCommand("spectate").setExecutor(new SpectateCommand(this));
        getCommand("spectate").setTabCompleter(new SpectateTabCompleter(this));
        
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
    
    public ArenaEditorGui getArenaEditorGui() {
        return arenaEditorGui;
    }
    
    public KitEditorGui getKitEditorGui() {
        return kitEditorGui;
    }
    
    public SpectatorListener getSpectatorListener() {
        return spectatorListener;
    }
    
    public Location getSpawnLocation() {
        return spawnLocation;
    }
    
    public void setSpawnLocation(Location location) {
        this.spawnLocation = location;
        // Save to config
        getConfig().set("spawn.world", location.getWorld().getName());
        getConfig().set("spawn.x", location.getX());
        getConfig().set("spawn.y", location.getY());
        getConfig().set("spawn.z", location.getZ());
        getConfig().set("spawn.yaw", location.getYaw());
        getConfig().set("spawn.pitch", location.getPitch());
        saveConfig();
    }
    
    private void loadSpawnLocation() {
        if (getConfig().contains("spawn")) {
            String worldName = getConfig().getString("spawn.world");
            if (worldName != null && Bukkit.getWorld(worldName) != null) {
                spawnLocation = new Location(
                    Bukkit.getWorld(worldName),
                    getConfig().getDouble("spawn.x"),
                    getConfig().getDouble("spawn.y"),
                    getConfig().getDouble("spawn.z"),
                    (float) getConfig().getDouble("spawn.yaw"),
                    (float) getConfig().getDouble("spawn.pitch")
                );
            }
        }
    }
}
