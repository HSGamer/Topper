package me.hsgamer.topper.spigot.storage;

import me.hsgamer.hscore.database.Setting;
import me.hsgamer.hscore.database.client.sql.java.JavaSqlClient;
import me.hsgamer.hscore.database.driver.sqlite.SqliteFileDriver;
import me.hsgamer.topper.core.holder.DataHolder;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.config.DatabaseConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public class SqliteStorage extends SqlStorage {
    private static final JavaSqlClient client;
    private static final AtomicReference<Connection> connectionReference = new AtomicReference<>();

    static {
        TopperPlugin instance = JavaPlugin.getPlugin(TopperPlugin.class);
        client = new JavaSqlClient(
                Setting.create().setDatabaseName(DatabaseConfig.DATABASE.getValue()),
                new SqliteFileDriver(instance.getDataFolder())
        );
        instance.addDisableFunction(() -> {
            try {
                Connection connection = connectionReference.get();
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                instance.getLogger().log(Level.SEVERE, "disable()", e);
            }
        });
    }

    public SqliteStorage(DataHolder<Double> holder) {
        super(holder);
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
