package me.hsgamer.topper.spigot.number;

import me.hsgamer.hscore.database.client.sql.StatementBuilder;
import me.hsgamer.topper.agent.storage.simple.converter.FlatEntryConverter;
import me.hsgamer.topper.agent.storage.simple.converter.SqlEntryConverter;
import me.hsgamer.topper.spigot.agent.storage.simple.SpigotDataStorageBuilder;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
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
        public StatementBuilder createTable(Connection connection, String name) {
            return StatementBuilder.create(connection).setStatement("CREATE TABLE IF NOT EXISTS `" + name + "` (`uuid` varchar(36) NOT NULL UNIQUE, `value` double DEFAULT 0);");
        }

        @Override
        public StatementBuilder selectAll(Connection connection, String name) {
            return StatementBuilder.create(connection).setStatement("SELECT * FROM `" + name + "`;");
        }

        @Override
        public StatementBuilder select(Connection connection, String name, UUID uuid) {
            return StatementBuilder.create(connection)
                    .setStatement("SELECT * FROM `" + name + "` WHERE `uuid` = ?;")
                    .addValues(uuid.toString());
        }

        @Override
        public StatementBuilder insert(Connection connection, String name, UUID uuid, Double value) {
            return StatementBuilder.create(connection)
                    .setStatement("INSERT INTO `" + name + "` (`uuid`, `value`) VALUES (?, ?);")
                    .addValues(uuid.toString())
                    .addValues(value);
        }

        @Override
        public StatementBuilder update(Connection connection, String name, UUID uuid, Double value) {
            return StatementBuilder.create(connection)
                    .setStatement("UPDATE `" + name + "` SET `value` = ? WHERE `uuid` = ?;")
                    .addValues(value)
                    .addValues(uuid.toString());
        }

        @Override
        public Double getValue(UUID uuid, ResultSet resultSet) throws SQLException {
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
