package io.github.soju06.hotreload.utility;

import io.github.soju06.hotreload.HotReload;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ChatUtility {
    public static final String BOOL_TRUE = ChatColor.GREEN + "true" + ChatColor.RESET;
    public static final String BOOL_FALSE = ChatColor.RED + "false" + ChatColor.RESET;

    public static  String getBool(boolean value) {
        if (value) return BOOL_TRUE;
        else return BOOL_FALSE;
    }

    public static String getChat(String message) {
        return getChat(message, ChatColor.RESET, null);
    }

    public static String getChat(String message, String sub) {
        return getChat(message, ChatColor.RESET);
    }

    public static String getChat(String message, ChatColor color) {
        return getChat(message, color, null);
    }

    public static String getChat(String message, ChatColor color, String sub) {
        return ChatColor.YELLOW + "[HotReoad" + (sub != null ? " " + sub : "") + "] " +  color + message;
    }

    public static int sendChat(String message) {
        return HotReload.getInstance().sendAdminChat(getChat(message));
    }

    public static int sendChat(String message, ChatColor color) {
        return HotReload.getInstance().sendAdminChat(getChat(message, color));
    }

    public static int sendError(String message) {
        return HotReload.getInstance().sendAdminChat(getChat(message, ChatColor.RED, "ERROR"));
    }

    public static List<String> spaceTabComplete(String current, List<String> subs) {
        var paths = new ArrayList<String>();
        var spaceCount = (int) current.chars().filter(c -> c == ' ').count();
        for (String sub : subs) {
            paths.add(sub.substring(spaceIndexOf(sub, spaceCount, 0) + 1));
        }
        return paths;
    }

    private static int spaceIndexOf(String str, int count, int index) {
        if (count-- <= 0) return -1;
        var i = str.indexOf(' ', index);
        if (count > 0) return spaceIndexOf(str, count, i + 1);
        else return i;
    }
}
