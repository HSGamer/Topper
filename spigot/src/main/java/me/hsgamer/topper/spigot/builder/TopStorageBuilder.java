package me.hsgamer.topper.spigot.builder;

import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.topper.core.holder.DataHolder;
import me.hsgamer.topper.core.storage.DataStorage;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.storage.MySqlStorageSupplier;
import me.hsgamer.topper.spigot.storage.SqliteStorageSupplier;
import me.hsgamer.topper.spigot.storage.YamlStorageSupplier;

import java.util.function.Function;

public class TopStorageBuilder extends Builder<TopperPlugin, Function<DataHolder<Double>, DataStorage<Double>>> {
    public static final TopStorageBuilder INSTANCE = new TopStorageBuilder();

    private TopStorageBuilder() {
        register(YamlStorageSupplier::new, "yaml", "yml");
        register(SqliteStorageSupplier::new, "sqlite", "sqlite3");
        register(MySqlStorageSupplier::new, "mysql", "mysql-connector-java", "mysql-connector");
    }
}
