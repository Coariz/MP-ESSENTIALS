package com.mpessentials.repositories;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SQLiteWarpRepository implements WarpRepository {

    private final Connection connection;
    private final Map<String, WarpData> cache = new ConcurrentHashMap<>();

    public SQLiteWarpRepository(JavaPlugin plugin) {
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
            throw new RuntimeException("Failed to initialize SQLiteWarpRepository", e);
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
            CREATE TABLE IF NOT EXISTS warps (
                name TEXT PRIMARY KEY,
                world TEXT NOT NULL,
                x REAL NOT NULL,
                y REAL NOT NULL,
                z REAL NOT NULL,
                yaw REAL NOT NULL,
                pitch REAL NOT NULL
            )
            """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    private void loadCache() throws SQLException {
        String sql = "SELECT * FROM warps";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                WarpData warp = new WarpData(
                    rs.getString("name"),
                    rs.getString("world"),
                    rs.getDouble("x"),
                    rs.getDouble("y"),
                    rs.getDouble("z"),
                    rs.getFloat("yaw"),
                    rs.getFloat("pitch")
                );
                cache.put(warp.name(), warp);
            }
        }
    }

    @Override
    public void saveWarp(String name, String world, double x, double y, double z, float yaw, float pitch) {
        String sql = """
            INSERT OR REPLACE INTO warps (name, world, x, y, z, yaw, pitch)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, world);
            pstmt.setDouble(3, x);
            pstmt.setDouble(4, y);
            pstmt.setDouble(5, z);
            pstmt.setFloat(6, yaw);
            pstmt.setFloat(7, pitch);
            pstmt.executeUpdate();
            
            cache.put(name, new WarpData(name, world, x, y, z, yaw, pitch));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save warp", e);
        }
    }

    @Override
    public void deleteWarp(String name) {
        String sql = "DELETE FROM warps WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            cache.remove(name);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete warp", e);
        }
    }

    @Override
    public Optional<WarpData> getWarp(String name) {
        return Optional.ofNullable(cache.get(name));
    }

    @Override
    public List<String> getWarpNames() {
        return new ArrayList<>(cache.keySet());
    }

    @Override
    public boolean warpExists(String name) {
        return cache.containsKey(name);
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
