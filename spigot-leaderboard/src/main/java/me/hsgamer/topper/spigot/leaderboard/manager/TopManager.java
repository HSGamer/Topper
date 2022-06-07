package me.hsgamer.topper.spigot.leaderboard.manager;

import me.hsgamer.topper.core.holder.DataHolder;
import me.hsgamer.topper.core.storage.DataStorage;
import me.hsgamer.topper.spigot.builder.TopStorageBuilder;
import me.hsgamer.topper.spigot.formatter.DataFormatter;
import me.hsgamer.topper.spigot.leaderboard.TopperLeaderboard;
import me.hsgamer.topper.spigot.leaderboard.config.MainConfig;
import me.hsgamer.topper.spigot.leaderboard.holder.NumberTopHolder;
import me.hsgamer.topper.spigot.leaderboard.holder.PlaceholderTopHolder;

import java.util.*;
import java.util.function.Function;

public class TopManager {
    private final Map<String, NumberTopHolder> topHolders = new HashMap<>();
    private final Map<String, DataFormatter> topFormatters = new HashMap<>();
    private final DataFormatter defaultFormatter = new DataFormatter();
    private final TopperLeaderboard instance;

    public TopManager(TopperLeaderboard instance) {
        this.instance = instance;
    }

    public void register() {
        Function<DataHolder<Double>, DataStorage<Double>> storageSupplier = TopStorageBuilder.build(instance);
        MainConfig.PLACEHOLDERS.getValue().forEach((key, value) -> addTopHolder(key, new PlaceholderTopHolder(instance, storageSupplier, key, value)));
        topFormatters.putAll(MainConfig.FORMATTERS.getValue());
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

    public DataFormatter getTopFormatter(String name) {
        return topFormatters.getOrDefault(name, defaultFormatter);
    }

    public void create(UUID uuid) {
        topHolders.values().forEach(holder -> holder.getOrCreateEntry(uuid));
    }
}
