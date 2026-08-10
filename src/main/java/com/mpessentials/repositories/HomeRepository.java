package com.mpessentials.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HomeRepository {
    void saveHome(UUID playerUuid, String name, String world, double x, double y, double z, float yaw, float pitch);
    void deleteHome(UUID playerUuid, String name);
    Optional<HomeData> getHome(UUID playerUuid, String name);
    List<String> getHomeNames(UUID playerUuid);
    int countHomes(UUID playerUuid);
    void close();
}
