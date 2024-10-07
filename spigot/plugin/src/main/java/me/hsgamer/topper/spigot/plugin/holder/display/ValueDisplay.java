package me.hsgamer.topper.spigot.plugin.holder.display;

import me.hsgamer.topper.spigot.plugin.holder.NumberTopHolder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ValueDisplay {
    private final NumberTopHolder holder;
    private final String line;
    private final String displayNullName;
    private final String displayNullUuid;
    private final String displayNullValue;

    public ValueDisplay(NumberTopHolder holder, Map<String, Object> map) {
        this.holder = holder;
        this.line = Optional.ofNullable(map.get("line"))
                .map(Object::toString)
                .orElse("&7[&b{index}&7] &b{name} &7- &b{value}");
        this.displayNullName = Optional.ofNullable(map.get("null-name"))
                .map(Object::toString)
                .orElse("---");
        this.displayNullUuid = Optional.ofNullable(map.get("null-uuid"))
                .map(Object::toString)
                .orElse("---");
        this.displayNullValue = Optional.ofNullable(map.get("null-value"))
                .map(Object::toString)
                .orElse("---");
    }

    public String getDisplayUuid(@Nullable UUID uuid) {
        return uuid != null ? uuid.toString() : displayNullUuid;
    }

    public String getDisplayName(@Nullable UUID uuid) {
        return Optional.ofNullable(uuid)
                .map(Bukkit::getOfflinePlayer)
                .map(OfflinePlayer::getName)
                .orElse(displayNullName);
    }

    public String getDisplayValue(@Nullable Double value, boolean raw) {
        return Optional.ofNullable(value)
                .map(v -> raw ? String.valueOf(v) : new DecimalFormat("#.##").format(v))
                .orElse(displayNullValue);
    }

    public String getDisplayLine(int index) {
        Map.Entry<UUID, Double> dataSnapshot = holder.getSnapshotAgent().getSnapshotByIndex(index).orElse(null);
        return line
                .replace("{index}", String.valueOf(index))
                .replace("{uuid}", getDisplayUuid(dataSnapshot == null ? null : dataSnapshot.getKey()))
                .replace("{name}", getDisplayName(dataSnapshot == null ? null : dataSnapshot.getKey()))
                .replace("{value}", getDisplayValue(dataSnapshot == null ? null : dataSnapshot.getValue(), true))
                .replace("{raw_value}", getDisplayValue(dataSnapshot == null ? null : dataSnapshot.getValue(), false));
    }
}
