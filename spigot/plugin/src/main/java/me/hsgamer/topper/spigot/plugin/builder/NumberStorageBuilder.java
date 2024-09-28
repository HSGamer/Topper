package me.hsgamer.topper.spigot.plugin.builder;

import me.hsgamer.topper.agent.storage.number.FlatNumberEntryConverter;
import me.hsgamer.topper.agent.storage.number.SqlNumberEntryConverter;
import me.hsgamer.topper.agent.storage.simple.setting.DatabaseSetting;
import me.hsgamer.topper.spigot.agent.storage.simple.SpigotDataStorageBuilder;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Supplier;

public class NumberStorageBuilder extends SpigotDataStorageBuilder<UUID, Double> {
    public NumberStorageBuilder(JavaPlugin plugin, File holderBaseFolder, Supplier<DatabaseSetting> databaseSettingSupplier) {
        super(plugin, holderBaseFolder, databaseSettingSupplier,
                new FlatNumberEntryConverter<UUID>() {
                    @Override
                    public UUID toKey(String key) {
                        return UUID.fromString(key);
                    }

                    @Override
                    public String toRawKey(UUID uuid) {
                        return uuid.toString();
                    }
                },
                new SqlNumberEntryConverter<UUID>() {
                    @Override
                    public String[] getKeyColumns() {
                        return new String[]{"uuid"};
                    }

                    @Override
                    public UUID getKey(ResultSet resultSet) throws SQLException {
                        return UUID.fromString(resultSet.getString("uuid"));
                    }

                    @Override
                    public String[] getKeyColumnDefinitions() {
                        return new String[]{"`uuid` varchar(36) NOT NULL"};
                    }

                    @Override
                    public Object[] toKeyQueryValues(UUID key) {
                        return new Object[]{key.toString()};
                    }
                });
    }

}
