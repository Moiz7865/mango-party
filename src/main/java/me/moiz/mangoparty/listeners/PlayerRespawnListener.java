package me.moiz.mangoparty.listeners;

import me.moiz.mangoparty.MangoParty;
import me.moiz.mangoparty.models.Match;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {
    private MangoParty plugin;
    
    public PlayerRespawnListener(MangoParty plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Match match = plugin.getMatchManager().getPlayerMatch(player);
        
        if (match == null) {
            return; // Player not in a match
        }
        
        // Cancel default respawn behavior for match players
        event.setCancelled(true);
        
        // If player is eliminated, keep them at their death location
        if (match.isPlayerSpectator(player.getUniqueId())) {
            // Don't change respawn location - let them stay where they died
            // The spectator setup will be handled by PlayerDeathListener
        }
    }
}
