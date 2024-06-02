package me.hsgamer.topper.placeholderleaderboard.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.topper.placeholderleaderboard.Permissions;
import me.hsgamer.topper.placeholderleaderboard.TopperPlaceholderLeaderboard;
import me.hsgamer.topper.placeholderleaderboard.config.MainConfig;
import me.hsgamer.topper.placeholderleaderboard.config.MessageConfig;
import me.hsgamer.topper.placeholderleaderboard.manager.SignManager;
import me.hsgamer.topper.placeholderleaderboard.manager.SkullManager;
import me.hsgamer.topper.placeholderleaderboard.manager.TopManager;
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
        instance.get(SignManager.class).disable();
        instance.get(SkullManager.class).disable();
        instance.get(TopManager.class).disable();
        instance.get(MainConfig.class).reloadConfig();
        instance.get(MessageConfig.class).reloadConfig();
        instance.get(TopManager.class).enable();
        instance.get(SignManager.class).enable();
        instance.get(SkullManager.class).enable();
        MessageUtils.sendMessage(sender, instance.get(MessageConfig.class).getSuccess());
        return true;
    }
}
