package me.moiz.mangoparty.listeners;

import me.moiz.mangoparty.MangoParty;
import me.moiz.mangoparty.models.Match;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerDeathListener implements Listener {
    private MangoParty plugin;
    
    public PlayerDeathListener(MangoParty plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Match match = plugin.getMatchManager().getPlayerMatch(player);
        
        if (match == null) {
            return; // Player not in a match
        }
        
        // Override death behavior for match players
        event.setKeepInventory(true);
        event.setKeepLevel(true);
        event.getDrops().clear();
        event.setDroppedExp(0);
        
        // Store death location
        Location deathLocation = player.getLocation().clone();
        
        // Handle killer if exists
        Player killer = player.getKiller();
        if (killer != null && plugin.getMatchManager().isInMatch(killer)) {
            match.addKill(killer.getUniqueId());
        }
        
        // Eliminate player from match
        plugin.getMatchManager().eliminatePlayer(player, match);
        
        // Send elimination message
        player.sendTitle("§c§lELIMINATED", "§7You are now spectating", 10, 40, 10);
        
        // Announce elimination to all match players
        for (Player matchPlayer : match.getAllPlayers()) {
            if (!matchPlayer.equals(player)) {
                String killerName = killer != null ? killer.getName() : "unknown causes";
                matchPlayer.sendMessage("§c" + player.getName() + " §7was eliminated by §c" + killerName);
            }
        }
        
        // Schedule spectator setup after respawn
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    // Teleport to death location and set spectator mode
                    player.teleport(deathLocation);
                    player.setGameMode(GameMode.SPECTATOR);
                    player.getInventory().clear();
                    
                    // Update scoreboard
                    plugin.getScoreboardManager().updateMatchScoreboards(match);
                    
                    player.sendMessage("§7You are now spectating. Use §e/spectate <player> §7to follow other players.");
                }
            }
        }.runTaskLater(plugin, 1L); // Run after 1 tick to ensure respawn is processed
    }
}
