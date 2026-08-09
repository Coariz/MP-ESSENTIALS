package com.mpessentials.repositories;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SQLiteCooldownRepository implements CooldownRepository {

    private final Connection connection;
    private final Map<String, Long> cache = new ConcurrentHashMap<>();

    public SQLiteCooldownRepository(JavaPlugin plugin) {
        try {
            Class.forName("org.sqlite.JDBC");
            String dbPath = plugin.getDataFolder().getParentFile().getAbsolutePath() + "/MPEssentials.db";
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            createTables();
            loadCache();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SQLiteCooldownRepository", e);
        }
    }

    private void createTables() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS cooldowns (
                player_uuid TEXT NOT NULL,
                key TEXT NOT NULL,
                expiry_time REAL NOT NULL,
                PRIMARY KEY (player_uuid, key)
            )
            """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    private void loadCache() throws SQLException {
        String sql = "SELECT * FROM cooldowns WHERE expiry_time > ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, System.currentTimeMillis());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String cacheKey = rs.getString("player_uuid") + ":" + rs.getString("key");
                    cache.put(cacheKey, rs.getLong("expiry_time"));
                }
            }
        }
    }

    @Override
    public void setCooldown(UUID playerUuid, String key, long expiryTime) {
        String sql = """
            INSERT OR REPLACE INTO cooldowns (player_uuid, key, expiry_time)
            VALUES (?, ?, ?)
            """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUuid.toString());
            pstmt.setString(2, key);
            pstmt.setLong(3, expiryTime);
            pstmt.executeUpdate();
            
            String cacheKey = playerUuid.toString() + ":" + key;
            cache.put(cacheKey, expiryTime);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to set cooldown", e);
        }
    }

    @Override
    public long getCooldown(UUID playerUuid, String key) {
        String cacheKey = playerUuid.toString() + ":" + key;
        long expiry = cache.getOrDefault(cacheKey, 0L);
        long now = System.currentTimeMillis();
        return expiry > now ? expiry - now : 0;
    }

    @Override
    public boolean hasCooldown(UUID playerUuid, String key) {
        return getCooldown(playerUuid, key) > 0;
    }

    @Override
    public void removeCooldown(UUID playerUuid, String key) {
        String sql = "DELETE FROM cooldowns WHERE player_uuid = ? AND key = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUuid.toString());
            pstmt.setString(2, key);
            pstmt.executeUpdate();
            
            String cacheKey = playerUuid.toString() + ":" + key;
            cache.remove(cacheKey);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove cooldown", e);
        }
    }

    @Override
    public void close() {
        try {
            // Clean up expired cooldowns before closing
            String sql = "DELETE FROM cooldowns WHERE expiry_time <= ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setLong(1, System.currentTimeMillis());
                pstmt.executeUpdate();
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
