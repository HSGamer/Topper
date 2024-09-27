package me.hsgamer.topper.spigot.plugin.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.topper.spigot.plugin.Permissions;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.config.MainConfig;
import me.hsgamer.topper.spigot.plugin.config.MessageConfig;
import me.hsgamer.topper.spigot.plugin.manager.TopManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class ReloadCommand extends Command {
    private final TopperPlugin instance;

    public ReloadCommand(TopperPlugin instance) {
        super("reloadtop", "Reload the plugin", "/reloadtop", Collections.singletonList("rltop"));
        this.instance = instance;
        setPermission(Permissions.RELOAD.getName());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        instance.get(TopManager.class).disable();
        instance.get(MainConfig.class).reloadConfig();
        instance.get(MessageConfig.class).reloadConfig();
        instance.get(TopManager.class).enable();
        MessageUtils.sendMessage(sender, instance.get(MessageConfig.class).getSuccess());
        return true;
    }
}
