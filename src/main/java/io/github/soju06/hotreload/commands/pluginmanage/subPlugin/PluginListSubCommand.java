package io.github.soju06.hotreload.commands.pluginmanage.subPlugin;

import io.github.soju06.hotreload.command.CommandRouter;
import io.github.soju06.hotreload.utility.StringUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PluginListSubCommand extends CommandRouter {
    public PluginListSubCommand(String name) {
        super(name);
    }

    @Override
    public String getUsage() {
        return "Displays the loaded plug-in list.";
    }

    @Override
    protected boolean hexecute(CommandSender sender, String[] args) {
        var sb = new StringBuilder();
        var plugins = Bukkit.getPluginManager().getPlugins();

        sb.append(ChatColor.GOLD + "           PLUGIN LIST           " + ChatColor.RESET + "\n");
        sb.append(ChatColor.GOLD + "---------------------------------" + ChatColor.RESET + "\n");
        for (var plugin : plugins) {
            var des = plugin.getDescription();
            sb.append(StringUtility.format("\n - {} {}{} {}ver {}\n  {}[{}]\n{}",
                    (plugin.isEnabled() ? ChatColor.GREEN + "Enabled " : ChatColor.RED + "Disabled"),
                    ChatColor.RESET, plugin.getName(), ChatColor.GOLD, des.getVersion(),
                    ChatColor.GRAY, des.getDescription(), ChatColor.RESET));
        }
        sb.append(ChatColor.GOLD + "---------------------------------" + ChatColor.RESET);

        sender.sendMessage(sb.toString());
        return true;
    }
}
