package com.mpessentials.services;

import com.mpessentials.MPEssentials;
import com.mpessentials.config.PluginConfig;
import com.mpessentials.repositories.CooldownRepository;
import com.mpessentials.repositories.KitData;
import com.mpessentials.repositories.KitRepository;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class KitService {

    private final MPEssentials plugin;
    private final KitRepository repository;
    private final CooldownRepository cooldownRepo;

    public KitService(MPEssentials plugin, KitRepository repository, CooldownRepository cooldownRepo) {
        this.plugin = plugin;
        this.repository = repository;
        this.cooldownRepo = cooldownRepo;
    }

    public KitClaimResult claimKit(Player player, String name, boolean bypassCooldown) {
        KitData kit = repository.getKit(name).orElse(null);
        if (kit == null) {
            return KitClaimResult.NOT_FOUND;
        }

        PluginConfig config = plugin.getPluginConfig();
        String cooldownKey = "kit:" + name;

        if (!bypassCooldown && cooldownRepo.hasCooldown(player.getUniqueId(), cooldownKey)) {
            return KitClaimResult.ON_COOLDOWN;
        }

        // Give items to player
        var inventory = player.getInventory();
        for (ItemStack item : kit.items()) {
            if (item != null && item.getType().isAir() == false) {
                Map<Integer, ItemStack> leftovers = inventory.addItem(item);
                for (ItemStack leftover : leftovers.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), leftover);
                }
            }
        }

        // Set cooldown
        if (kit.cooldownSeconds() > 0) {
            long expiry = System.currentTimeMillis() + (kit.cooldownSeconds() * 1000L);
            cooldownRepo.setCooldown(player.getUniqueId(), cooldownKey, expiry);
        }

        player.sendMessage(plugin.parseMessage(config.getMessage("kit-claimed")));
        return KitClaimResult.SUCCESS;
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

    public enum KitClaimResult {
        SUCCESS,
        NOT_FOUND,
        ON_COOLDOWN
    }
}
