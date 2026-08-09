package com.mpessentials.commands;

import com.mpessentials.MPEssentials;
import com.mpessentials.config.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GiveKitCommand implements CommandExecutor, TabCompleter {

    private final MPEssentials plugin;

    public GiveKitCommand(MPEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PluginConfig config = plugin.getPluginConfig();

        if (!sender.hasPermission("mpessentials.kit.admin")) {
            sender.sendMessage(plugin.parseMessage(config.getMessage("no-permission")));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(plugin.parseMessage("<red>Usage: /givekit <player> <kit></red>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(plugin.parseMessage(config.getMessage("player-not-found")));
            return true;
        }

        String kitName = args[1];
        if (!plugin.getKitService().claimKit(target, kitName)) {
            sender.sendMessage(plugin.parseMessage(config.getMessage("kit-not-found")));
        } else {
            sender.sendMessage(plugin.parseMessage("<green>Kit given to " + target.getName() + "</green>"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(input))
                .collect(Collectors.toList());
        } else if (args.length == 2) {
            String input = args[1].toLowerCase();
            return plugin.getKitService().getKitNames().stream()
                .filter(name -> name.toLowerCase().startsWith(input))
                .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
