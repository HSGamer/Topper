package me.hsgamer.topper.spigot.storage;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
import me.hsgamer.hscore.database.Setting;
import me.hsgamer.hscore.database.client.sql.java.JavaSqlClient;
import me.hsgamer.hscore.database.driver.sqlite.SqliteFileDriver;
import me.hsgamer.topper.extra.storage.converter.SqlEntryConverter;
import me.hsgamer.topper.spigot.config.DatabaseConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public class SqliteStorageSupplier<T> extends SqlStorageSupplier<T> {
    private final JavaSqlClient client;
    private final AtomicReference<Connection> connectionReference = new AtomicReference<>();
    private final JavaPlugin plugin;

    public SqliteStorageSupplier(JavaPlugin plugin, File baseFolder, SqlEntryConverter<UUID, T> converter) {
        super(plugin.getLogger(), converter);
        this.plugin = plugin;
        DatabaseConfig databaseConfig = ConfigGenerator.newInstance(DatabaseConfig.class, new BukkitConfig(plugin, "database.yml"));
        client = new JavaSqlClient(Setting.create(new SqliteFileDriver(baseFolder)).setDatabaseName(databaseConfig.getDatabase()));
    }

    @Override
    public Connection getConnection(String name) throws SQLException {
        Connection connection = connectionReference.get();
        if (connection == null || connection.isClosed()) {
            connection = client.getConnection();
            connectionReference.set(connection);
        }
        return connection;
    }

    @Override
    public void flushConnection(Connection connection) {
        // EMPTY
    }

    @Override
    public void disable() {
        try {
            Connection connection = connectionReference.get();
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "disable()", e);
        }
    }
}
