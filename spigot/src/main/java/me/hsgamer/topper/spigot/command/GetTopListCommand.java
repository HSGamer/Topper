package me.hsgamer.topper.spigot.command;

import me.hsgamer.topper.core.common.DataEntry;
import me.hsgamer.topper.spigot.Permissions;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.config.MessageConfig;
import me.hsgamer.topper.spigot.formatter.TopFormatter;
import me.hsgamer.topper.spigot.holder.PlaceholderTopHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

import static me.hsgamer.hscore.bukkit.utils.MessageUtils.sendMessage;

public class GetTopListCommand extends Command {
    private final TopperPlugin instance;

    public GetTopListCommand(TopperPlugin instance) {
        super("gettoplist", "Get Top List", "/gettop <holder> [from_index] [to_index]", Arrays.asList("toplist", "gettop"));
        this.instance = instance;
        setPermission(Permissions.TOP.getName());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (args.length < 1) {
            sendMessage(sender, "&c" + getUsage());
            return false;
        }
        Optional<PlaceholderTopHolder> optional = instance.getTopManager().getTopHolder(args[0]);
        if (!optional.isPresent()) {
            sendMessage(sender, MessageConfig.TOP_HOLDER_NOT_FOUND.getValue());
            return false;
        }
        PlaceholderTopHolder topHolder = optional.get();
        TopFormatter topFormatter = instance.getTopManager().getTopFormatter(args[0]);

        int fromIndex = 1;
        int toIndex = 10;
        if (args.length == 2) {
            try {
                toIndex = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sendMessage(sender, MessageConfig.NUMBER_REQUIRED.getValue());
                return false;
            }
        } else if (args.length > 2) {
            try {
                fromIndex = Integer.parseInt(args[1]);
                toIndex = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sendMessage(sender, MessageConfig.NUMBER_REQUIRED.getValue());
                return false;
            }
        }
        if (fromIndex >= toIndex) {
            sendMessage(sender, MessageConfig.ILLEGAL_FROM_TO_INDEX.getValue());
            return false;
        }

        List<String> topList = new ArrayList<>();
        for (int i = fromIndex; i <= toIndex; i++) {
            Optional<DataEntry<Double>> optionalTopEntry = topHolder.getEntryByIndex(i - 1);
            UUID uuid = optionalTopEntry.map(DataEntry::getUuid).orElse(null);
            Double value = optionalTopEntry.map(DataEntry::getValue).orElse(null);
            topList.add(
                    topFormatter.replace(MessageConfig.TOP_ENTRY_LINE.getValue(), uuid, value)
                            .replace("{index}", String.valueOf(i))
            );
        }
        if (topList.isEmpty()) {
            sendMessage(sender, MessageConfig.TOP_EMPTY.getValue());
        } else {
            topList.forEach(s -> sendMessage(sender, s));
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return instance.getTopManager().getTopHolderNames().stream()
                    .filter(name -> args[0].isEmpty() || name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2 || args.length == 3) {
            return Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        }
        return Collections.emptyList();
    }
}
