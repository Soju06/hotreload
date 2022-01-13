package io.github.soju06.hotreload.commands.pluginmanage.subPlugin;

import io.github.soju06.hotreload.command.CommandRouter;
import io.github.soju06.hotreload.utility.ChatUtility;
import io.github.soju06.hotreload.utility.HotReloadUtility;
import io.github.soju06.hotreload.utility.PluginUtility;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class PluginDisableSubCommand extends CommandRouter {
    public PluginDisableSubCommand(String name) {
        super(name);
    }

    @Override
    public String getUsage() {
        return "Disable the plugin.";
    }

    @Override
    protected boolean hexecute(CommandSender sender, String[] args) {
        var n = args.length < 1 ? null : args[0];
        var plugin = HotReloadUtility.getPlugin(n, sender);
        if (plugin == null) return false;

        if (!plugin.isEnabled()) {
            sender.sendMessage(ChatUtility.getChat("Plug-ins have already been disabled: " + n, ChatColor.GOLD));
            return true;
        }
        PluginUtility.disable(plugin);
        sender.sendMessage(ChatUtility.getChat("Plug-ins have been disabled: " + n, ChatColor.GREEN));
        return true;
    }

    @Override
    protected boolean tabComplete(List<String> tabs, String[] args) {
        tabs.addAll(PluginUtility.getPlugins(1));
        return true;
    }
}
