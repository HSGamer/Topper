package me.hsgamer.topper.spigot.agent.storage.simple;

import io.github.projectunified.minelib.scheduler.common.task.Task;
import io.github.projectunified.minelib.scheduler.global.GlobalScheduler;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.topper.agent.storage.simple.builder.DataStorageBuilder;
import me.hsgamer.topper.agent.storage.simple.converter.FlatEntryConverter;
import me.hsgamer.topper.agent.storage.simple.converter.SqlEntryConverter;
import me.hsgamer.topper.agent.storage.simple.setting.DatabaseSetting;
import me.hsgamer.topper.spigot.agent.storage.simple.supplier.ConfigStorageSupplier;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class SpigotDataStorageBuilder<K, V> extends DataStorageBuilder<K, V> {
    private final UnaryOperator<Runnable> runTaskFunction;
    private final Executor mainThreadExecutor;

    public SpigotDataStorageBuilder(JavaPlugin plugin, File holderBaseFolder, Supplier<DatabaseSetting> databaseSettingSupplier, FlatEntryConverter<K, V> flatEntryConverter, SqlEntryConverter<K, V> sqlEntryConverter) {
        super(
                databaseSettingSupplier,
                holderBaseFolder,
                flatEntryConverter,
                sqlEntryConverter
        );
        this.runTaskFunction = runnable -> {
            Task task = GlobalScheduler.get(plugin).runLater(runnable, 40L);
            return task::cancel;
        };
        this.mainThreadExecutor = runnable -> GlobalScheduler.get(plugin).run(runnable);
        register(v -> new ConfigStorageSupplier<>(mainThreadExecutor, name -> name + ".yml", BukkitConfig::new, holderBaseFolder, flatEntryConverter), "config", "yaml", "yml");
        register(v -> new ConfigStorageSupplier<>(mainThreadExecutor, name -> name + ".json", BukkitConfig::new, holderBaseFolder, flatEntryConverter), "json");
    }

    public UnaryOperator<Runnable> getRunTaskFunction() {
        return runTaskFunction;
    }

    public Executor getMainThreadExecutor() {
        return mainThreadExecutor;
    }
}
