package com.mpessentials.services;

import com.mpessentials.MPEssentials;
import com.mpessentials.config.PluginConfig;
import com.mpessentials.repositories.KitData;
import com.mpessentials.repositories.KitRepository;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class KitService {

    private final MPEssentials plugin;
    private final KitRepository repository;
    private final CooldownRepository cooldownRepo;

    public KitService(MPEssentials plugin, KitRepository repository, CooldownRepository cooldownRepo) {
        this.plugin = plugin;
        this.repository = repository;
        this.cooldownRepo = cooldownRepo;
    }

    public boolean claimKit(Player player, String name) {
        KitData kit = repository.getKit(name).orElse(null);
        if (kit == null) {
            return false;
        }

        PluginConfig config = plugin.getPluginConfig();
        String cooldownKey = "kit:" + name;

        if (cooldownRepo.hasCooldown(player.getUniqueId(), cooldownKey)) {
            player.sendMessage(plugin.parseMessage(config.getMessage("kit-cooldown")));
            return false;
        }

        // Give items to player
        var inventory = player.getInventory();
        for (ItemStack item : kit.items()) {
            if (item != null && item.getType().isAir() == false) {
                inventory.addItem(item);
            }
        }

        // Set cooldown
        if (kit.cooldownSeconds() > 0) {
            long expiry = System.currentTimeMillis() + (kit.cooldownSeconds() * 1000L);
            cooldownRepo.setCooldown(player.getUniqueId(), cooldownKey, expiry);
        }

        player.sendMessage(plugin.parseMessage(config.getMessage("kit-claimed")));
        return true;
    }

    public List<String> getKitNames() {
        return repository.getKitNames();
    }

    public boolean kitExists(String name) {
        return repository.kitExists(name);
    }

    public void close() {
        repository.close();
    }
}
