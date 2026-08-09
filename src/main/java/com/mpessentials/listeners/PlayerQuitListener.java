package com.mpessentials.listeners;

import com.mpessentials.MPEssentials;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final MPEssentials plugin;

    public PlayerQuitListener(MPEssentials plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Clean up TPA requests for quitting player
        plugin.getTeleportRequestService().cleanupExpired();
    }
}
