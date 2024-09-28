package me.hsgamer.topper.agent.storage.simple.builder;

import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.topper.agent.storage.simple.converter.FlatEntryConverter;
import me.hsgamer.topper.agent.storage.simple.converter.SqlEntryConverter;
import me.hsgamer.topper.agent.storage.simple.setting.DatabaseSetting;
import me.hsgamer.topper.agent.storage.simple.supplier.DataStorageSupplier;
import me.hsgamer.topper.agent.storage.simple.supplier.FlatStorageSupplier;
import me.hsgamer.topper.agent.storage.simple.supplier.MySqlStorageSupplier;
import me.hsgamer.topper.agent.storage.simple.supplier.SqliteStorageSupplier;

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
            Function<File, Config> jsonConfigProvider,
            Supplier<DatabaseSetting> databaseSettingSupplier,
            File holderBaseFolder,
            FlatEntryConverter<K, V> flatEntryConverter,
            SqlEntryConverter<K, V> sqlEntryConverter
    ) {
        Supplier<DataStorageSupplier<K, V>> yamlSupplier = () -> new FlatStorageSupplier<>(runTaskFunction, mainThreadExecutor, name -> name + ".yml", yamlConfigProvider, holderBaseFolder, flatEntryConverter);
        this.defaultSupplier = yamlSupplier;
        register(v -> defaultSupplier.get(), "default", "");
        register(v -> yamlSupplier.get(), "yaml", "yml");
        register(v -> new FlatStorageSupplier<>(runTaskFunction, mainThreadExecutor, name -> name + ".json", jsonConfigProvider, holderBaseFolder, flatEntryConverter), "json");
        register(v -> new SqliteStorageSupplier<>(databaseSettingSupplier.get(), holderBaseFolder, sqlEntryConverter), "sqlite", "sqlite3");
        register(v -> new MySqlStorageSupplier<>(databaseSettingSupplier.get(), sqlEntryConverter), "mysql", "mysql-connector-java", "mysql-connector");
    }

    public DataStorageSupplier<K, V> buildSupplier(String type) {
        return build(type, null).orElseGet(defaultSupplier);
    }
}
