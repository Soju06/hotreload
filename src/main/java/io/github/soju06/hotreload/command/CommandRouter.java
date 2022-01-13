package io.github.soju06.hotreload.command;

import io.github.soju06.hotreload.utility.StringUtility;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandRouter implements ISubCommnad {
    String name = "";
    protected List<ISubCommnad> commands = new ArrayList<>();

    public CommandRouter(String name) {
        setName(name);
    }

    @Override
    @NotNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean add(ISubCommnad command) {
        return commands.add(command);
    }

    public boolean remove(ISubCommnad command) {
        return commands.add(command);
    }

    public boolean remove(String command) {
        return remove(get(command));
    }

    public ISubCommnad get(int index) {
        return commands.get(index);
    }

    public ISubCommnad get(String name) {
        for (ISubCommnad c : commands) {
            if (name.equals(c.getName())) {
                return c;
            }
        }
        return null;
    }

    protected boolean hexecute(CommandSender sender, String[] args) {
        return false;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (hexecute(sender, args)) return true;
        if (args.length < 1) {
            if (commands.size() <= 0) return false;
            var sb = new StringBuilder();
            sb.append(StringUtility.format("{}-------- {} Commands --------{}\n", ChatColor.LIGHT_PURPLE, name, ChatColor.RESET));
            commands.forEach(command -> sb.append(StringUtility.format(" - {}  {}\n", ChatColor.GOLD + command.getName(), ChatColor.RESET + command.getUsage())));
            sender.sendMessage(sb.toString());
            return false;
        }
        var cmd = get(args[0]);
        if (cmd != null) {
            return cmd.execute(sender, getSub(args));
        }
        return false;
    }

    protected boolean tabComplete(List<String> tabs, String[] args) {
        return false;
    }

    @Override
    public void getTabComplete(List<String> tabs, String[] args) {
        if (tabComplete(tabs, args)) return;
        if (args.length < 2) {
            commands.forEach(c->tabs.add(c.getName()));
            return;
        }
        var cmd = get(args[0]);
        if (cmd != null) {
            cmd.getTabComplete(tabs, getSub(args));
        }
    }

     private static String[] getSub(String[] subs) {
        var len = subs.length;
        if (len < 2) return new String[] { };
        return Arrays.copyOfRange(subs, 1, len);
    }

    @Override
    public String getUsage() {
        return "";
    }
}
