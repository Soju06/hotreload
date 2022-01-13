package io.github.soju06.hotreload.commands.pluginmanage;

import io.github.soju06.hotreload.command.RoutedExecuter;
import io.github.soju06.hotreload.commands.pluginmanage.subLoader.PluginLoadSubCommand;
import io.github.soju06.hotreload.commands.pluginmanage.subLoader.PluginReloadSubCommand;
import io.github.soju06.hotreload.commands.pluginmanage.subLoader.PluginUnloadSubCommand;
import io.github.soju06.hotreload.commands.pluginmanage.subPlugin.PluginDisableSubCommand;
import io.github.soju06.hotreload.commands.pluginmanage.subPlugin.PluginEnableSubCommand;
import io.github.soju06.hotreload.commands.pluginmanage.subPlugin.PluginListSubCommand;
import io.github.soju06.hotreload.commands.pluginmanage.subWatch.PluginWatchSubCommand;

public class HotReloadCommands extends RoutedExecuter {
    public HotReloadCommands() {
        super("hotreload");
        add(new PluginListSubCommand("list"));
        add(new PluginEnableSubCommand("enable"));
        add(new PluginDisableSubCommand("disable"));
        add(new PluginLoadSubCommand("load", 0));
        add(new PluginUnloadSubCommand("unload"));
        add(new PluginReloadSubCommand("reload"));
        add(new PluginWatchSubCommand("watch", 0));
    }
}
