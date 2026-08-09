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

public class DelHomeCommand implements CommandExecutor, TabCompleter {

    private final MPEssentials plugin;

    public DelHomeCommand(MPEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.parseMessage("<red>Only players can use this command.</red>"));
            return true;
        }

        PluginConfig config = plugin.getPluginConfig();

        if (!player.hasPermission("mpessentials.home.delete")) {
            player.sendMessage(plugin.parseMessage(config.getMessage("no-permission")));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(plugin.parseMessage("<red>Usage: /delhome <name></red>"));
            return true;
        }

        plugin.getHomeService().deleteHome(player, args[0]);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player) || args.length != 1) {
            return new ArrayList<>();
        }

        List<String> homes = plugin.getHomeService().getHomeNames(player);
        String input = args[0].toLowerCase();
        
        List<String> result = new ArrayList<>();
        for (String home : homes) {
            if (home.toLowerCase().startsWith(input)) {
                result.add(home);
            }
        }
        return result;
    }
}
