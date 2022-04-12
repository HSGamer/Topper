package me.hsgamer.topper.spigot.command;

import me.hsgamer.topper.spigot.Permissions;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.config.MessageConfig;
import me.hsgamer.topper.spigot.manager.BlockManager;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.permissions.Permission;

public class SetTopSignCommand extends SetTopBlockCommand {
    public SetTopSignCommand(TopperPlugin instance) {
        super(instance, "settopsign", "Set the sign for top players");
    }

    @Override
    protected BlockManager getBlockManager() {
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
        return MessageConfig.SIGN_REQUIRED.getValue();
    }
}
