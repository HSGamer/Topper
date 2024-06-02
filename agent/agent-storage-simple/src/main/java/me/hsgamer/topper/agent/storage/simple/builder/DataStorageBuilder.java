package me.hsgamer.topper.agent.storage.simple.builder;

import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.topper.agent.storage.simple.config.DatabaseConfig;
import me.hsgamer.topper.agent.storage.simple.converter.FlatEntryConverter;
import me.hsgamer.topper.agent.storage.simple.converter.SqlEntryConverter;
import me.hsgamer.topper.agent.storage.simple.supplier.MySqlStorageSupplier;
import me.hsgamer.topper.agent.storage.simple.supplier.SqliteStorageSupplier;
import me.hsgamer.topper.agent.storage.simple.supplier.YamlStorageSupplier;
import me.hsgamer.topper.agent.storage.supplier.DataStorageSupplier;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class DataStorageBuilder<K, V> extends Builder<Void, DataStorageSupplier<K, V>> {
    private final Supplier<DataStorageSupplier<K, V>> defaultSupplier;

    public DataStorageBuilder(
            UnaryOperator<Runnable> runTaskFunction,
            Executor mainThreadExecutor,
            Function<File, Config> yamlConfigProvider,
            Supplier<DatabaseConfig> databaseConfigSupplier,
            File baseFolder,
            FlatEntryConverter<K, V> flatEntryConverter,
            SqlEntryConverter<K, V> sqlEntryConverter
    ) {
        Supplier<YamlStorageSupplier<K, V>> yamlSupplier = () -> new YamlStorageSupplier<>(runTaskFunction, mainThreadExecutor, yamlConfigProvider, baseFolder, flatEntryConverter);
        this.defaultSupplier = yamlSupplier::get;
        register(v -> defaultSupplier.get(), "default", "");
        register(v -> yamlSupplier.get(), "yaml", "yml");
        register(v -> new SqliteStorageSupplier<>(databaseConfigSupplier.get(), baseFolder, sqlEntryConverter), "sqlite", "sqlite3");
        register(v -> new MySqlStorageSupplier<>(databaseConfigSupplier.get(), sqlEntryConverter), "mysql", "mysql-connector-java", "mysql-connector");
    }

    public DataStorageSupplier<K, V> buildSupplier(String type) {
        return build(type, null).orElseGet(defaultSupplier);
    }
}
