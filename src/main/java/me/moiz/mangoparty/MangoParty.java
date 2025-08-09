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
import me.moiz.mangoparty.listeners.ArenaBoundsListener;
import me.moiz.mangoparty.managers.ArenaManager;
import me.moiz.mangoparty.managers.KitManager;
import me.moiz.mangoparty.managers.MatchManager;
import me.moiz.mangoparty.managers.PartyManager;
import me.moiz.mangoparty.managers.ScoreboardManager;
import me.moiz.mangoparty.models.Arena;
import me.moiz.mangoparty.models.Kit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

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
    private ArenaBoundsListener arenaBoundsListener;
    private Location spawnLocation;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Cool startup banner
        printStartupBanner();
        
        getLogger().info("§e⚡ Initializing MangoParty systems...");
        
        // Initialize configuration
        getLogger().info("§b📁 Loading configuration files...");
        configManager = new ConfigManager(this);
        configManager.loadConfigs();
        getLogger().info("§a✓ Configuration loaded successfully!");
        
        // Load spawn location
        loadSpawnLocation();
        
        // Initialize managers
        getLogger().info("§b🎮 Initializing core managers...");
        partyManager = new PartyManager();
        arenaManager = new ArenaManager(this);
        kitManager = new KitManager(this);
        matchManager = new MatchManager(this);
        guiManager = new GuiManager(this);
        getLogger().info("§a✓ Core managers initialized!");
        
        // Initialize scoreboard manager
        getLogger().info("§b📊 Setting up scoreboard system...");
        scoreboardManager = new ScoreboardManager(this);
        getLogger().info("§a✓ Scoreboard system ready!");
        
        // Initialize GUI managers
        getLogger().info("§b🖥️ Loading GUI systems...");
        arenaEditorGui = new ArenaEditorGui(this);
        kitEditorGui = new KitEditorGui(this);
        getLogger().info("§a✓ GUI systems loaded!");
        
        // Initialize listeners
        getLogger().info("§b👂 Registering event listeners...");
        spectatorListener = new SpectatorListener(this);
        arenaBoundsListener = new ArenaBoundsListener(this);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(this), this);
        getServer().getPluginManager().registerEvents(new KitRulesListener(this), this);
        getServer().getPluginManager().registerEvents(spectatorListener, this);
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(arenaBoundsListener, this);
        getLogger().info("§a✓ Event listeners registered!");
        
        // Register commands with tab completers
        getLogger().info("§b⌨️ Registering commands...");
        getCommand("party").setExecutor(new PartyCommand(this));
        getCommand("party").setTabCompleter(new PartyTabCompleter(this));

        getCommand("mango").setExecutor(new MangoCommand(this));
        getCommand("mango").setTabCompleter(new MangoTabCompleter(this));

        getCommand("spectate").setExecutor(new SpectateCommand(this));
        getCommand("spectate").setTabCompleter(new SpectateTabCompleter(this));
        getLogger().info("§a✓ Commands registered!");
        
        // Display loaded content
        displayLoadedContent();
        
        // Final startup message
        getLogger().info("");
        getLogger().info("§a🎉 MangoParty has been successfully enabled!");
        getLogger().info("§e⚡ Ready for epic party battles!");
        getLogger().info("");
    }
    
    private void printStartupBanner() {
        getLogger().info("");
        getLogger().info("§6╔══════════════════════════════════════╗");
        getLogger().info("§6║              §e§lMANGO PARTY§r§6              ║");
        getLogger().info("§6║                                      ║");
        getLogger().info("§6║        §a🥭 Epic Party Battles 🥭§6        ║");
        getLogger().info("§6║                                      ║");
        getLogger().info("§6║           §bVersion: §f1.0.0§6             ║");
        getLogger().info("§6║           §bAuthor: §fMoiz§6               ║");
        getLogger().info("§6╚══════════════════════════════════════╝");
        getLogger().info("");
    }
    
    private void displayLoadedContent() {
        getLogger().info("");
        getLogger().info("§6📋 §e§lLOADED CONTENT SUMMARY§r");
        getLogger().info("§6═══════════════════════════════════");
        
        // Display loaded arenas
        Map<String, Arena> arenas = arenaManager.getArenas();
        getLogger().info("§b🏟️ Arenas: §f" + arenas.size() + " loaded");
        if (!arenas.isEmpty()) {
            for (Arena arena : arenas.values()) {
                String status = arena.isComplete() ? "§a✓ Complete" : "§c✗ Incomplete";
                getLogger().info("  §7• §e" + arena.getName() + " §7- " + status);
            }
        } else {
            getLogger().info("  §7• §cNo arenas found! Use §e/mango arena create <name> §cto create one.");
        }
        
        getLogger().info("");
        
        // Display loaded kits
        Map<String, Kit> kits = kitManager.getKits();
        getLogger().info("§b⚔️ Kits: §f" + kits.size() + " loaded");
        if (!kits.isEmpty()) {
            for (Kit kit : kits.values()) {
                String rules = getKitRulesSummary(kit);
                getLogger().info("  §7• §e" + kit.getName() + " §7- " + rules);
            }
        } else {
            getLogger().info("  §7• §cNo kits found! Use §e/mango create kit <name> §cto create one.");
        }
        
        getLogger().info("");
        
        // Display spawn status
        if (spawnLocation != null) {
            getLogger().info("§b🏠 Spawn: §a✓ Set §7(" + spawnLocation.getWorld().getName() + 
                           " " + (int)spawnLocation.getX() + ", " + (int)spawnLocation.getY() + 
                           ", " + (int)spawnLocation.getZ() + ")");
        } else {
            getLogger().info("§b🏠 Spawn: §c✗ Not set §7- Use §e/mango setspawn");
        }
        
        getLogger().info("");
        getLogger().info("§6═══════════════════════════════════");
        getLogger().info("§a🚀 All systems operational!");
    }
    
    private String getKitRulesSummary(Kit kit) {
        int activeRules = 0;
        if (!kit.getRules().isNaturalHealthRegen()) activeRules++;
        if (kit.getRules().isBlockBreak()) activeRules++;
        if (kit.getRules().isBlockPlace()) activeRules++;
        if (kit.getRules().getDamageMultiplier() > 1.0) activeRules++;
        if (kit.getRules().isInstantTnt()) activeRules++;
        
        if (activeRules == 0) {
            return "§7Default rules";
        } else {
            return "§a" + activeRules + " custom rule" + (activeRules == 1 ? "" : "s");
        }
    }
    
    @Override
    public void onDisable() {
        getLogger().info("");
        getLogger().info("§6🛑 Shutting down MangoParty...");
        
        // Clean up any ongoing matches
        if (matchManager != null) {
            getLogger().info("§e⏹️ Cleaning up active matches...");
            matchManager.cleanup();
        }
        
        // Clean up scoreboards
        if (scoreboardManager != null) {
            getLogger().info("§e📊 Cleaning up scoreboards...");
            scoreboardManager.cleanup();
        }
        
        getLogger().info("§a✓ All systems shut down cleanly!");
        getLogger().info("§6🥭 Thanks for using MangoParty! 🥭");
        getLogger().info("");
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
    
    public ArenaBoundsListener getArenaBoundsListener() {
        return arenaBoundsListener;
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
        
        getLogger().info("§a🏠 Spawn location updated to: " + location.getWorld().getName() + 
                        " " + (int)location.getX() + ", " + (int)location.getY() + ", " + (int)location.getZ());
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
