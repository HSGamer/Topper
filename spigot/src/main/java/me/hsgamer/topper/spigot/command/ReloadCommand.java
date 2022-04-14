package me.hsgamer.topper.spigot.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.topper.spigot.Permissions;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.config.MessageConfig;
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
        instance.getSignManager().unregister();
        instance.getSkullManager().unregister();
        instance.getTopManager().unregister();
        instance.getMainConfig().reload();
        instance.getMessageConfig().reload();
        instance.getDatabaseConfig().reload();
        instance.getTopManager().register();
        instance.getSignManager().register();
        instance.getSkullManager().register();
        MessageUtils.sendMessage(sender, MessageConfig.SUCCESS.getValue());
        return true;
    }
}
