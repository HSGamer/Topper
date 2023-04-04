package me.hsgamer.topper.spigot.placeholderleaderboard.command;

import me.hsgamer.topper.spigot.placeholderleaderboard.Permissions;
import me.hsgamer.topper.spigot.placeholderleaderboard.TopperPlaceholderLeaderboard;
import me.hsgamer.topper.spigot.placeholderleaderboard.manager.SignManager;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.permissions.Permission;

public class SetTopSignCommand extends SetTopBlockCommand {
    public SetTopSignCommand(TopperPlaceholderLeaderboard instance) {
        super(instance, "settopsign", "Set the sign for top players");
    }

    @Override
    protected SignManager getBlockManager() {
        return instance.getSignManager();
    }

    @Override
    protected Permission getRequiredPermission() {
        return Permissions.SIGN;
    }

    @Override
    protected boolean isValidBlock(Block block) {
        return block.getState() instanceof Sign;
    }

    @Override
    protected String getBlockRequiredMessage() {
        return instance.getMessageConfig().getSignRequired();
    }
}
