package io.github.soju06.hotreload;

import io.github.soju06.filesystems.watch.WatchDaemon;
import io.github.soju06.hotreload.utility.ChatUtility;
import io.github.soju06.hotreload.utility.HotReloadUtility;
import io.github.soju06.hotreload.utility.PluginUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HotReloadDaemon extends WatchDaemon {
    private long holdTime = System.currentTimeMillis();
    private Plugin plugin;
    private UUID threadId;
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
            hold_run(false, null);
        }
    }

    void hold_run(boolean schedule, @Nullable UUID threadId) {
        if (threadId != null && threadId != this.threadId) return;
        var now = System.currentTimeMillis();
        var cooldownValue = HotReload.getInstance().getConfig().get("reload-cool-down");
        var cooldown = (cooldownValue != null ? (cooldownValue instanceof Integer ? (int)cooldownValue : HotReloadUtility.tryParseInt(cooldownValue.toString(), 1)) : 1) * 1000;
        var hold = now - holdTime;
        var use = hold >= cooldown;
        if (!schedule) {
            if (hold >= cooldown * 5.7) use = false;
            holdTime = now;
            ChatUtility.sendChat(ChatColor.LIGHT_PURPLE + "plug-in files have been changed: " + plugin.getName());
            if (!use) {
                var tid = this.threadId = UUID.randomUUID();
                Bukkit.getScheduler().scheduleSyncDelayedTask(HotReload.getInstance(), ()->hold_run(true, tid), (cooldown + 200) / 50);
            }
        }

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
