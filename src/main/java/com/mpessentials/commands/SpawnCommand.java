package com.mpessentials.commands;

import com.mpessentials.MPEssentials;
import com.mpessentials.config.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    private final MPEssentials plugin;

    public SpawnCommand(MPEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PluginConfig config = plugin.getPluginConfig();

        if (!sender.hasPermission("mpessentials.spawn")) {
            sender.sendMessage(plugin.parseMessage(config.getMessage("no-permission")));
            return true;
        }

        // Admin can teleport other players
        if (args.length > 0 && sender.hasPermission("mpessentials.spawn.admin")) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                sender.sendMessage(plugin.parseMessage(config.getMessage("player-not-found")));
                return true;
            }
            Location spawn = getSpawnLocation();
            if (spawn != null) {
                target.teleport(spawn);
                target.sendMessage(plugin.parseMessage("<green>Teleported to spawn.</green>"));
                sender.sendMessage(plugin.parseMessage("<green>Teleported " + target.getName() + " to spawn.</green>"));
            } else {
                sender.sendMessage(plugin.parseMessage("<red>Spawn location not set.</red>"));
            }
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.parseMessage("<red>Only players can use this command.</red>"));
            return true;
        }

        Location spawn = getSpawnLocation();
        if (spawn != null) {
            player.teleport(spawn);
            player.sendMessage(plugin.parseMessage("<green>Teleported to spawn.</green>"));
        } else {
            player.sendMessage(plugin.parseMessage("<red>Spawn location not set.</red>"));
        }
        return true;
    }

    private Location getSpawnLocation() {
        // Try to get spawn from server's main world
        var server = Bukkit.getServer();
        if (server.getWorlds().isEmpty()) {
            return null;
        }
        var world = server.getWorlds().get(0);
        return world.getSpawnLocation();
    }
}
