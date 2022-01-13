package io.github.soju06.hotreload;

import io.github.soju06.filesystems.watch.WatchManager;
import io.github.soju06.hotreload.commands.pluginmanage.HotReloadCommands;
import io.github.soju06.hotreload.utility.UpdateUtility;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class HotReload extends JavaPlugin {
    private static HotReload instance = null;
    private WatchManager<HotReloadDaemon> watchManager;
    private UUID threadId;

    @Override
    public void onEnable() {
        if (watchManager != null) {
            try {
                watchManager.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        instance = this;

        getLogger().info(ChatColor.AQUA + "HotReload! v" + getDescription().getVersion());
        getCommand("hotreload").setExecutor(new HotReloadCommands());
        initConfig();
        watchManager = new WatchManager<>();

        var u = threadId = UUID.randomUUID();
        var thread = new Thread(()->main(u));
        thread.setDaemon(true);
        thread.start();
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                var latest = UpdateUtility.getLatest();
                if (!getDescription().getVersion().equals(Objects.requireNonNull(latest))) {
                    getLogger().warning("HotReload: The latest version of HotReload has been released!");
                    getLogger().warning("HotReload: Download from https://github.com/Soju06/hotreload/releases/latest/download/hotreload-" + latest + ".jar");
                }
            } catch (Exception e) {
                getLogger().warning("HotReload: Update check fail. " + e);
            }
        });
    }

    void main(UUID currentId) {
        while (currentId == threadId) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (HotReloadDaemon daemon : watchManager) {
                try {
                    daemon.watch();
                } catch (IOException e) {
                    sendAdminChat(ChatColor.RED + "HotReload: [watch pump thread] IOException " + daemon.getName() + "\n" + e);
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDisable() {
        threadId = UUID.randomUUID();
        if (watchManager != null) {
            try {
                watchManager.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            watchManager = null;
        }
        getLogger().warning(ChatColor.RED + "hotReload disabled!");
    }

    void initConfig() {
        var cf = new File(getDataFolder(), "config.yml");
        if (!cf.exists() || cf.length() == 0) {
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
    }

    public int sendAdminChat(String chat) {
        int c = 0;
        getLogger().info(chat);
        for (Player player : getServer().getOnlinePlayers()) {
            if (!player.isOp() || !player.isOnline()) continue;
            player.sendMessage(chat);
            c++;
        }
        return c;
    }

    public int sendAdminChat(Component chat) {
        int c = 0;
        getLogger().info(chat.toString());
        for (Player player : getServer().getOnlinePlayers()) {
            if (!player.isOp() || !player.isOnline()) continue;
            player.sendMessage(chat);
            c++;
        }
        return c;
    }

    public WatchManager<HotReloadDaemon> getWatchManager() {
        return watchManager;
    }

    public static HotReload getInstance() {
        return instance;
    }
}
