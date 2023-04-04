package me.hsgamer.topper.spigot.placeholderleaderboard.command;

import me.hsgamer.topper.spigot.placeholderleaderboard.Permissions;
import me.hsgamer.topper.spigot.placeholderleaderboard.TopperPlaceholderLeaderboard;
import me.hsgamer.topper.spigot.placeholderleaderboard.manager.SkullManager;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.permissions.Permission;

public class SetTopSkullCommand extends SetTopBlockCommand {
    public SetTopSkullCommand(TopperPlaceholderLeaderboard instance) {
        super(instance, "settopskull", "Set the skull for top players");
    }

    @Override
    protected SkullManager getBlockManager() {
        return instance.getSkullManager();
    }

    @Override
    protected Permission getRequiredPermission() {
        return Permissions.SKULL;
    }

    @Override
    protected boolean isValidBlock(Block block) {
        return block.getState() instanceof Skull;
    }

    @Override
    protected String getBlockRequiredMessage() {
        return instance.getMessageConfig().getSkullRequired();
    }
}
