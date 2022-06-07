package me.hsgamer.topper.spigot.storage;

import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.database.Setting;
import me.hsgamer.hscore.database.client.sql.java.JavaSqlClient;
import me.hsgamer.hscore.database.driver.sqlite.SqliteFileDriver;
import me.hsgamer.topper.spigot.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public class SqliteStorageSupplier extends SqlStorageSupplier {
    private final JavaSqlClient client;
    private final AtomicReference<Connection> connectionReference = new AtomicReference<>();

    public SqliteStorageSupplier(BasePlugin plugin) {
        client = new JavaSqlClient(
                Setting.create().setDatabaseName(DatabaseConfig.DATABASE.getValue()),
                new SqliteFileDriver(plugin.getDataFolder())
        );
        plugin.addDisableFunction(() -> {
            try {
                Connection connection = connectionReference.get();
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "disable()", e);
            }
        });
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
}
