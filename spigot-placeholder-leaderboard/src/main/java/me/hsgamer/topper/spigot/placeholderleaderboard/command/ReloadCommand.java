package me.hsgamer.topper.spigot.placeholderleaderboard.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.topper.spigot.placeholderleaderboard.Permissions;
import me.hsgamer.topper.spigot.placeholderleaderboard.TopperPlaceholderLeaderboard;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class ReloadCommand extends Command {
    private final TopperPlaceholderLeaderboard instance;

    public ReloadCommand(TopperPlaceholderLeaderboard instance) {
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
        instance.getMainConfig().reloadConfig();
        instance.getMessageConfig().reloadConfig();
        instance.getTopManager().register();
        instance.getSignManager().register();
        instance.getSkullManager().register();
        MessageUtils.sendMessage(sender, instance.getMessageConfig().getSuccess());
        return true;
    }
}
