package io.github.soju06.hotreload.commands.pluginmanage.subLoader;

import io.github.soju06.hotreload.command.CommandRouter;
import io.github.soju06.hotreload.utility.ChatUtility;
import io.github.soju06.hotreload.utility.HotReloadUtility;
import io.github.soju06.hotreload.utility.PluginUtility;
import io.github.soju06.hotreload.utility.StringUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;

public class PluginReloadSubCommand extends CommandRouter {
    public PluginReloadSubCommand(String name) {
        super(name);
    }

    @Override
    public String getUsage() {
        return "Reload the plugin.";
    }

    @Override
    public boolean hexecute(CommandSender sender, String[] args) {
        var n = args.length < 1 ? null : args[0];
        var plugin = HotReloadUtility.getPlugin(n, sender);
        if (plugin == null) return false;

        var pluginsFile = Bukkit.getPluginsFolder();
        File file = PluginUtility.getFileName(pluginsFile, plugin);
        if (file == null || !file.exists()) {
            sender.sendMessage(ChatUtility.getChat(StringUtility.format(
                    "Plug-in folder {} does not exist: {}", pluginsFile,
                    file != null ? file.getName() : plugin.getName()), ChatColor.RED));
            return false;
        }

        try {
            var isEnabled = plugin.isEnabled();
            PluginUtility.unload(plugin);
            plugin = PluginUtility.load(file);
            HotReloadUtility.sendLoadDone(n, sender);
            if (!plugin.isEnabled() && isEnabled) {
                PluginUtility.enable(plugin);
                ChatUtility.sendChat("Plug-ins have been enabled: " + plugin.getName(), ChatColor.AQUA);
            }
        } catch (InvalidPluginException e) {
            HotReloadUtility.sendError(e, sender);
            e.printStackTrace();
            return false;
        } catch (InvalidDescriptionException e) {
            HotReloadUtility.sendError(e, sender);
            e.printStackTrace();
            return false;
        }

        sender.sendMessage(ChatUtility.getChat("Reload done: " + n, ChatColor.GREEN));
        return true;
    }

    @Override
    protected boolean tabComplete(List<String> tabs, String[] args) {
        tabs.addAll(PluginUtility.getPlugins(0));
        return true;
    }
}
