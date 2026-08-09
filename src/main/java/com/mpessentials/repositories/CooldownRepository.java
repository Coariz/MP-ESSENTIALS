package com.mpessentials.repositories;

import java.util.Map;
import java.util.UUID;

public interface CooldownRepository {
    void setCooldown(UUID playerUuid, String key, long expiryTime);
    long getCooldown(UUID playerUuid, String key);
    boolean hasCooldown(UUID playerUuid, String key);
    void removeCooldown(UUID playerUuid, String key);
    void close();
}
