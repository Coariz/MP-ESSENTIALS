package com.mpessentials.repositories;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SQLiteKitRepository implements KitRepository {

    private final Connection connection;
    private final Map<String, KitData> cache = new ConcurrentHashMap<>();

    public SQLiteKitRepository(JavaPlugin plugin) {
        try {
            Class.forName("org.sqlite.JDBC");
            String dbPath = plugin.getDataFolder().getParentFile().getAbsolutePath() + "/MPEssentials.db";
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            createTables();
            loadCache();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SQLiteKitRepository", e);
        }
    }

    private void createTables() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS kits (
                name TEXT PRIMARY KEY,
                items BLOB NOT NULL,
                cooldown INTEGER NOT NULL
            )
            """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    private void loadCache() throws SQLException {
        String sql = "SELECT * FROM kits";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                byte[] itemData = rs.getBytes("items");
                List<ItemStack> items = deserializeItems(itemData);
                KitData kit = new KitData(
                    rs.getString("name"),
                    items,
                    rs.getInt("cooldown")
                );
                cache.put(kit.name(), kit);
            }
        }
    }

    private byte[] serializeItems(List<ItemStack> items) {
        try {
            var baos = new java.io.ByteArrayOutputStream();
            var oos = new BukkitObjectOutputStream(new GZIPOutputStream(baos));
            oos.writeInt(items.size());
            for (ItemStack item : items) {
                oos.writeObject(item);
            }
            oos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize items", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<ItemStack> deserializeItems(byte[] data) {
        try {
            var bais = new java.io.ByteArrayInputStream(data);
            var ois = new BukkitObjectInputStream(new GZIPInputStream(bais));
            int size = ois.readInt();
            List<ItemStack> items = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                ItemStack item = (ItemStack) ois.readObject();
                if (item != null && item.getType() != Material.AIR) {
                    items.add(item);
                }
            }
            ois.close();
            return items;
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize items", e);
        }
    }

    @Override
    public void saveKit(String name, List<ItemStack> items, int cooldownSeconds) {
        byte[] itemData = serializeItems(items);
        String sql = """
            INSERT OR REPLACE INTO kits (name, items, cooldown)
            VALUES (?, ?, ?)
            """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setBytes(2, itemData);
            pstmt.setInt(3, cooldownSeconds);
            pstmt.executeUpdate();
            
            cache.put(name, new KitData(name, new ArrayList<>(items), cooldownSeconds));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save kit", e);
        }
    }

    @Override
    public void deleteKit(String name) {
        String sql = "DELETE FROM kits WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            cache.remove(name);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete kit", e);
        }
    }

    @Override
    public Optional<KitData> getKit(String name) {
        return Optional.ofNullable(cache.get(name));
    }

    @Override
    public List<KitData> getAllKits() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public List<String> getKitNames() {
        return new ArrayList<>(cache.keySet());
    }

    @Override
    public boolean kitExists(String name) {
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
