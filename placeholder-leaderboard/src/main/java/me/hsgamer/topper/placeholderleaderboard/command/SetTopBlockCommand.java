package me.hsgamer.topper.placeholderleaderboard.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.topper.placeholderleaderboard.TopperPlaceholderLeaderboard;
import me.hsgamer.topper.spigot.block.BlockEntry;
import me.hsgamer.topper.spigot.block.BlockManager;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static me.hsgamer.hscore.bukkit.utils.MessageUtils.sendMessage;

public abstract class SetTopBlockCommand extends Command {
    protected final TopperPlaceholderLeaderboard instance;

    protected SetTopBlockCommand(TopperPlaceholderLeaderboard instance, String name, String description) {
        super(name, description, "/" + name + " <holder> <index>", Collections.emptyList());
        this.instance = instance;
        setPermission(getRequiredPermission().getName());
    }

    protected abstract BlockManager<Double> getBlockManager();

    protected abstract Permission getRequiredPermission();

    protected abstract boolean isValidBlock(Block block);

    protected abstract String getBlockRequiredMessage();

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, instance.getMessageConfig().getPlayerOnly());
            return false;
        }
        if (args.length < 2) {
            sendMessage(sender, "&c" + getUsage());
            return false;
        }
        if (!instance.getTopManager().getTopHolder(args[0]).isPresent()) {
            sendMessage(sender, instance.getMessageConfig().getTopHolderNotFound());
            return false;
        }
        int index;
        try {
            index = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sendMessage(sender, instance.getMessageConfig().getNumberRequired());
            return false;
        }
        Block block = ((Player) sender).getTargetBlock(null, 5);
        if (block == null || !isValidBlock(block)) {
            sendMessage(sender, getBlockRequiredMessage());
            return false;
        }
        getBlockManager().add(new BlockEntry(block.getLocation(), args[0], index - 1));
        sendMessage(sender, instance.getMessageConfig().getSuccess());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return instance.getTopManager().getTopHolderNames().stream()
                    .filter(name -> args[0].isEmpty() || name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            return Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        }
        return Collections.emptyList();
    }
}
