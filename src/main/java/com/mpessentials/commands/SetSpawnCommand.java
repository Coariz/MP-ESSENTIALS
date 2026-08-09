package com.mpessentials.commands;

import com.mpessentials.MPEssentials;
import com.mpessentials.config.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {

    private final MPEssentials plugin;

    public SetSpawnCommand(MPEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PluginConfig config = plugin.getPluginConfig();

        if (!sender.hasPermission("mpessentials.spawn.set")) {
            sender.sendMessage(plugin.parseMessage(config.getMessage("no-permission")));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.parseMessage("<red>Only players can use this command.</red>"));
            return true;
        }

        // Set spawn for the main world
        var server = Bukkit.getServer();
        if (server.getWorlds().isEmpty()) {
            sender.sendMessage(plugin.parseMessage("<red>No worlds available.</red>"));
            return true;
        }

        var world = server.getWorlds().get(0);
        world.setSpawnLocation(player.getLocation());
        
        sender.sendMessage(plugin.parseMessage(config.getMessage("spawn-set")));
        return true;
    }
}
