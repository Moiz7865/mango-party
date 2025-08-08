package me.moiz.mangoparty.commands;

import me.moiz.mangoparty.MangoParty;
import me.moiz.mangoparty.models.Arena;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class MangoCommand implements CommandExecutor {
    private MangoParty plugin;
    
    public MangoCommand(MangoParty plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mangoparty.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }
        
        if (args[0].equalsIgnoreCase("arena")) {
            if (args.length < 2) {
                sendArenaHelp(player);
                return true;
            }
            
            handleArenaCommand(player, args);
        } else if (args[0].equalsIgnoreCase("create") && args.length > 1 && args[1].equalsIgnoreCase("kit")) {
            if (args.length < 3) {
                player.sendMessage("§cUsage: /mango create kit <name>");
                return true;
            }
            handleCreateKitCommand(player, args[2]);
        } else {
            sendHelpMessage(player);
        }
        
        return true;
    }
    
    private void sendHelpMessage(Player player) {
        player.sendMessage("§6=== MangoParty Admin Commands ===");
        player.sendMessage("§e/mango arena create <name> §7- Create a new arena");
        player.sendMessage("§e/mango arena corner1 <name> §7- Set arena corner 1");
        player.sendMessage("§e/mango arena corner2 <name> §7- Set arena corner 2");
        player.sendMessage("§e/mango arena center <name> §7- Set arena center");
        player.sendMessage("§e/mango arena spawn1 <name> §7- Set arena spawn 1");
        player.sendMessage("§e/mango arena spawn2 <name> §7- Set arena spawn 2");
        player.sendMessage("§e/mango arena save <name> §7- Save arena schematic");
        player.sendMessage("§e/mango create kit <name> §7- Create kit from inventory");
    }
    
    private void sendArenaHelp(Player player) {
        player.sendMessage("§6=== Arena Commands ===");
        player.sendMessage("§e/mango arena create <name> §7- Create a new arena");
        player.sendMessage("§e/mango arena corner1 <name> §7- Set arena corner 1");
        player.sendMessage("§e/mango arena corner2 <name> §7- Set arena corner 2");
        player.sendMessage("§e/mango arena center <name> §7- Set arena center");
        player.sendMessage("§e/mango arena spawn1 <name> §7- Set arena spawn 1");
        player.sendMessage("§e/mango arena spawn2 <name> §7- Set arena spawn 2");
        player.sendMessage("§e/mango arena save <name> §7- Save arena schematic");
        player.sendMessage("§e/mango arena list §7- List all arenas");
        player.sendMessage("§e/mango arena delete <name> §7- Delete an arena");
    }
    
    private void handleArenaCommand(Player player, String[] args) {
        String subCommand = args[1].toLowerCase();
        
        if (args.length < 3) {
            if (subCommand.equals("list")) {
                // Allow /mango arena list without arena name
            } else {
                player.sendMessage("§cPlease specify an arena name!");
                return;
            }
        }
        
        String arenaName = (args.length > 2) ? args[2] : null;
        
        switch (subCommand) {
            case "create":
                handleArenaCreate(player, arenaName);
                break;
            case "corner1":
                handleArenaCorner1(player, arenaName);
                break;
            case "corner2":
                handleArenaCorner2(player, arenaName);
                break;
            case "center":
                handleArenaCenter(player, arenaName);
                break;
            case "spawn1":
                handleArenaSpawn1(player, arenaName);
                break;
            case "spawn2":
                handleArenaSpawn2(player, arenaName);
                break;
            case "save":
                handleArenaSave(player, arenaName);
                break;
            case "list":
                handleArenaList(player);
                break;
            case "delete":
                handleArenaDelete(player, arenaName);
                break;
            default:
                sendArenaHelp(player);
                break;
        }
    }
    
    private void handleArenaCreate(Player player, String arenaName) {
        Arena existingArena = plugin.getArenaManager().getArena(arenaName);
        if (existingArena != null) {
            player.sendMessage("§cArena with that name already exists!");
            return;
        }
        
        Arena arena = plugin.getArenaManager().createArena(arenaName, player.getWorld().getName());
        player.sendMessage("§aArena '" + arenaName + "' created!");
    }
    
    private void handleArenaCorner1(Player player, String arenaName) {
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            player.sendMessage("§cArena not found! Create it first with /mango arena create " + arenaName);
            return;
        }
        
        arena.setCorner1(player.getLocation());
        plugin.getArenaManager().saveArena(arena);
        player.sendMessage("§aCorner 1 set for arena '" + arenaName + "'!");
    }
    
    private void handleArenaCorner2(Player player, String arenaName) {
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            player.sendMessage("§cArena not found! Create it first with /mango arena create " + arenaName);
            return;
        }
        
        arena.setCorner2(player.getLocation());
        plugin.getArenaManager().saveArena(arena);
        player.sendMessage("§aCorner 2 set for arena '" + arenaName + "'!");
    }
    
    private void handleArenaCenter(Player player, String arenaName) {
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            player.sendMessage("§cArena not found! Create it first with /mango arena create " + arenaName);
            return;
        }
        
        arena.setCenter(player.getLocation());
        plugin.getArenaManager().saveArena(arena);
        player.sendMessage("§aCenter set for arena '" + arenaName + "'!");
    }
    
    private void handleArenaSpawn1(Player player, String arenaName) {
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            player.sendMessage("§cArena not found! Create it first with /mango arena create " + arenaName);
            return;
        }
        
        arena.setSpawn1(player.getLocation());
        plugin.getArenaManager().saveArena(arena);
        player.sendMessage("§aSpawn 1 set for arena '" + arenaName + "'!");
    }
    
    private void handleArenaSpawn2(Player player, String arenaName) {
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            player.sendMessage("§cArena not found! Create it first with /mango arena create " + arenaName);
            return;
        }
        
        arena.setSpawn2(player.getLocation());
        plugin.getArenaManager().saveArena(arena);
        player.sendMessage("§aSpawn 2 set for arena '" + arenaName + "'!");
    }
    
    private void handleArenaSave(Player player, String arenaName) {
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            player.sendMessage("§cArena not found! Create it first with /mango arena create " + arenaName);
            return;
        }
        
        if (!arena.isComplete()) {
            player.sendMessage("§cArena is not complete! Set all corners, center, and spawns first.");
            return;
        }
        
        boolean success = plugin.getArenaManager().saveSchematic(arena);
        if (success) {
            player.sendMessage("§aSchematic saved for arena '" + arenaName + "'!");
        } else {
            player.sendMessage("§cFailed to save schematic for arena '" + arenaName + "'!");
        }
    }
    
    private void handleCreateKitCommand(Player player, String kitName) {
        if (plugin.getKitManager().getKit(kitName) != null) {
            player.sendMessage("§cKit with that name already exists!");
            return;
        }
        
        plugin.getKitManager().createKit(kitName, player);
        player.sendMessage("§aKit '" + kitName + "' created from your current inventory!");
    }

    private void handleArenaList(Player player) {
        Map<String, Arena> arenas = plugin.getArenaManager().getArenas();
        if (arenas.isEmpty()) {
            player.sendMessage("§cNo arenas found!");
            return;
        }
        
        player.sendMessage("§6=== Arena List ===");
        for (Arena arena : arenas.values()) {
            String status = arena.isComplete() ? "§aComplete" : "§cIncomplete";
            player.sendMessage("§e" + arena.getName() + " §7- " + status);
        }
    }

    private void handleArenaDelete(Player player, String arenaName) {
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            player.sendMessage("§cArena not found!");
            return;
        }
        
        // Remove from manager and config
        plugin.getArenaManager().deleteArena(arenaName);
        player.sendMessage("§aArena '" + arenaName + "' deleted!");
    }
}
