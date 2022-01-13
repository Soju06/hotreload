package io.github.soju06.hotreload.commands.pluginmanage.subLoader;

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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PluginLoadSubCommand extends CommandRouter {
    private final int mode;

    public PluginLoadSubCommand(String name, int mode) {
        super(name);
        this.mode = mode;
        if(mode == 0) {
            add(new PluginLoadSubCommand("plugin", 1));
            add(new PluginLoadSubCommand("path", 2));
        }
    }

    @Override
    public String getUsage() {
        return "Load the plugin.";
    }

    @Override
    public boolean hexecute(CommandSender sender, String[] args) {
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
                if (PluginUtility.getPlugin(PluginUtility.getDescription(file).getName()) != null) {
                    sender.sendMessage(ChatUtility.getChat("There is a plugin with the same name.",
                            ChatColor.RED, "ERROR"));
                    return false;
                }
                PluginUtility.load(file);
                HotReloadUtility.sendLoadDone(name, sender);

                return true;
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
        return false;
    }

    @Override
    protected boolean tabComplete(List<String> tabs, String[] args) {
        if (mode == 0 && args.length <= 1) {
            return false;
        } else if (mode == 1) {
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
