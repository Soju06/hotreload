package io.github.soju06.hotreload.commands.pluginmanage.subWatch;

import io.github.soju06.hotreload.HotReload;
import io.github.soju06.hotreload.HotReloadDaemon;
import io.github.soju06.hotreload.command.CommandRouter;
import io.github.soju06.hotreload.utility.ChatUtility;
import io.github.soju06.hotreload.utility.StringUtility;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.io.IOException;
import java.util.List;

public class PluginWatchSubCommand extends CommandRouter {
    private int mode;

    public PluginWatchSubCommand(String name, int mode) {
        super(name);
        this.mode = mode;
        if (mode == 0) {
            add(new PluginWatchSubCommand("list", 1));
            add(new PluginWatchASubCommand("add", 0));
            add(new PluginWatchSubCommand("remove", 2));
        }
    }

    @Override
    public String getUsage() {
        if (mode == 1) return "Displays a plug-in list.";
        else if(mode == 2) return "Remove the plug-in from the list.";
        return "Detects the plug-in file change.";
    }

    @Override
    protected boolean hexecute(CommandSender sender, String[] args) {
        if (mode == 1) {
            var sb = new StringBuilder();
            sb.append(ChatColor.GOLD + "        WATCHED FILE LIST        " + ChatColor.RESET + "\n");
            sb.append(ChatColor.GOLD + "---------------------------------" + ChatColor.RESET + "\n");
            HotReload.getInstance().getWatchManager().forEach(daemon ->
                    sb.append(StringUtility.format("\n{}  watch: {}  rCount: {}{}{}\n    {}[{}]",
                            ChatColor.GOLD + daemon.getName() + ChatColor.RESET,
                            ChatUtility.getBool(!daemon.getError()),
                            ChatColor.LIGHT_PURPLE, daemon.getReloadCount(), ChatColor.RESET,
                            ChatColor.GOLD, daemon.getPath())));
            sb.append("\n" + ChatColor.GOLD + "---------------------------------" + ChatColor.RESET);
            sender.sendMessage(sb.toString());
            return true;
        } else if (mode == 2) {
            var name = args[0];
            var manager = HotReload.getInstance().getWatchManager();
            for (HotReloadDaemon daemon : manager) {
                if (daemon.getName().equals(name)) {
                    manager.remove(daemon);
                    try {
                        daemon.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sender.sendMessage(ChatUtility.getChat("The list has been removed:" + name, ChatColor.GREEN));
                    return true;
                }
            }
            sender.sendMessage(ChatUtility.getChat("The plugin can not be found:" + name, ChatColor.RED, "ERROR"));
        }
        return false;
    }

    @Override
    protected boolean tabComplete(List<String> tabs, String[] args) {
        if (mode == 1) return true;
        else if (mode == 2) HotReload.getInstance().getWatchManager()
                .forEach(daemon -> tabs.add(daemon.getName()));

        return false;
    }
}
