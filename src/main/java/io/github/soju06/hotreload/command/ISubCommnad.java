package io.github.soju06.hotreload.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface ISubCommnad {
    String getName();

    boolean execute(CommandSender sender, String[] args);

    void getTabComplete(List<String> tabs, String[] args);

    String getUsage();
}