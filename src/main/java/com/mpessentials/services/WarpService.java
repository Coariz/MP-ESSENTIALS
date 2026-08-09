package com.mpessentials.services;

import com.mpessentials.MPEssentials;
import com.mpessentials.repositories.WarpData;
import com.mpessentials.repositories.WarpRepository;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class WarpService {

    private final MPEssentials plugin;
    private final WarpRepository repository;

    public WarpService(MPEssentials plugin, WarpRepository repository) {
        this.plugin = plugin;
        this.repository = repository;
    }

    public boolean setWarp(String name, Location location) {
        repository.saveWarp(name, location.getWorld().getName(),
            location.getX(), location.getY(), location.getZ(),
            location.getYaw(), location.getPitch());
        return true;
    }

    public boolean deleteWarp(String name) {
        if (!repository.warpExists(name)) {
            return false;
        }
        repository.deleteWarp(name);
        return true;
    }

    public boolean teleportWarp(Player player, String name) {
        WarpData warp = repository.getWarp(name).orElse(null);
        if (warp == null) {
            return false;
        }

        Location loc = new Location(
            org.bukkit.Bukkit.getWorld(warp.world()),
            warp.x(), warp.y(), warp.z(), warp.yaw(), warp.pitch()
        );

        if (loc.getWorld() == null) {
            return false;
        }

        player.teleport(loc);
        return true;
    }

    public Optional<WarpData> getWarp(String name) {
        return repository.getWarp(name);
    }

    public List<String> getWarpNames() {
        return repository.getWarpNames();
    }

    public boolean warpExists(String name) {
        return repository.warpExists(name);
    }

    public void close() {
        repository.close();
    }
}
