package me.hsgamer.topper.spigot.plugin.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.topper.core.DataEntry;
import me.hsgamer.topper.spigot.plugin.Permissions;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.config.MessageConfig;
import me.hsgamer.topper.spigot.plugin.holder.NumberTopHolder;
import me.hsgamer.topper.spigot.plugin.manager.TopManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;
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
        Optional<NumberTopHolder> optional = instance.get(TopManager.class).getTopHolder(args[0]);
        if (!optional.isPresent()) {
            MessageUtils.sendMessage(sender, instance.get(MessageConfig.class).getTopHolderNotFound());
            return false;
        }
        NumberTopHolder topHolder = optional.get();

        int fromIndex = 1;
        int toIndex = 10;
        if (args.length == 2) {
            try {
                toIndex = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sendMessage(sender, instance.get(MessageConfig.class).getNumberRequired());
                return false;
            }
        } else if (args.length > 2) {
            try {
                fromIndex = Integer.parseInt(args[1]);
                toIndex = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sendMessage(sender, instance.get(MessageConfig.class).getNumberRequired());
                return false;
            }
        }
        if (fromIndex >= toIndex) {
            sendMessage(sender, instance.get(MessageConfig.class).getIllegalFromToIndex());
            return false;
        }

        List<String> topList = new ArrayList<>();
        String line = instance.get(MessageConfig.class).getTopEntryLine();
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        for (int i = fromIndex; i <= toIndex; i++) {
            Optional<DataEntry<UUID, Double>> optionalTopEntry = topHolder.getSnapshotAgent().getEntryByIndex(i - 1);
            UUID uuid = optionalTopEntry.map(DataEntry::getKey).orElse(null);
            Double value = optionalTopEntry.map(DataEntry::getValue).orElse(null);
            String name = Optional.ofNullable(uuid).map(instance.getServer()::getOfflinePlayer).map(OfflinePlayer::getName).orElse(null);
            topList.add(
                    line.replace("{index}", String.valueOf(i + 1))
                            .replace("{name}", name == null ? instance.get(MessageConfig.class).getDisplayNullName() : name)
                            .replace("{uuid}", uuid == null ? instance.get(MessageConfig.class).getDisplayNullUuid() : uuid.toString())
                            .replace("{value}", value == null ? instance.get(MessageConfig.class).getDisplayNullValue() : decimalFormat.format(value))
                            .replace("{raw_value}", value == null ? instance.get(MessageConfig.class).getDisplayNullValue() : String.valueOf(value))
            );
        }
        if (topList.isEmpty()) {
            sendMessage(sender, instance.get(MessageConfig.class).getTopEmpty());
        } else {
            topList.forEach(s -> sendMessage(sender, s));
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return instance.get(TopManager.class).getTopHolderNames().stream()
                    .filter(name -> args[0].isEmpty() || name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2 || args.length == 3) {
            return Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        }
        return Collections.emptyList();
    }
}
