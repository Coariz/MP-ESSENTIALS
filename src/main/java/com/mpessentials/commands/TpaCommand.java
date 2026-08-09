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

public class TpaCommand implements CommandExecutor, TabCompleter {

    private final MPEssentials plugin;

    public TpaCommand(MPEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.parseMessage("<red>Only players can use this command.</red>"));
            return true;
        }

        PluginConfig config = plugin.getPluginConfig();

        if (!player.hasPermission("mpessentials.tpa")) {
            player.sendMessage(plugin.parseMessage(config.getMessage("no-permission")));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(plugin.parseMessage("<red>Usage: /tpa <player></red>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(plugin.parseMessage(config.getMessage("player-not-found")));
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(plugin.parseMessage("<red>You cannot teleport to yourself.</red>"));
            return true;
        }

        plugin.getTeleportRequestService().sendTpa(player, target);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player) || args.length != 1) {
            return new ArrayList<>();
        }

        String input = args[0].toLowerCase();
        return Bukkit.getOnlinePlayers().stream()
            .map(Player::getName)
            .filter(name -> name.toLowerCase().startsWith(input))
            .filter(name -> !name.equals(player.getName()))
            .collect(Collectors.toList());
    }
}
