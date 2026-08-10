package com.mpessentials.repositories;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SQLiteHomeRepository implements HomeRepository {

    private final Connection connection;
    private final Map<UUID, List<HomeData>> cache = new ConcurrentHashMap<>();

    public SQLiteHomeRepository(JavaPlugin plugin) {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            String dbPath = plugin.getDataFolder().getParentFile().getAbsolutePath() + "/MPEssentials.db";
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            this.connection = conn;
            createTables();
            loadCache();
        } catch (Exception e) {
            closeConnectionSilently(conn);
            throw new RuntimeException("Failed to initialize SQLiteHomeRepository", e);
        }
    }

    private void closeConnectionSilently(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.close();
        } catch (SQLException ignored) {
        }
    }

    private void createTables() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS homes (
                player_uuid TEXT NOT NULL,
                name TEXT NOT NULL,
                world TEXT NOT NULL,
                x REAL NOT NULL,
                y REAL NOT NULL,
                z REAL NOT NULL,
                yaw REAL NOT NULL,
                pitch REAL NOT NULL,
                PRIMARY KEY (player_uuid, name)
            )
            """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    private void loadCache() throws SQLException {
        String sql = "SELECT * FROM homes";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                HomeData home = new HomeData(
                    uuid,
                    rs.getString("name"),
                    rs.getString("world"),
                    rs.getDouble("x"),
                    rs.getDouble("y"),
                    rs.getDouble("z"),
                    rs.getFloat("yaw"),
                    rs.getFloat("pitch")
                );
                cache.computeIfAbsent(uuid, k -> new ArrayList<>()).add(home);
            }
        }
    }

    @Override
    public void saveHome(UUID playerUuid, String name, String world, double x, double y, double z, float yaw, float pitch) {
        String sql = """
            INSERT OR REPLACE INTO homes (player_uuid, name, world, x, y, z, yaw, pitch)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUuid.toString());
            pstmt.setString(2, name);
            pstmt.setString(3, world);
            pstmt.setDouble(4, x);
            pstmt.setDouble(5, y);
            pstmt.setDouble(6, z);
            pstmt.setFloat(7, yaw);
            pstmt.setFloat(8, pitch);
            pstmt.executeUpdate();
            
            // Update cache
            cache.computeIfAbsent(playerUuid, k -> new ArrayList<>());
            cache.get(playerUuid).removeIf(h -> h.name().equals(name));
            cache.get(playerUuid).add(new HomeData(playerUuid, name, world, x, y, z, yaw, pitch));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save home", e);
        }
    }

    @Override
    public void deleteHome(UUID playerUuid, String name) {
        String sql = "DELETE FROM homes WHERE player_uuid = ? AND name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUuid.toString());
            pstmt.setString(2, name);
            pstmt.executeUpdate();
            
            // Update cache
            if (cache.containsKey(playerUuid)) {
                cache.get(playerUuid).removeIf(h -> h.name().equals(name));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete home", e);
        }
    }

    @Override
    public Optional<HomeData> getHome(UUID playerUuid, String name) {
        return cache.getOrDefault(playerUuid, new ArrayList<>()).stream()
            .filter(h -> h.name().equals(name))
            .findFirst();
    }

    @Override
    public List<String> getHomeNames(UUID playerUuid) {
        return cache.getOrDefault(playerUuid, new ArrayList<>()).stream()
            .map(HomeData::name)
            .toList();
    }

    @Override
    public int countHomes(UUID playerUuid) {
        return cache.getOrDefault(playerUuid, new ArrayList<>()).size();
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
