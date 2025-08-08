package me.moiz.mangoparty.gui;

import me.moiz.mangoparty.MangoParty;
import me.moiz.mangoparty.models.Kit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import me.moiz.mangoparty.models.Arena;
import me.moiz.mangoparty.models.Party;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GuiManager implements Listener {
    private MangoParty plugin;
    private YamlConfiguration splitConfig;
    private YamlConfiguration ffaConfig;
    
    public GuiManager(MangoParty plugin) {
        this.plugin = plugin;
        loadGuiConfigs();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    private void loadGuiConfigs() {
        File guiDir = new File(plugin.getDataFolder(), "gui");
        if (!guiDir.exists()) {
            guiDir.mkdirs();
        }
        
        File splitFile = new File(guiDir, "split.yml");
        File ffaFile = new File(guiDir, "ffa.yml");
        
        if (!splitFile.exists()) {
            plugin.saveResource("gui/split.yml", false);
        }
        if (!ffaFile.exists()) {
            plugin.saveResource("gui/ffa.yml", false);
        }
        
        splitConfig = YamlConfiguration.loadConfiguration(splitFile);
        ffaConfig = YamlConfiguration.loadConfiguration(ffaFile);
    }
    
    public void openMatchTypeGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, "§6Select Match Type");
        
        // Party Split item
        ItemStack splitItem = new ItemStack(Material.IRON_SWORD);
        ItemMeta splitMeta = splitItem.getItemMeta();
        splitMeta.setDisplayName("§aParty Split");
        List<String> splitLore = new ArrayList<>();
        splitLore.add("§7Divide your party into two teams");
        splitLore.add("§7and fight against each other!");
        splitMeta.setLore(splitLore);
        splitItem.setItemMeta(splitMeta);
        gui.setItem(3, splitItem);
        
        // Party FFA item
        ItemStack ffaItem = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta ffaMeta = ffaItem.getItemMeta();
        ffaMeta.setDisplayName("§cParty FFA");
        List<String> ffaLore = new ArrayList<>();
        ffaLore.add("§7Free for all battle!");
        ffaLore.add("§7Last player standing wins!");
        ffaMeta.setLore(ffaLore);
        ffaItem.setItemMeta(ffaMeta);
        gui.setItem(5, ffaItem);
        
        player.openInventory(gui);
    }
    
    public void openKitGui(Player player, String matchType) {
        YamlConfiguration config = "split".equalsIgnoreCase(matchType) ? splitConfig : ffaConfig;
        String title = config.getString("title", "§6Select Kit");
        int size = config.getInt("size", 27);
        
        Inventory gui = Bukkit.createInventory(null, size, title);
        
        ConfigurationSection kitsSection = config.getConfigurationSection("kits");
        if (kitsSection != null) {
            Map<String, Kit> availableKits = plugin.getKitManager().getKits();
            
            for (String kitKey : kitsSection.getKeys(false)) {
                ConfigurationSection kitSection = kitsSection.getConfigurationSection(kitKey);
                if (kitSection != null) {
                    String kitName = kitSection.getString("kit");
                    Kit kit = availableKits.get(kitName);
                    
                    if (kit != null) {
                        int slot = kitSection.getInt("slot");
                        String displayName = kitSection.getString("name", kit.getDisplayName());
                        List<String> lore = kitSection.getStringList("lore");
                        
                        ItemStack item = kit.getIcon() != null ? kit.getIcon().clone() : new ItemStack(Material.IRON_SWORD);
                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName(displayName);
                        meta.setLore(lore);
                        
                        if (kitSection.contains("customModelData")) {
                            meta.setCustomModelData(kitSection.getInt("customModelData"));
                        }
                        
                        item.setItemMeta(meta);
                        gui.setItem(slot, item);
                    }
                }
            }
        }
        
        player.openInventory(gui);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        if (title.equals("§6Select Match Type")) {
            event.setCancelled(true);
            
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;
            
            if (clicked.getType() == Material.IRON_SWORD) {
                // Party Split selected
                openKitGui(player, "split");
            } else if (clicked.getType() == Material.DIAMOND_SWORD) {
                // Party FFA selected
                openKitGui(player, "ffa");
            }
        } else if (title.contains("Select Kit")) {
            event.setCancelled(true);
            
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;
            
            // Find which kit was selected
            String matchType = title.contains("Split") ? "split" : "ffa";
            YamlConfiguration config = "split".equalsIgnoreCase(matchType) ? splitConfig : ffaConfig;
            
            ConfigurationSection kitsSection = config.getConfigurationSection("kits");
            if (kitsSection != null) {
                for (String kitKey : kitsSection.getKeys(false)) {
                    ConfigurationSection kitSection = kitsSection.getConfigurationSection(kitKey);
                    if (kitSection != null && kitSection.getInt("slot") == event.getSlot()) {
                        String kitName = kitSection.getString("kit");
                        Kit kit = plugin.getKitManager().getKit(kitName);
                        
                        if (kit != null) {
                            // Start match preparation
                            startMatchPreparation(player, kit, matchType);
                            player.closeInventory();
                            return;
                        }
                    }
                }
            }
        }
    }
    
    private void startMatchPreparation(Player player, Kit kit, String matchType) {
        // Get player's party
        Party party = plugin.getPartyManager().getParty(player);
        if (party == null || !party.isLeader(player.getUniqueId())) {
            player.sendMessage("§cYou must be a party leader to start matches!");
            return;
        }
        
        // For now, use the first available arena (you could add arena selection GUI)
        Map<String, Arena> arenas = plugin.getArenaManager().getArenas();
        if (arenas.isEmpty()) {
            player.sendMessage("§cNo arenas available! Contact an administrator.");
            return;
        }
        
        Arena arena = arenas.values().iterator().next();
        if (!arena.isComplete()) {
            player.sendMessage("§cSelected arena is not complete! Contact an administrator.");
            return;
        }
        
        // Start the match
        plugin.getMatchManager().startMatch(party, arena, kit, matchType);
        player.sendMessage("§aStarting " + matchType + " match with kit: " + kit.getDisplayName());
    }
}
