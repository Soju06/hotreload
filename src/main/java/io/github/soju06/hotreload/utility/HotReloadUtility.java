package io.github.soju06.hotreload.utility;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HotReloadUtility {
    public static Plugin getPlugin(String name, CommandSender sender) {
        var plugin = PluginUtility.getPlugin(name);
        if (plugin == null) {
            if (sender != null) sender.sendMessage(ChatUtility.getChat(
                    "The plugin can not be found: " + name, ChatColor.RED, "ERROR"));
            return null;
        }
        return plugin;
    }

    public static void sendError(InvalidPluginException e, CommandSender sender) {
        sender.sendMessage(ChatUtility.getChat(StringUtility.format(
                "The plug-in file is invalid.\n{}", e), ChatColor.RED, "ERROR"));
    }

    public static void sendError(InvalidDescriptionException e, CommandSender sender) {
        sender.sendMessage(ChatUtility.getChat(StringUtility.format(
                "The plug-in description is invalid.\n{}", e), ChatColor.RED, "ERROR"));
    }

    public static void sendLoadDone(String name, CommandSender sender) {
        sender.sendMessage(ChatUtility.getChat("Plug-in has been loaded: " + name, ChatColor.GREEN));
    }

    public static boolean commandExplorer(List<String> tabs, String[] args) {
        var path = "";
        if (args.length < 1 || args[0].trim().length() <= 0) path = "./";
        else path = String.join(" ", args);

        var file = new File(path);
        if (file.isFile())
            tabs.addAll(ChatUtility.spaceTabComplete(path, List.of(file.getPath())));
        else {
            if (!file.isDirectory()) {
                try {
                    Paths.get(path);
                    file = new File(path).getParentFile();
                    if (!file.isDirectory()) return false;
                } catch (Exception e) {
                    return false;
                }
            }
            var files = file.listFiles();
            if (files != null) {
                var paths = new ArrayList<String>();
                for (File f : files) {
                    var name = f.getName();
                    if (f.isDirectory() || (f.isFile() && name.substring(name.lastIndexOf('.') + 1).equals("jar")))
                        paths.add(f.getPath());
                }
                tabs.addAll(ChatUtility.spaceTabComplete(path, paths));
                return true;
            }
        }
        return false;
    }

    public static int tryParseInt(String value, int defaultVal) {
        try {
            return Integer.parseUnsignedInt(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}
