package me.hsgamer.topper.spigot.builder;

import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.topper.core.storage.DataStorageSupplier;
import me.hsgamer.topper.extra.storage.converter.FlatEntryConverter;
import me.hsgamer.topper.extra.storage.converter.SqlEntryConverter;
import me.hsgamer.topper.spigot.storage.MySqlStorageSupplier;
import me.hsgamer.topper.spigot.storage.SqliteStorageSupplier;
import me.hsgamer.topper.spigot.storage.YamlStorageSupplier;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.function.Supplier;

public class DataStorageBuilder<T> extends Builder<Void, DataStorageSupplier<T>> {
    private final Supplier<DataStorageSupplier<T>> defaultSupplier;

    public DataStorageBuilder(JavaPlugin plugin, File baseFolder, FlatEntryConverter<T> flatEntryConverter, SqlEntryConverter<T> sqlEntryConverter) {
        this.defaultSupplier = () -> new YamlStorageSupplier<>(plugin, baseFolder, flatEntryConverter);
        register(v -> defaultSupplier.get(), "default", "");
        register(v -> new YamlStorageSupplier<>(plugin, baseFolder, flatEntryConverter), "yaml", "yml");
        register(v -> new SqliteStorageSupplier<>(plugin, baseFolder, sqlEntryConverter), "sqlite", "sqlite3");
        register(v -> new MySqlStorageSupplier<>(plugin, sqlEntryConverter), "mysql", "mysql-connector-java", "mysql-connector");
    }

    public DataStorageSupplier<T> buildSupplier(String type) {
        return build(type, null).orElseGet(defaultSupplier);
    }
}
