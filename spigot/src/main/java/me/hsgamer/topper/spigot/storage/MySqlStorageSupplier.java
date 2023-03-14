package me.hsgamer.topper.spigot.storage;

import me.hsgamer.hscore.database.Setting;
import me.hsgamer.hscore.database.client.sql.java.JavaSqlClient;
import me.hsgamer.hscore.database.driver.mysql.MySqlDriver;
import me.hsgamer.topper.extra.storage.converter.SqlEntryConverter;
import me.hsgamer.topper.spigot.config.DatabaseConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class MySqlStorageSupplier<T> extends SqlStorageSupplier<T> {
    private final JavaPlugin plugin;
    private final JavaSqlClient client;

    public MySqlStorageSupplier(JavaPlugin plugin, SqlEntryConverter<T> converter) {
        super(converter);
        this.plugin = plugin;
        Setting setting = Setting.create()
                .setDatabaseName(DatabaseConfig.DATABASE.getValue())
                .setHost(DatabaseConfig.HOST.getValue())
                .setPort(DatabaseConfig.PORT.getValue())
                .setUsername(DatabaseConfig.USERNAME.getValue())
                .setPassword(DatabaseConfig.PASSWORD.getValue());
        if (Boolean.TRUE.equals(DatabaseConfig.USE_SSL.getValue())) {
            setting.setDriverProperty("useSSL", "true");
        }
        client = new JavaSqlClient(setting, new MySqlDriver());
    }

    @Override
    public Connection getConnection(String name) throws SQLException {
        return client.getConnection();
    }

    @Override
    public void flushConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to close connection", e);
        }
    }
}
