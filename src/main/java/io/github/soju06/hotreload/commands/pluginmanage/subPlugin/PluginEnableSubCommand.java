package io.github.soju06.hotreload.commands.pluginmanage.subPlugin;

import io.github.soju06.hotreload.command.CommandRouter;
import io.github.soju06.hotreload.utility.ChatUtility;
import io.github.soju06.hotreload.utility.HotReloadUtility;
import io.github.soju06.hotreload.utility.PluginUtility;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class PluginEnableSubCommand extends CommandRouter {
    public PluginEnableSubCommand(String name) {
        super(name);
    }

    @Override
    public String getUsage() {
        return "Enable the plugin.";
    }

    @Override
    protected boolean hexecute(CommandSender sender, String[] args) {
        var n = args.length < 1 ? null : args[0];
        var plugin = HotReloadUtility.getPlugin(n, sender);
        if (plugin == null) return false;

        if (plugin.isEnabled()) {
            sender.sendMessage(ChatUtility.getChat("Plug-ins have already been enabled: " + n, ChatColor.GOLD));
            return true;
        }
        PluginUtility.enable(plugin);
        sender.sendMessage(ChatUtility.getChat("Plug-ins have been enabled: " + n, ChatColor.GREEN));
        return true;
    }

    @Override
    protected boolean tabComplete(List<String> tabs, String[] args) {
        tabs.addAll(PluginUtility.getPlugins(2));
        return true;
    }
}
