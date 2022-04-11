package me.hsgamer.topper.spigot.sign;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.topper.core.TopEntry;
import me.hsgamer.topper.core.TopFormatter;
import me.hsgamer.topper.core.TopHolder;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.config.SignConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.plugin.java.JavaPlugin;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SignEntry {
    public final Location location;
    public final String topHolderName;
    public final int index;
    private final TopperPlugin instance;

    public SignEntry(Location location, String topHolderName, int index) {
        this.instance = JavaPlugin.getPlugin(TopperPlugin.class);
        this.location = location;
        this.topHolderName = topHolderName;
        this.index = index;
    }

    public static SignEntry deserialize(String rawValue) {
        String[] split = rawValue.split(",");
        Location location = new Location(
                Bukkit.getWorld(split[0]),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Double.parseDouble(split[3])
        );
        String topHolderName = split[4];
        int index = Integer.parseInt(split[5]);
        return new SignEntry(location, topHolderName, index);
    }

    public void update() {
        Optional<TopHolder> optional = instance.getTopManager().getTopHolder(topHolderName);
        if (!optional.isPresent()) return;
        TopHolder topHolder = optional.get();
        TopFormatter formatter = instance.getTopManager().getTopFormatter(topHolderName);
        Optional<TopEntry> optionalEntry = topHolder.getEntryByIndex(index);
        UUID uuid = optionalEntry.map(TopEntry::getUuid).orElse(null);
        BigDecimal value = optionalEntry.map(TopEntry::getValue).orElse(null);

        Block block = location.getBlock();
        if (!block.getChunk().isLoaded()) return;

        BlockState blockState = block.getState();
        if (blockState instanceof Sign) {
            Sign sign = (Sign) blockState;
            String[] lines = getSignLines(uuid, value, formatter);
            for (int i = 0; i < 4; i++) {
                sign.setLine(i, lines[i]);
            }
            sign.update(false, false);
        }
    }

    private String[] getSignLines(UUID uuid, BigDecimal value, TopFormatter formatter) {
        List<String> list = SignConfig.SIGN_LINES.getValue();
        int startIndex = SignConfig.START_INDEX.getValue();
        list.replaceAll(s ->
                formatter.replace(s, uuid, value).replace("{index}", String.valueOf(index + startIndex))
        );
        String[] lines = new String[4];
        for (int i = 0; i < 4; i++) {
            lines[i] = MessageUtils.colorize(i < list.size() ? list.get(i) : "");
        }
        return lines;
    }

    public String serialize() {
        return String.join(",",
                location.getWorld().getName(),
                Double.toString(location.getX()),
                Double.toString(location.getY()),
                Double.toString(location.getZ()),
                topHolderName,
                String.valueOf(index)
        );
    }
}
