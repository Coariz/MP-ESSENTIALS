package com.mpessentials.commands;

import com.mpessentials.MPEssentials;
import com.mpessentials.config.PluginConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpAcceptCommand implements CommandExecutor {

    private final MPEssentials plugin;

    public TpAcceptCommand(MPEssentials plugin) {
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

        plugin.getTeleportRequestService().acceptTpa(player);
        return true;
    }
}
