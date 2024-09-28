package me.hsgamer.topper.spigot.agent.storage.simple;

import io.github.projectunified.minelib.scheduler.common.task.Task;
import io.github.projectunified.minelib.scheduler.global.GlobalScheduler;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.gson.GsonConfig;
import me.hsgamer.topper.agent.storage.simple.builder.DataStorageBuilder;
import me.hsgamer.topper.agent.storage.simple.converter.FlatEntryConverter;
import me.hsgamer.topper.agent.storage.simple.converter.SqlEntryConverter;
import me.hsgamer.topper.agent.storage.simple.setting.DatabaseSetting;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.function.Supplier;

public class SpigotDataStorageBuilder<K, V> extends DataStorageBuilder<K, V> {
    public SpigotDataStorageBuilder(JavaPlugin plugin, File holderBaseFolder, Supplier<DatabaseSetting> databaseSettingSupplier, FlatEntryConverter<K, V> flatEntryConverter, SqlEntryConverter<K, V> sqlEntryConverter) {
        super(
                runnable -> {
                    Task task = GlobalScheduler.get(plugin).runLater(runnable, 40L);
                    return task::cancel;
                },
                runnable -> GlobalScheduler.get(plugin).run(runnable),
                BukkitConfig::new,
                GsonConfig::new,
                databaseSettingSupplier,
                holderBaseFolder,
                flatEntryConverter,
                sqlEntryConverter
        );
    }
}
