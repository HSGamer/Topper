package me.hsgamer.topper.placeholderleaderboard.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.topper.core.storage.DataStorageSupplier;
import me.hsgamer.topper.placeholderleaderboard.TopperPlaceholderLeaderboard;
import me.hsgamer.topper.placeholderleaderboard.config.MainConfig;
import me.hsgamer.topper.placeholderleaderboard.holder.NumberTopHolder;
import me.hsgamer.topper.placeholderleaderboard.holder.PlaceholderTopHolder;
import me.hsgamer.topper.spigot.number.NumberFormatter;
import me.hsgamer.topper.spigot.number.NumberStorageBuilder;

import java.util.*;

public class TopManager implements Loadable {
    private final Map<String, NumberTopHolder> topHolders = new HashMap<>();
    private final Map<String, NumberFormatter> topFormatters = new HashMap<>();
    private final NumberFormatter defaultFormatter = new NumberFormatter();
    private final TopperPlaceholderLeaderboard instance;
    private DataStorageSupplier<Double> storageSupplier;

    public TopManager(TopperPlaceholderLeaderboard instance) {
        this.instance = instance;
    }

    @Override
    public void enable() {
        storageSupplier = instance.get(NumberStorageBuilder.class).buildSupplier(instance.get(MainConfig.class).getStorageType());
        storageSupplier.enable();
        instance.get(MainConfig.class).getPlaceholders().forEach((key, value) -> addTopHolder(key, new PlaceholderTopHolder(instance, key, value)));
        topFormatters.putAll(instance.get(MainConfig.class).getFormatters());
    }

    @Override
    public void disable() {
        topHolders.values().forEach(NumberTopHolder::unregister);
        topHolders.clear();
        topFormatters.clear();
        storageSupplier.disable();
    }

    public void addTopHolder(String key, NumberTopHolder topHolder) {
        topHolder.register();
        NumberTopHolder oldHolder = topHolders.put(key, topHolder);
        if (oldHolder != null) oldHolder.unregister();
    }

    public DataStorageSupplier<Double> getStorageSupplier() {
        return storageSupplier;
    }

    public Optional<NumberTopHolder> getTopHolder(String name) {
        return Optional.ofNullable(topHolders.get(name));
    }

    public List<String> getTopHolderNames() {
        return Collections.unmodifiableList(new ArrayList<>(topHolders.keySet()));
    }

    public NumberFormatter getTopFormatter(String name) {
        return topFormatters.getOrDefault(name, defaultFormatter);
    }

    public void create(UUID uuid) {
        topHolders.values().forEach(holder -> holder.getOrCreateEntry(uuid));
    }
}
