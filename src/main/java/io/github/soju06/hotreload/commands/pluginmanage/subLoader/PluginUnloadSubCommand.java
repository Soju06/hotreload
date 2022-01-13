package io.github.soju06.hotreload.commands.pluginmanage.subLoader;

import io.github.soju06.hotreload.command.CommandRouter;
import io.github.soju06.hotreload.utility.ChatUtility;
import io.github.soju06.hotreload.utility.HotReloadUtility;
import io.github.soju06.hotreload.utility.PluginUtility;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class PluginUnloadSubCommand extends CommandRouter {
    public PluginUnloadSubCommand(String name) {
        super(name);
    }

    @Override
    public String getUsage() {
        return "Unload the plugin.";
    }

    @Override
    public boolean hexecute(CommandSender sender, String[] args) {
        var n = args.length < 1 ? null : args[0];
        var plugin = HotReloadUtility.getPlugin(n, sender);
        if (plugin == null) return false;

        PluginUtility.unload(plugin);
        sender.sendMessage(ChatUtility.getChat("Unload done: " + n, ChatColor.GREEN));
        return true;
    }

    @Override
    protected boolean tabComplete(List<String> tabs, String[] args) {
        tabs.addAll(PluginUtility.getPlugins(0));
        return true;
    }
}
