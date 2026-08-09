package com.mpessentials.repositories;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public record KitData(String name, List<ItemStack> items, int cooldownSeconds) {}
