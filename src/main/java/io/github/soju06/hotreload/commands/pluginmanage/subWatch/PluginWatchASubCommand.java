package io.github.soju06.hotreload.commands.pluginmanage.subWatch;

import io.github.soju06.hotreload.HotReload;
import io.github.soju06.hotreload.HotReloadDaemon;
import io.github.soju06.hotreload.command.CommandRouter;
import io.github.soju06.hotreload.utility.ChatUtility;
import io.github.soju06.hotreload.utility.HotReloadUtility;
import io.github.soju06.hotreload.utility.PluginUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PluginWatchASubCommand extends CommandRouter {
    private int mode;
    public PluginWatchASubCommand(String name, int mode) {
        super(name);
        this.mode = mode;
        if (mode == 0) {
            add(new PluginWatchASubCommand("plugin", 1));
            add(new PluginWatchASubCommand("path", 2));
        }
    }

    @Override
    public String getUsage() {
        if (mode == 1) return "Add a plugin in the plugins folder.";
        else if (mode == 2) return "Add the plugin for that path.";
        return "Add a plugin to the plug-in list.";
    }

    @Override
    protected boolean hexecute(CommandSender sender, String[] args) {
        if (mode == 1 || mode == 2) {
            var name = String.join(" ", args);
            File file = null;
            if (mode == 1) file = new File(Bukkit.getPluginsFolder(), name);
            else file = new File(name);

            if (!file.isFile()) {
                sender.sendMessage(ChatUtility.getChat("The plugin file could not be found: " + name, ChatColor.RED, "ERROR"));
                return false;
            }

            try {
                var manager = HotReload.getInstance().getWatchManager();
                var path = file.getPath();
                if (manager.stream().anyMatch(daemon -> daemon.getPath().equals(path))) {
                    sender.sendMessage(ChatUtility.getChat("The plugin is already in the list: " + name, ChatColor.RED, "ERROR"));
                    return false;
                }
                var plugin = PluginUtility.getPlugin(PluginUtility.getDescription(file).getName());
                if (plugin == null) {
                    try {
                        plugin = PluginUtility.load(file);
                        HotReloadUtility.sendLoadDone(name, sender);
                    } catch (InvalidPluginException e) {
                        HotReloadUtility.sendError(e, sender);
                        e.printStackTrace();
                        return false;
                    } catch (InvalidDescriptionException e) {
                        HotReloadUtility.sendError(e, sender);
                        e.printStackTrace();
                        return false;
                    }
                }
                var daemon = new HotReloadDaemon(plugin, file);
                manager.add(daemon);
                sender.sendMessage(ChatUtility.getChat("Plug-ins have been added to the list: " + name, ChatColor.LIGHT_PURPLE));
                if (!plugin.isEnabled()) {
                    PluginUtility.enable(plugin);
                    sender.sendMessage(ChatUtility.getChat("Plug-ins have been enabled: " + plugin.getName(), ChatColor.AQUA));
                }
                return true;
            } catch (InvalidDescriptionException e) {
                HotReloadUtility.sendError(e, sender);
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    protected boolean tabComplete(List<String> tabs, String[] args) {
        if (mode == 0 && args.length <= 1) return false;
        else if (mode == 1) {
            var plugins = PluginUtility.getPlugins(Bukkit.getPluginsFolder(), 0);
            if (plugins != null) {
                var files = new ArrayList<String>();
                plugins.forEach(p->files.add(p.getName()));
                tabs.addAll(ChatUtility.spaceTabComplete(String.join(" ", args), files));
            }
            return true;
        } else if (mode == 2) {
            HotReloadUtility.commandExplorer(tabs, args);
            return true;
        }
        return false;
    }
}