package com.mpessentials.services;

import com.mpessentials.MPEssentials;
import com.mpessentials.config.PluginConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportRequestService {

    private final MPEssentials plugin;
    private final Map<UUID, TpaRequest> requests = new ConcurrentHashMap<>();
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    public record TpaRequest(UUID from, UUID to, boolean here, long expiry) {}

    public TeleportRequestService(MPEssentials plugin) {
        this.plugin = plugin;
    }

    public boolean sendTpa(Player from, Player to) {
        PluginConfig config = plugin.getPluginConfig();
        
        // Check cooldown
        if (cooldowns.containsKey(from.getUniqueId())) {
            long remaining = cooldowns.get(from.getUniqueId()) - System.currentTimeMillis();
            if (remaining > 0) {
                from.sendMessage(plugin.parseMessage(config.getMessage("tpa-cooldown")));
                return false;
            }
        }

        // Set cooldown
        cooldowns.put(from.getUniqueId(), System.currentTimeMillis() + (config.getTpaCooldown() * 1000L));

        // Create request
        TpaRequest request = new TpaRequest(
            from.getUniqueId(),
            to.getUniqueId(),
            false,
            System.currentTimeMillis() + (config.getTpaTimeout() * 1000L)
        );
        requests.put(to.getUniqueId(), request);

        // Send messages
        String msg = config.getMessage("tpa-sent").replace("%player%", to.getName());
        from.sendMessage(plugin.parseMessage(msg));
        
        msg = config.getMessage("tpa-request").replace("%player%", from.getName());
        to.sendMessage(plugin.parseMessage(msg));

        return true;
    }

    public boolean sendTpaHere(Player from, Player to) {
        PluginConfig config = plugin.getPluginConfig();
        
        if (cooldowns.containsKey(from.getUniqueId())) {
            long remaining = cooldowns.get(from.getUniqueId()) - System.currentTimeMillis();
            if (remaining > 0) {
                from.sendMessage(plugin.parseMessage(config.getMessage("tpa-cooldown")));
                return false;
            }
        }

        cooldowns.put(from.getUniqueId(), System.currentTimeMillis() + (config.getTpaCooldown() * 1000L));

        TpaRequest request = new TpaRequest(
            from.getUniqueId(),
            to.getUniqueId(),
            true,
            System.currentTimeMillis() + (config.getTpaTimeout() * 1000L)
        );
        requests.put(to.getUniqueId(), request);

        String msg = config.getMessage("tpa-sent").replace("%player%", to.getName());
        from.sendMessage(plugin.parseMessage(msg));
        
        msg = config.getMessage("tpahere-request").replace("%player%", from.getName());
        to.sendMessage(plugin.parseMessage(msg));

        return true;
    }

    public boolean acceptTpa(Player player) {
        PluginConfig config = plugin.getPluginConfig();
        TpaRequest request = requests.get(player.getUniqueId());

        if (request == null || System.currentTimeMillis() > request.expiry()) {
            requests.remove(player.getUniqueId());
            if (request != null) {
                player.sendMessage(plugin.parseMessage(config.getMessage("tpa-timeout")));
            }
            return false;
        }

        Player from = Bukkit.getPlayer(request.from());
        if (from == null || !from.isOnline()) {
            requests.remove(player.getUniqueId());
            player.sendMessage(plugin.parseMessage(config.getMessage("player-not-found")));
            return false;
        }

        requests.remove(player.getUniqueId());
        player.sendMessage(plugin.parseMessage(config.getMessage("tpa-accepted")));
        from.sendMessage(plugin.parseMessage(config.getMessage("tpa-accepted")));

        // Perform teleport
        if (request.here()) {
            from.teleport(player.getLocation());
        } else {
            player.teleport(from.getLocation());
        }

        return true;
    }

    public boolean denyTpa(Player player) {
        PluginConfig config = plugin.getPluginConfig();
        TpaRequest request = requests.remove(player.getUniqueId());

        if (request == null) {
            return false;
        }

        player.sendMessage(plugin.parseMessage(config.getMessage("tpa-denied")));
        
        Player from = Bukkit.getPlayer(request.from());
        if (from != null && from.isOnline()) {
            from.sendMessage(plugin.parseMessage(config.getMessage("tpa-denied")));
        }

        return true;
    }

    public void cleanupExpired() {
        long now = System.currentTimeMillis();
        requests.entrySet().removeIf(e -> e.getValue().expiry() < now);
        cooldowns.entrySet().removeIf(e -> e.getValue() < now);
    }
}
