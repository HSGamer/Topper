package me.hsgamer.topper.spigot.builder;

import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.database.client.sql.StatementBuilder;
import me.hsgamer.topper.core.holder.DataHolder;
import me.hsgamer.topper.core.storage.DataStorage;
import me.hsgamer.topper.spigot.storage.MySqlStorageSupplier;
import me.hsgamer.topper.spigot.storage.SqlStorageSupplier;
import me.hsgamer.topper.spigot.storage.SqliteStorageSupplier;
import me.hsgamer.topper.spigot.storage.YamlStorageSupplier;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class NumberStorageBuilder extends Builder<BasePlugin, Function<DataHolder<Double>, DataStorage<Double>>> {
    public static final NumberStorageBuilder INSTANCE = new NumberStorageBuilder();

    private NumberStorageBuilder() {
        register(plugin -> new YamlStorageSupplier<>(plugin, new YamlNumberStorageConverter()), "yaml", "yml");
        register(plugin -> new SqliteStorageSupplier<>(plugin, new SqlNumberStorageConverter()), "sqlite", "sqlite3");
        register(plugin -> new MySqlStorageSupplier<>(plugin, new SqlNumberStorageConverter()), "mysql", "mysql-connector-java", "mysql-connector");
    }

    public static Function<DataHolder<Double>, DataStorage<Double>> buildSupplier(String type, BasePlugin plugin) {
        return INSTANCE.build(type, plugin).orElseGet(() -> new YamlStorageSupplier<>(plugin, new YamlNumberStorageConverter()));
    }

    private static class YamlNumberStorageConverter implements YamlStorageSupplier.Converter<Double> {
        @Override
        public Double toValue(Object object) {
            try {
                return Double.parseDouble(String.valueOf(object));
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public Object toRaw(Double object) {
            return object;
        }
    }

    private static class SqlNumberStorageConverter implements SqlStorageSupplier.Converter<Double> {
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

        @Override
        public Double getDefaultValue() {
            return 0D;
        }
    }
}
