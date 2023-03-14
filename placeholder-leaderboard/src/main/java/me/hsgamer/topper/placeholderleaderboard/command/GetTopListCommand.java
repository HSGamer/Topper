package me.hsgamer.topper.placeholderleaderboard.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.topper.core.entry.DataEntry;
import me.hsgamer.topper.placeholderleaderboard.Permissions;
import me.hsgamer.topper.placeholderleaderboard.TopperPlaceholderLeaderboard;
import me.hsgamer.topper.placeholderleaderboard.holder.NumberTopHolder;
import me.hsgamer.topper.spigot.formatter.NumberFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

import static me.hsgamer.hscore.bukkit.utils.MessageUtils.sendMessage;

public class GetTopListCommand extends Command {
    private final TopperPlaceholderLeaderboard instance;

    public GetTopListCommand(TopperPlaceholderLeaderboard instance) {
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
        Optional<NumberTopHolder> optional = instance.getTopManager().getTopHolder(args[0]);
        if (!optional.isPresent()) {
            MessageUtils.sendMessage(sender, instance.getMessageConfig().getTopHolderNotFound());
            return false;
        }
        NumberTopHolder topHolder = optional.get();
        NumberFormatter numberFormatter = instance.getTopManager().getTopFormatter(args[0]);

        int fromIndex = 1;
        int toIndex = 10;
        if (args.length == 2) {
            try {
                toIndex = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sendMessage(sender, instance.getMessageConfig().getNumberRequired());
                return false;
            }
        } else if (args.length > 2) {
            try {
                fromIndex = Integer.parseInt(args[1]);
                toIndex = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sendMessage(sender, instance.getMessageConfig().getNumberRequired());
                return false;
            }
        }
        if (fromIndex >= toIndex) {
            sendMessage(sender, instance.getMessageConfig().getIllegalFromToIndex());
            return false;
        }

        List<String> topList = new ArrayList<>();
        for (int i = fromIndex; i <= toIndex; i++) {
            Optional<DataEntry<Double>> optionalTopEntry = topHolder.getSnapshotAgent().getEntryByIndex(i - 1);
            UUID uuid = optionalTopEntry.map(DataEntry::getUuid).orElse(null);
            Double value = optionalTopEntry.map(DataEntry::getValue).orElse(null);
            topList.add(
                    numberFormatter.replace(instance.getMessageConfig().getTopEntryLine(), uuid, value)
                            .replace("{index}", String.valueOf(i))
            );
        }
        if (topList.isEmpty()) {
            sendMessage(sender, instance.getMessageConfig().getTopEmpty());
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
