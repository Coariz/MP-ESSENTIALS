package com.mpessentials.repositories;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface KitRepository {
    void saveKit(String name, List<ItemStack> items, int cooldownSeconds);
    void deleteKit(String name);
    Optional<KitData> getKit(String name);
    List<String> getKitNames();
    boolean kitExists(String name);
    void close();
}
