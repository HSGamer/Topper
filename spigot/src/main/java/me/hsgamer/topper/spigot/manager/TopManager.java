package me.hsgamer.topper.spigot.manager;

import me.hsgamer.topper.core.storage.DataStorage;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.builder.TopStorageBuilder;
import me.hsgamer.topper.spigot.config.MainConfig;
import me.hsgamer.topper.spigot.formatter.TopFormatter;
import me.hsgamer.topper.spigot.holder.NumberTopHolder;
import me.hsgamer.topper.spigot.holder.PlaceholderTopHolder;
import me.hsgamer.topper.spigot.storage.YamlStorage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;

public class TopManager {
    private final Map<String, NumberTopHolder> topHolders = new HashMap<>();
    private final Map<String, TopFormatter> topFormatters = new HashMap<>();
    private final TopFormatter defaultFormatter = new TopFormatter();
    private final TopperPlugin instance;

    public TopManager(TopperPlugin instance) {
        this.instance = instance;
    }

    public void register() {
        MainConfig.PLACEHOLDERS.getValue().forEach((key, value) -> {
            DataStorage<Double> storage = TopStorageBuilder.INSTANCE.build(MainConfig.STORAGE_TYPE.getValue(), instance).orElseGet(YamlStorage::new);
            addTopHolder(key, new PlaceholderTopHolder(instance, storage, key, value));
        });
        TopFormatter.setNullValueSupplier(MainConfig.NULL_DISPLAY_VALUE::getValue);
        defaultFormatter.addReplacer("name", (uuid, bigDecimal) -> Optional.ofNullable(uuid).map(Bukkit::getOfflinePlayer).map(OfflinePlayer::getName).orElseGet(MainConfig.NULL_DISPLAY_NAME::getValue));
        topFormatters.putAll(MainConfig.FORMATTERS.getValue());
        topFormatters.values().forEach(topFormatter -> topFormatter.addReplacer("name", (uuid, bigDecimal) -> Optional.ofNullable(uuid).map(Bukkit::getOfflinePlayer).map(OfflinePlayer::getName).orElseGet(MainConfig.NULL_DISPLAY_NAME::getValue)));
    }

    public void unregister() {
        topHolders.values().forEach(NumberTopHolder::unregister);
        topHolders.clear();
        topFormatters.clear();
    }

    public void addTopHolder(String key, PlaceholderTopHolder topHolder) {
        if (topHolders.containsKey(key)) {
            topHolders.get(key).unregister();
        }
        topHolder.register();
        topHolders.put(key, topHolder);
    }

    public Optional<NumberTopHolder> getTopHolder(String name) {
        return Optional.ofNullable(topHolders.get(name));
    }

    public List<String> getTopHolderNames() {
        return Collections.unmodifiableList(new ArrayList<>(topHolders.keySet()));
    }

    public TopFormatter getTopFormatter(String name) {
        return topFormatters.getOrDefault(name, defaultFormatter);
    }

    public void create(UUID uuid) {
        topHolders.values().forEach(holder -> holder.getOrCreateEntry(uuid));
    }
}
