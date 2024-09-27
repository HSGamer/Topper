package me.hsgamer.topper.spigot.plugin.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.topper.agent.storage.simple.supplier.DataStorageSupplier;
import me.hsgamer.topper.spigot.agent.storage.number.NumberStorageBuilder;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.config.MainConfig;
import me.hsgamer.topper.spigot.plugin.holder.NumberTopHolder;
import me.hsgamer.topper.spigot.plugin.provider.PlaceholderValueProvider;

import java.util.*;

public class TopManager implements Loadable {
    private final Map<String, NumberTopHolder> topHolders = new HashMap<>();
    private final TopperPlugin instance;
    private DataStorageSupplier<UUID, Double> storageSupplier;

    public TopManager(TopperPlugin instance) {
        this.instance = instance;
    }

    @Override
    public void enable() {
        storageSupplier = instance.get(NumberStorageBuilder.class).buildSupplier(instance.get(MainConfig.class).getStorageType());
        storageSupplier.enable();
        instance.get(MainConfig.class).getPlaceholders().forEach((key, value) -> addTopHolder(key, new NumberTopHolder(instance, key, new PlaceholderValueProvider(instance, PlaceholderValueProvider.Input.fromString(value)))));
    }

    @Override
    public void disable() {
        topHolders.values().forEach(NumberTopHolder::unregister);
        topHolders.clear();
        storageSupplier.disable();
    }

    public void addTopHolder(String key, NumberTopHolder topHolder) {
        topHolder.register();
        NumberTopHolder oldHolder = topHolders.put(key, topHolder);
        if (oldHolder != null) oldHolder.unregister();
    }

    public DataStorageSupplier<UUID, Double> getStorageSupplier() {
        return storageSupplier;
    }

    public Optional<NumberTopHolder> getTopHolder(String name) {
        return Optional.ofNullable(topHolders.get(name));
    }

    public List<String> getTopHolderNames() {
        return Collections.unmodifiableList(new ArrayList<>(topHolders.keySet()));
    }

    public void create(UUID uuid) {
        topHolders.values().forEach(holder -> holder.getOrCreateEntry(uuid));
    }
}
