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

public class KitCommand implements CommandExecutor, TabCompleter {

    private final MPEssentials plugin;

    public KitCommand(MPEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.parseMessage("<red>Only players can use this command.</red>"));
            return true;
        }

        PluginConfig config = plugin.getPluginConfig();

        if (!player.hasPermission("mpessentials.kit")) {
            player.sendMessage(plugin.parseMessage(config.getMessage("no-permission")));
            return true;
        }

        if (args.length == 0) {
            List<String> kits = plugin.getKitService().getKitNames();
            if (kits.isEmpty()) {
                player.sendMessage(plugin.parseMessage("<red>No kits available.</red>"));
            } else {
                player.sendMessage(plugin.parseMessage("<gold>Available kits: <yellow>" + String.join(", ", kits)));
            }
            return true;
        }

        String kitName = args[0];
        if (!plugin.getKitService().claimKit(player, kitName)) {
            player.sendMessage(plugin.parseMessage(config.getMessage("kit-not-found")));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player) || args.length != 1) {
            return new ArrayList<>();
        }

        String input = args[0].toLowerCase();
        return plugin.getKitService().getKitNames().stream()
            .filter(name -> name.toLowerCase().startsWith(input))
            .collect(Collectors.toList());
    }
}
