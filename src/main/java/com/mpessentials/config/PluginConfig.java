package com.mpessentials.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginConfig {

    private final JavaPlugin plugin;

    public PluginConfig(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public int getMaxHomes() {
        return plugin.getConfig().getInt("homes.max-homes", 5);
    }

    public int getHomeCooldown() {
        return plugin.getConfig().getInt("homes.cooldown-seconds", 30);
    }

    public int getTpaTimeout() {
        return plugin.getConfig().getInt("tpa.timeout-seconds", 60);
    }

    public int getTpaCooldown() {
        return plugin.getConfig().getInt("tpa.cooldown-seconds", 10);
    }

    public boolean isWarpPermissionRequired() {
        return plugin.getConfig().getBoolean("warps.require-permission", true);
    }

    public int getKitCooldown() {
        return plugin.getConfig().getInt("kits.cooldown-seconds", 300);
    }

    public int getSpawnTeleportDelay() {
        return plugin.getConfig().getInt("spawn.teleport-delay", 5);
    }

    public String getMessage(String key) {
        return plugin.getConfig().getString("messages." + key, "");
    }

    public String getPrefix() {
        return plugin.getConfig().getString("messages.prefix", "<gray>[<gold>MPEssentials<gray>] ");
    }
}
