package me.hsgamer.topper.spigot.command;

import me.hsgamer.topper.spigot.Permissions;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.config.MessageConfig;
import me.hsgamer.topper.spigot.manager.BlockManager;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.permissions.Permission;

public class SetTopSkullCommand extends SetTopBlockCommand {
    public SetTopSkullCommand(TopperPlugin instance) {
        super(instance, "settopskull", "Set the skull for top players");
    }

    @Override
    protected BlockManager getBlockManager() {
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
        return MessageConfig.SKULL_REQUIRED.getValue();
    }
}
