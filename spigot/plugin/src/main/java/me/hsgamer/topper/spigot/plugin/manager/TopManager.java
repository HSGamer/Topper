package me.hsgamer.topper.spigot.plugin.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.topper.agent.storage.simple.supplier.DataStorageSupplier;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.builder.NumberStorageBuilder;
import me.hsgamer.topper.spigot.plugin.config.MainConfig;
import me.hsgamer.topper.spigot.plugin.holder.NumberTopHolder;

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
        instance.get(MainConfig.class).getHolders().forEach((key, value) -> {
            NumberTopHolder topHolder = new NumberTopHolder(instance, key, value);
            topHolder.register();
            topHolders.put(key, topHolder);
        });
    }

    @Override
    public void disable() {
        topHolders.values().forEach(NumberTopHolder::unregister);
        topHolders.clear();
        storageSupplier.disable();
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
