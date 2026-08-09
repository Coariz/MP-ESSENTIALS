package com.mpessentials.repositories;

import java.util.UUID;

public record HomeData(UUID playerUuid, String name, String world, double x, double y, double z, float yaw, float pitch) {}
