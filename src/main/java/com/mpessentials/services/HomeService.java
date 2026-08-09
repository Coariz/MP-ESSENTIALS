package com.mpessentials.services;

import com.mpessentials.MPEssentials;
import com.mpessentials.config.PluginConfig;
import com.mpessentials.repositories.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class HomeService {

    private final MPEssentials plugin;
    private final HomeRepository repository;
    private final CooldownRepository cooldownRepo;

    public HomeService(MPEssentials plugin, HomeRepository repository, CooldownRepository cooldownRepo) {
        this.plugin = plugin;
        this.repository = repository;
        this.cooldownRepo = cooldownRepo;
    }

    public boolean setHome(Player player, String name) {
        PluginConfig config = plugin.getPluginConfig();
        
        if (repository.countHomes(player.getUniqueId()) >= config.getMaxHomes()) {
            player.sendMessage(plugin.parseMessage(config.getMessage("home-limit-reached")));
            return false;
        }

        Location loc = player.getLocation();
        repository.saveHome(player.getUniqueId(), name, loc.getWorld().getName(), 
            loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        
        player.sendMessage(plugin.parseMessage(config.getMessage("home-set")));
        return true;
    }

    public boolean deleteHome(Player player, String name) {
        if (repository.getHome(player.getUniqueId(), name).isEmpty()) {
            player.sendMessage(plugin.parseMessage(plugin.getPluginConfig().getMessage("home-not-found")));
            return false;
        }

        repository.deleteHome(player.getUniqueId(), name);
        player.sendMessage(plugin.parseMessage(plugin.getPluginConfig().getMessage("home-deleted")));
        return true;
    }

    public boolean teleportHome(Player player, String name) {
        HomeData home = repository.getHome(player.getUniqueId(), name).orElse(null);
        if (home == null) {
            player.sendMessage(plugin.parseMessage(plugin.getPluginConfig().getMessage("home-not-found")));
            return false;
        }

        Location loc = new Location(
            org.bukkit.Bukkit.getWorld(home.world()),
            home.x(), home.y(), home.z(), home.yaw(), home.pitch()
        );
        
        if (loc.getWorld() == null) {
            player.sendMessage(plugin.parseMessage("<red>Home world not found.</red>"));
            return false;
        }

        player.teleport(loc);
        return true;
    }

    public List<String> getHomeNames(Player player) {
        return repository.getHomeNames(player.getUniqueId());
    }

    public int getHomeCount(Player player) {
        return repository.countHomes(player.getUniqueId());
    }

    public void close() {
        repository.close();
    }
}
