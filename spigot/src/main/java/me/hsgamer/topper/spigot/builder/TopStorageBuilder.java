package me.hsgamer.topper.spigot.builder;

import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.topper.core.TopStorage;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.storage.YamlStorage;

public class TopStorageBuilder extends Builder<TopperPlugin, TopStorage> {
    public static final TopStorageBuilder INSTANCE = new TopStorageBuilder();

    private TopStorageBuilder() {
        register(plugin -> new YamlStorage(), "yaml", "yml");
    }
}
