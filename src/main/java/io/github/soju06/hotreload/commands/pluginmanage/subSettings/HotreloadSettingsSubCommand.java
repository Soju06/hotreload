package io.github.soju06.hotreload.commands.pluginmanage.subSettings;

import com.google.common.primitives.UnsignedInteger;
import io.github.soju06.hotreload.HotReload;
import io.github.soju06.hotreload.command.CommandRouter;
import io.github.soju06.hotreload.utility.ChatUtility;
import io.github.soju06.hotreload.utility.HotReloadUtility;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class HotreloadSettingsSubCommand extends CommandRouter {
    int mode;
    public HotreloadSettingsSubCommand(String name, int mode) {
        super(name);
        this.mode = mode;
        if (mode == 0) {
            add(new HotreloadSettingsSubCommand("cooldown", 1));
        }
    }

    @Override
    public String getUsage() {
        if (mode == 0) return "Hotreload settings";
        else if (mode == 1) return "Reload cool down (seconds)";
        else return null;
    }

    @Override
    protected boolean hexecute(CommandSender sender, String[] args) {
        if (mode == 1) {
            if(args.length < 1) {
                sender.sendMessage(ChatUtility.getChat("Current value: " + HotReload.getInstance().getConfig().get("reload-cool-down")));
            } else {
                var value = HotReloadUtility.tryParseInt(args[0], 1);
                HotReload.getInstance().getConfig().set("reload-cool-down", value);
                HotReload.getInstance().saveConfig();
                sender.sendMessage(ChatUtility.getChat("Now value: " + value));
            }
            return true;
        }
        return false;
    }
}
