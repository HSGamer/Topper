package me.hsgamer.topper.spigot.sign;

import me.hsgamer.topper.core.TopEntry;
import me.hsgamer.topper.core.TopFormatter;
import me.hsgamer.topper.core.TopHolder;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.config.SignConfig;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.plugin.java.JavaPlugin;

import java.math.BigDecimal;
import java.util.*;

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

    public static SignEntry deserialize(Map<String, Object> args) {
        return new SignEntry(Location.deserialize(args), (String) args.get("holder"), (int) args.get("index"));
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
            lines[i] = i < list.size() ? list.get(i) : "";
        }
        return lines;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>(location.serialize());
        map.put("holder", topHolderName);
        map.put("index", index);
        return map;
    }
}
