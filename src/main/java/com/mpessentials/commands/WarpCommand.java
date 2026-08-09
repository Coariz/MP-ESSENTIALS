package com.mpessentials.commands;

import com.mpessentials.MPEssentials;
import com.mpessentials.config.PluginConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WarpCommand implements CommandExecutor, TabCompleter {

    private final MPEssentials plugin;

    public WarpCommand(MPEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.parseMessage("<red>Only players can use this command.</red>"));
            return true;
        }

        PluginConfig config = plugin.getPluginConfig();

        if (!player.hasPermission("mpessentials.warp")) {
            player.sendMessage(plugin.parseMessage(config.getMessage("no-permission")));
            return true;
        }

        if (args.length == 0) {
            List<String> warps = plugin.getWarpService().getWarpNames();
            if (warps.isEmpty()) {
                player.sendMessage(plugin.parseMessage("<red>No warps available.</red>"));
            } else {
                player.sendMessage(plugin.parseMessage("<gold>Available warps: <yellow>" + String.join(", ", warps)));
            }
            return true;
        }

        String warpName = args[0];
        
        // Check permission for specific warp if required
        if (config.isWarpPermissionRequired() && !player.hasPermission("mpessentials.warp." + warpName.toLowerCase())) {
            player.sendMessage(plugin.parseMessage(config.getMessage("no-permission")));
            return true;
        }

        if (!plugin.getWarpService().teleportWarp(player, warpName)) {
            player.sendMessage(plugin.parseMessage(config.getMessage("warp-not-found")));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player) || args.length != 1) {
            return new ArrayList<>();
        }

        String input = args[0].toLowerCase();
        return plugin.getWarpService().getWarpNames().stream()
            .filter(name -> name.toLowerCase().startsWith(input))
            .collect(Collectors.toList());
    }
}
