package me.hsgamer.topper.spigot.builder;

import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.topper.core.holder.DataHolder;
import me.hsgamer.topper.core.storage.DataStorage;
import me.hsgamer.topper.spigot.storage.MySqlStorage;
import me.hsgamer.topper.spigot.storage.SqliteStorage;
import me.hsgamer.topper.spigot.storage.YamlStorage;

public class TopStorageBuilder extends Builder<DataHolder<Double>, DataStorage<Double>> {
    public static final TopStorageBuilder INSTANCE = new TopStorageBuilder();

    private TopStorageBuilder() {
        register(YamlStorage::new, "yaml", "yml");
        register(SqliteStorage::new, "sqlite", "sqlite3");
        register(MySqlStorage::new, "mysql", "mysql-connector-java", "mysql-connector");
    }
}
