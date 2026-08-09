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

public class HomeCommand implements CommandExecutor, TabCompleter {

    private final MPEssentials plugin;

    public HomeCommand(MPEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.parseMessage("<red>Only players can use this command.</red>"));
            return true;
        }

        PluginConfig config = plugin.getPluginConfig();

        if (!player.hasPermission("mpessentials.home")) {
            player.sendMessage(plugin.parseMessage(config.getMessage("no-permission")));
            return true;
        }

        if (args.length == 0) {
            // Teleport to first home or list homes
            List<String> homes = plugin.getHomeService().getHomeNames(player);
            if (homes.isEmpty()) {
                player.sendMessage(plugin.parseMessage("<red>You have no homes set. Use /sethome <name>.</red>"));
                return true;
            }
            if (homes.size() == 1) {
                plugin.getHomeService().teleportHome(player, homes.get(0));
            } else {
                player.sendMessage(plugin.parseMessage("<gold>Your homes: <yellow>" + String.join(", ", homes)));
            }
            return true;
        }

        String homeName = args[0];
        plugin.getHomeService().teleportHome(player, homeName);
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
