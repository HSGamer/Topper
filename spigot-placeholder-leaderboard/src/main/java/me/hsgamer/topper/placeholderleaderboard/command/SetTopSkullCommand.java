package me.hsgamer.topper.placeholderleaderboard.command;

import me.hsgamer.topper.placeholderleaderboard.Permissions;
import me.hsgamer.topper.placeholderleaderboard.TopperPlaceholderLeaderboard;
import me.hsgamer.topper.placeholderleaderboard.config.MessageConfig;
import me.hsgamer.topper.placeholderleaderboard.manager.SkullManager;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.permissions.Permission;

public class SetTopSkullCommand extends SetTopBlockCommand {
    public SetTopSkullCommand(TopperPlaceholderLeaderboard instance) {
        super(instance, "settopskull", "Set the skull for top players");
    }

    @Override
    protected SkullManager getBlockManager() {
        return instance.get(SkullManager.class);
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
        return instance.get(MessageConfig.class).getSkullRequired();
    }
}
