package io.github.soju06.hotreload.utility;
/*
 * #%L
 * PlugMan
 * %%
 * Copyright (C) 2010 - 2014 PlugMan
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
import com.google.common.base.Joiner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.soju06.hotreload.HotReload;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.plugin.*;

public class PluginUtility {
    public static File getFileName(File dir, Plugin plugin) {
        if (!dir.exists() || !dir.isDirectory()) return null;
        var files = getPlugins(dir, 0);
        if (files == null) return null;
        var loader = HotReload.getInstance().getPluginLoader();
        var name = plugin.getName();
        for (File file : files) {
            try {
                if (loader.getPluginDescription(file).getName().equals(name))
                    return file;
            } catch (InvalidDescriptionException ignored) {

            }
        }
        return null;
    }

    public static void enable(Plugin plugin) {
        Bukkit.getPluginManager().enablePlugin(plugin);
    }

    public static void disable(Plugin plugin) {
        Bukkit.getPluginManager().disablePlugin(plugin);
    }

    public static PluginDescriptionFile getDescription(File file) throws InvalidDescriptionException {
        return HotReload.getInstance().getPluginLoader().getPluginDescription(file);
    }

    public static Plugin load(File file) throws InvalidPluginException, InvalidDescriptionException {
        return Bukkit.getPluginManager().loadPlugin(file);
    }

    /**
     * Reload a plugin.
     *
     * @param plugin the plugin to reload
     */
    public static void reload(Plugin plugin, File dir) throws InvalidPluginException, InvalidDescriptionException {
        unload(plugin);
        load(Objects.requireNonNull(getFileName(dir, plugin)));
    }

    public static List<File> getPlugins(File dir, int level) {
        var files = dir.listFiles();
        if (files != null) return Arrays.stream(files).filter(f->isPluginFile(f, level)).toList();
        else return null;
    }

    private static boolean isPluginFile(File file, int level) {
        if (!file.isFile()) return false;
        if (level == 0) {
            var name = file.getName();
             return "jar".equals(name.substring(name.lastIndexOf('.') + 1));
        } else {
            try {
                getDescription(file);
                return true;
            } catch (InvalidDescriptionException e) {
                return false;
            }
        }
    }

    public static List<String> getPlugins(int mode) {
        var ls = new ArrayList<String>();
        var plugins = Bukkit.getPluginManager().getPlugins();
        for (Plugin plugin : plugins) {
            var isEnabled = plugin.isEnabled();
            if (mode == 1 && !isEnabled || mode == 2 && isEnabled) continue;
            ls.add(plugin.getName());
        }
        return ls;
    }

    public static Plugin getPlugin(String name) {
        var plugins = Bukkit.getPluginManager().getPlugins();
        for (Plugin plugin : plugins) {
            if (plugin.getName().equals(name)) return plugin;
        }
        return null;
    }

    /**
     * Unload a plugin.
     *
     * @param plugin the plugin to unload
     */
    @SuppressWarnings("unchecked")
    public static void unload(Plugin plugin) {
        String name = plugin.getName();
        PluginManager pluginManager = Bukkit.getPluginManager();
        SimpleCommandMap commandMap = null;
        List<Plugin> plugins = null;
        Map<String, Plugin> names = null;
        Map<String, Command> commands = null;
        Map<Event, SortedSet<RegisteredListener>> listeners = null;

        pluginManager.disablePlugin(plugin);

        try {
            Field pluginsField = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
            pluginsField.setAccessible(true);
            plugins = (List<Plugin>) pluginsField.get(pluginManager);

            Field lookupNamesField = Bukkit.getPluginManager().getClass().getDeclaredField("lookupNames");
            lookupNamesField.setAccessible(true);
            names = (Map<String, Plugin>) lookupNamesField.get(pluginManager);

            try {
                Field listenersField = Bukkit.getPluginManager().getClass().getDeclaredField("listeners");
                listenersField.setAccessible(true);
                listeners = (Map<Event, SortedSet<RegisteredListener>>) listenersField.get(pluginManager);
            } catch (Exception ignored) {

            }

            Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (SimpleCommandMap) commandMapField.get(pluginManager);

            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            commands = (Map<String, Command>) knownCommandsField.get(commandMap);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        pluginManager.disablePlugin(plugin);

        if (plugins != null && plugins.contains(plugin))
            plugins.remove(plugin);

        if (names != null && names.containsKey(name))
            names.remove(name);

        if (listeners != null) {
            for (SortedSet<RegisteredListener> set : listeners.values()) {
                set.removeIf(value -> value.getPlugin() == plugin);
            }
        }

        if (commandMap != null) {
            for (Iterator<Map.Entry<String, Command>> it = commands.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Command> entry = it.next();
                if (entry.getValue() instanceof PluginCommand c) {
                    if (c.getPlugin() == plugin) {
                        c.unregister(commandMap);
                        it.remove();
                    }
                }
            }
        }

        // Attempt to close the classloader to unlock any handles on the plugin's jar file.
        ClassLoader cl = plugin.getClass().getClassLoader();

        if (cl instanceof URLClassLoader) {

            try {

                Field pluginField = cl.getClass().getDeclaredField("plugin");
                pluginField.setAccessible(true);
                pluginField.set(cl, null);

                Field pluginInitField = cl.getClass().getDeclaredField("pluginInit");
                pluginInitField.setAccessible(true);
                pluginInitField.set(cl, null);

            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(PluginUtility.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {

                ((URLClassLoader) cl).close();
            } catch (IOException ex) {
                Logger.getLogger(PluginUtility.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        System.gc();
    }
}
