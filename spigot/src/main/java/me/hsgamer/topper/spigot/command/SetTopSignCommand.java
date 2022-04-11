package me.hsgamer.topper.spigot.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.topper.spigot.Permissions;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.config.MessageConfig;
import me.hsgamer.topper.spigot.sign.SignEntry;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SetTopSignCommand extends Command {
    private final TopperPlugin instance;

    public SetTopSignCommand(TopperPlugin instance) {
        super("settopsign", "Set the sign for top players", "/settopsign <top_holder> <index>", Collections.singletonList("topsign"));
        this.instance = instance;
        setPermission(Permissions.SIGN.getName());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, MessageConfig.PLAYER_ONLY.getValue());
            return false;
        }
        if (args.length < 2) {
            MessageUtils.sendMessage(sender, "&c" + getUsage());
            return false;
        }
        if (!instance.getTopManager().getTopHolder(args[0]).isPresent()) {
            MessageUtils.sendMessage(sender, MessageConfig.TOP_HOLDER_NOT_FOUND.getValue());
            return false;
        }
        int index;
        try {
            index = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            MessageUtils.sendMessage(sender, MessageConfig.NUMBER_REQUIRED.getValue());
            return false;
        }
        Block block = ((Player) sender).getTargetBlock(null, 5);
        if (block == null || !(block.getState() instanceof Sign)) {
            MessageUtils.sendMessage(sender, MessageConfig.SIGN_REQUIRED.getValue());
            return false;
        }
        instance.getSignManager().addSign(new SignEntry(block.getLocation(), args[0], index));
        MessageUtils.sendMessage(sender, MessageConfig.SUCCESS.getValue());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
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
