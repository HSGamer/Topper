package me.hsgamer.topper.spigot.manager;

import me.hsgamer.topper.core.TopHolder;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.config.MainConfig;
import me.hsgamer.topper.spigot.holder.PlaceholderTopHolder;
import me.hsgamer.topper.spigot.storage.YamlStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TopManager {
    private final Map<String, TopHolder> topHolders = new HashMap<>();
    private final TopperPlugin instance;

    public TopManager(TopperPlugin instance) {
        this.instance = instance;
    }

    public void setup() {
        MainConfig.PLACEHOLDERS.getValue().forEach((key, value) -> {
            PlaceholderTopHolder placeholderTopHolder = new PlaceholderTopHolder(instance, new YamlStorage(), key, value);
            placeholderTopHolder.register();
            topHolders.put(key, placeholderTopHolder);
        });
    }

    public void clear() {
        topHolders.values().forEach(TopHolder::unregister);
        topHolders.clear();
    }

    public Optional<TopHolder> getTopHolder(String name) {
        return Optional.ofNullable(topHolders.get(name));
    }

    public void create(UUID uuid) {
        topHolders.values().forEach(holder -> holder.getOrCreateEntry(uuid));
    }
}
