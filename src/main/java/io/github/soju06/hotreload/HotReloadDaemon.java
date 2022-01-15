package io.github.soju06.hotreload;

import io.github.soju06.filesystems.watch.WatchDaemon;
import io.github.soju06.hotreload.utility.ChatUtility;
import io.github.soju06.hotreload.utility.PluginUtility;
import org.bukkit.ChatColor;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HotReloadDaemon extends WatchDaemon {
    private Date holdTime = new Date(0);
    private final ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    private Plugin plugin;
    private int reloadCount = 0;

    public HotReloadDaemon(Plugin plugin, File file) {
        super(file);
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public String getName() {
        return plugin.getName();
    }

    public int getReloadCount() {
        return reloadCount;
    }

    public void addReloadCount() {
        reloadCount++;
    }

    @Override
    public void onModify(File file) {
        if (file.getPath().equals(getPath())) {
            hold_run(false);
        }
    }

    void hold_run(boolean schedule) {
        var now = new Date();
        var use = now.getTime() - holdTime.getTime() >= 1000;
        if (!schedule) {
            holdTime = now;
            if (!use) {
                threadPoolExecutor.getQueue().poll();
                threadPoolExecutor.schedule(()->hold_run(true),
                        1200, TimeUnit.MILLISECONDS);
            }
        }

        ChatUtility.sendChat(ChatColor.LIGHT_PURPLE + "plug-in files have been changed: " + plugin.getName());
        if (use) {
            try {
                PluginUtility.unload(plugin);
                plugin = PluginUtility.load(getFile());
                ChatUtility.sendChat("Plug-in has been loaded: " + plugin.getName(), ChatColor.GREEN);
                addReloadCount();
                PluginUtility.enable(plugin);
                ChatUtility.sendChat("Plug-ins have been enabled: " + plugin.getName(), ChatColor.AQUA);
            } catch (InvalidPluginException e) {
                ChatUtility.sendError("The plugin is invalid: " + plugin.getName() + "\n    " + e.getMessage());
                e.printStackTrace();
            } catch (InvalidDescriptionException e) {
                ChatUtility.sendError("Plug-in description is invalid: " + plugin.getName() + "\n    " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
