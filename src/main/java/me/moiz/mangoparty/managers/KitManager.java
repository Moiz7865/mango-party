package me.moiz.mangoparty.managers;

import me.moiz.mangoparty.MangoParty;
import me.moiz.mangoparty.models.Kit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class KitManager {
    private MangoParty plugin;
    private Map<String, Kit> kits;
    private File kitsDir;
    
    public KitManager(MangoParty plugin) {
        this.plugin = plugin;
        this.kits = new HashMap<>();
        this.kitsDir = new File(plugin.getDataFolder(), "kits");
        
        if (!kitsDir.exists()) {
            kitsDir.mkdirs();
        }
        
        loadKits();
    }
    
    private void loadKits() {
        File[] kitFiles = kitsDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (kitFiles != null) {
            for (File kitFile : kitFiles) {
                String kitName = kitFile.getName().replace(".yml", "");
                Kit kit = loadKitFromFile(kitName, kitFile);
                if (kit != null) {
                    kits.put(kitName, kit);
                }
            }
        }
    }
    
    private Kit loadKitFromFile(String name, File file) {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            Kit kit = new Kit(name);
            
            kit.setDisplayName(config.getString("displayName", name));
            
            if (config.contains("contents")) {
                ItemStack[] contents = new ItemStack[36];
                for (int i = 0; i < 36; i++) {
                    if (config.contains("contents." + i)) {
                        contents[i] = config.getItemStack("contents." + i);
                    }
                }
                kit.setContents(contents);
            }
            
            if (config.contains("armor")) {
                ItemStack[] armor = new ItemStack[4];
                for (int i = 0; i < 4; i++) {
                    if (config.contains("armor." + i)) {
                        armor[i] = config.getItemStack("armor." + i);
                    }
                }
                kit.setArmor(armor);
            }
            
            if (config.contains("icon")) {
                kit.setIcon(config.getItemStack("icon"));
            }
            
            return kit;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load kit: " + name);
            return null;
        }
    }
    
    public void createKit(String name, Player player) {
        Kit kit = new Kit(name);
        kit.setDisplayName(name);
        kit.setContents(player.getInventory().getContents());
        kit.setArmor(player.getInventory().getArmorContents());
        
        // Use first item in inventory as icon, or default to sword
        ItemStack icon = null;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                icon = item.clone();
                icon.setAmount(1);
                break;
            }
        }
        if (icon == null) {
            icon = new ItemStack(org.bukkit.Material.IRON_SWORD);
        }
        kit.setIcon(icon);
        
        kits.put(name, kit);
        saveKit(kit);
    }
    
    private void saveKit(Kit kit) {
        File kitFile = new File(kitsDir, kit.getName() + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        
        config.set("displayName", kit.getDisplayName());
        
        if (kit.getContents() != null) {
            for (int i = 0; i < kit.getContents().length; i++) {
                if (kit.getContents()[i] != null) {
                    config.set("contents." + i, kit.getContents()[i]);
                }
            }
        }
        
        if (kit.getArmor() != null) {
            for (int i = 0; i < kit.getArmor().length; i++) {
                if (kit.getArmor()[i] != null) {
                    config.set("armor." + i, kit.getArmor()[i]);
                }
            }
        }
        
        if (kit.getIcon() != null) {
            config.set("icon", kit.getIcon());
        }
        
        try {
            config.save(kitFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save kit: " + kit.getName());
        }
    }
    
    public Kit getKit(String name) {
        return kits.get(name);
    }
    
    public Map<String, Kit> getKits() {
        return new HashMap<>(kits);
    }
    
    public void giveKit(Player player, Kit kit) {
        player.getInventory().clear();
        
        if (kit.getContents() != null) {
            player.getInventory().setContents(kit.getContents());
        }
        
        if (kit.getArmor() != null) {
            player.getInventory().setArmorContents(kit.getArmor());
        }
        
        player.updateInventory();
    }
}
