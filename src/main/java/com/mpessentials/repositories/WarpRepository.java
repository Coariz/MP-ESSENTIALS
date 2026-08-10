package com.mpessentials.repositories;

import java.util.List;
import java.util.Optional;

public interface WarpRepository {
    void saveWarp(String name, String world, double x, double y, double z, float yaw, float pitch);
    void deleteWarp(String name);
    Optional<WarpData> getWarp(String name);
    List<String> getWarpNames();
    boolean warpExists(String name);
    void close();
}
