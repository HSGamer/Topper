package me.hsgamer.topper.spigot.agent.storage.number;

import me.hsgamer.topper.agent.storage.simple.converter.FlatEntryConverter;
import me.hsgamer.topper.agent.storage.simple.converter.SqlEntryConverter;
import me.hsgamer.topper.spigot.agent.storage.simple.SpigotDataStorageBuilder;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NumberStorageBuilder extends SpigotDataStorageBuilder<UUID, Double> {
    public NumberStorageBuilder(JavaPlugin plugin, File holderBaseFolder) {
        super(plugin, holderBaseFolder, new FlatNumberEntryConverter(), new SqlNumberEntryConverter());
    }

    private static class FlatNumberEntryConverter implements FlatEntryConverter<UUID, Double> {
        @Override
        public UUID toKey(String key) {
            return UUID.fromString(key);
        }

        @Override
        public String toRawKey(UUID uuid) {
            return uuid.toString();
        }

        @Override
        public Double toValue(Object object) {
            try {
                return Double.parseDouble(String.valueOf(object));
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public Object toRawValue(Double object) {
            return object;
        }
    }

    private static class SqlNumberEntryConverter implements SqlEntryConverter<UUID, Double> {
        @Override
        public String[] getKeyColumns() {
            return new String[]{"uuid"};
        }

        @Override
        public String[] getValueColumns() {
            return new String[]{"value"};
        }

        @Override
        public String[] getColumnDefinitions() {
            return new String[]{"`uuid` varchar(36) NOT NULL UNIQUE", "`value` double DEFAULT 0"};
        }

        @Override
        public Object[] toKeyQueryValues(UUID key) {
            return new Object[]{key.toString()};
        }

        @Override
        public Object[] toValueQueryValues(Double value) {
            return new Object[]{value};
        }

        @Override
        public Double getValue(ResultSet resultSet) throws SQLException {
            return resultSet.getDouble("value");
        }

        @Override
        public Map<UUID, Double> getMap(ResultSet resultSet) throws SQLException {
            Map<UUID, Double> map = new HashMap<>();
            while (resultSet.next()) {
                map.put(UUID.fromString(resultSet.getString("uuid")), resultSet.getDouble("value"));
            }
            return map;
        }
    }
}
