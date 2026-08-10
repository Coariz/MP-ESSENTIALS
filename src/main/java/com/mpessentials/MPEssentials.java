package com.mpessentials;

import com.mpessentials.config.PluginConfig;
import com.mpessentials.services.*;
import com.mpessentials.repositories.*;
import com.mpessentials.listeners.PlayerQuitListener;
import com.mpessentials.commands.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MPEssentials extends JavaPlugin {

    private PluginConfig config;
    private HomeService homeService;
    private WarpService warpService;
    private KitService kitService;
    private TeleportRequestService teleportRequestService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = new PluginConfig(this);

        // Initialize repositories (async database setup)
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            HomeRepository homeRepo = null;
            WarpRepository warpRepo = null;
            KitRepository kitRepo = null;
            CooldownRepository cooldownRepo = null;
            try {
                homeRepo = new SQLiteHomeRepository(this);
                warpRepo = new SQLiteWarpRepository(this);
                kitRepo = new SQLiteKitRepository(this);
                cooldownRepo = new SQLiteCooldownRepository(this);
            } catch (Exception e) {
                if (homeRepo != null) homeRepo.close();
                if (warpRepo != null) warpRepo.close();
                if (kitRepo != null) kitRepo.close();
                if (cooldownRepo != null) cooldownRepo.close();
                getLogger().severe("Failed to initialize repositories. Disabling plugin: " + e.getMessage());
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }

            final HomeRepository home = homeRepo;
            final WarpRepository warp = warpRepo;
            final KitRepository kit = kitRepo;
            final CooldownRepository cooldown = cooldownRepo;

            getServer().getScheduler().runTask(this, () -> {
                // Initialize services
                homeService = new HomeService(this, home, cooldown);
                warpService = new WarpService(this, warp);
                kitService = new KitService(this, kit, cooldown);
                teleportRequestService = new TeleportRequestService(this);

                // Register commands
                registerCommands();

                // Register listeners
                registerListeners();

                getLogger().info("MPEssentials enabled successfully!");
            });
        });
    }

    @Override
    public void onDisable() {
        if (homeService != null) homeService.close();
        if (warpService != null) warpService.close();
        if (kitService != null) kitService.close();
        getLogger().info("MPEssentials disabled.");
    }

    private void registerCommands() {
        // Player commands
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("home").setTabCompleter(new HomeCommand(this));
        
        getCommand("sethome").setExecutor(new SetHomeCommand(this));
        getCommand("sethome").setTabCompleter(new SetHomeCommand(this));
        
        getCommand("delhome").setExecutor(new DelHomeCommand(this));
        getCommand("delhome").setTabCompleter(new DelHomeCommand(this));
        
        getCommand("tpa").setExecutor(new TpaCommand(this));
        getCommand("tpa").setTabCompleter(new TpaCommand(this));
        
        getCommand("tpahere").setExecutor(new TpaHereCommand(this));
        getCommand("tpahere").setTabCompleter(new TpaHereCommand(this));
        
        getCommand("tpaccept").setExecutor(new TpAcceptCommand(this));
        getCommand("tpdeny").setExecutor(new TpDenyCommand(this));
        
        getCommand("warp").setExecutor(new WarpCommand(this));
        getCommand("warp").setTabCompleter(new WarpCommand(this));
        
        getCommand("setwarp").setExecutor(new SetWarpCommand(this));
        getCommand("delwarp").setExecutor(new DelWarpCommand(this));
        getCommand("delwarp").setTabCompleter(new DelWarpCommand(this));
        
        getCommand("kit").setExecutor(new KitCommand(this));
        getCommand("kit").setTabCompleter(new KitCommand(this));
        
        getCommand("givekit").setExecutor(new GiveKitCommand(this));
        getCommand("givekit").setTabCompleter(new GiveKitCommand(this));
        
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
    }

    public PluginConfig getPluginConfig() {
        return config;
    }

    public HomeService getHomeService() {
        return homeService;
    }

    public WarpService getWarpService() {
        return warpService;
    }

    public KitService getKitService() {
        return kitService;
    }

    public TeleportRequestService getTeleportRequestService() {
        return teleportRequestService;
    }

    public Component parseMessage(String message) {
        return MiniMessage.miniMessage().deserialize(config.getPrefix() + message);
    }
}
