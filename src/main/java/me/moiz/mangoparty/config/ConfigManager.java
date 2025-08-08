package me.moiz.mangoparty.config;

import me.moiz.mangoparty.MangoParty;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private MangoParty plugin;
    
    public ConfigManager(MangoParty plugin) {
        this.plugin = plugin;
    }
    
    public void loadConfigs() {
        // Create plugin data folder if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        // Create default configuration files
        createDefaultConfig();
        createDefaultGuiConfigs();
        createDefaultArenaConfig();
    }
    
    private void createDefaultConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
    }
    
    private void createDefaultGuiConfigs() {
        File guiDir = new File(plugin.getDataFolder(), "gui");
        if (!guiDir.exists()) {
            guiDir.mkdirs();
        }
        
        // Create split.yml
        File splitFile = new File(guiDir, "split.yml");
        if (!splitFile.exists()) {
            YamlConfiguration splitConfig = new YamlConfiguration();
            splitConfig.set("title", "§6Select Kit - Party Split");
            splitConfig.set("size", 27);
            
            // Example kit configurations
            splitConfig.set("kits.warrior.slot", 10);
            splitConfig.set("kits.warrior.name", "§cWarrior Kit");
            splitConfig.set("kits.warrior.kit", "warrior");
            splitConfig.set("kits.warrior.lore", new String[]{"§7A balanced melee kit", "§7with sword and armor"});
            
            splitConfig.set("kits.archer.slot", 12);
            splitConfig.set("kits.archer.name", "§aArcher Kit");
            splitConfig.set("kits.archer.kit", "archer");
            splitConfig.set("kits.archer.lore", new String[]{"§7Ranged combat kit", "§7with bow and arrows"});
            
            splitConfig.set("kits.mage.slot", 14);
            splitConfig.set("kits.mage.name", "§9Mage Kit");
            splitConfig.set("kits.mage.kit", "mage");
            splitConfig.set("kits.mage.lore", new String[]{"§7Magical kit with", "§7potions and enchanted items"});
            
            try {
                splitConfig.save(splitFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create split.yml: " + e.getMessage());
            }
        }
        
        // Create ffa.yml
        File ffaFile = new File(guiDir, "ffa.yml");
        if (!ffaFile.exists()) {
            YamlConfiguration ffaConfig = new YamlConfiguration();
            ffaConfig.set("title", "§6Select Kit - Party FFA");
            ffaConfig.set("size", 27);
            
            // Example kit configurations for FFA
            ffaConfig.set("kits.berserker.slot", 10);
            ffaConfig.set("kits.berserker.name", "§4Berserker Kit");
            ffaConfig.set("kits.berserker.kit", "berserker");
            ffaConfig.set("kits.berserker.lore", new String[]{"§7High damage melee kit", "§7for aggressive players"});
            
            ffaConfig.set("kits.assassin.slot", 12);
            ffaConfig.set("kits.assassin.name", "§8Assassin Kit");
            ffaConfig.set("kits.assassin.kit", "assassin");
            ffaConfig.set("kits.assassin.lore", new String[]{"§7Stealth and speed kit", "§7for quick eliminations"});
            
            ffaConfig.set("kits.tank.slot", 14);
            ffaConfig.set("kits.tank.name", "§7Tank Kit");
            ffaConfig.set("kits.tank.kit", "tank");
            ffaConfig.set("kits.tank.lore", new String[]{"§7Heavy armor kit", "§7for defensive play"});
            
            try {
                ffaConfig.save(ffaFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create ffa.yml: " + e.getMessage());
            }
        }
    }
    
    private void createDefaultArenaConfig() {
        File arenasFile = new File(plugin.getDataFolder(), "arenas.yml");
        if (!arenasFile.exists()) {
            YamlConfiguration arenasConfig = new YamlConfiguration();
            arenasConfig.set("arenas", ""); // Empty arenas section
            
            try {
                arenasConfig.save(arenasFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create arenas.yml: " + e.getMessage());
            }
        }
    }
}
