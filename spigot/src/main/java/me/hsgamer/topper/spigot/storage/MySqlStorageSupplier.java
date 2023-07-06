package me.hsgamer.topper.spigot.storage;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
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
        super(plugin.getLogger(), converter);
        this.plugin = plugin;
        DatabaseConfig databaseConfig = ConfigGenerator.newInstance(DatabaseConfig.class, new BukkitConfig(plugin, "database.yml"));
        Setting setting = Setting.create(new MySqlDriver())
                .setDatabaseName(databaseConfig.getDatabase())
                .setHost(databaseConfig.getHost())
                .setPort(databaseConfig.getPort())
                .setUsername(databaseConfig.getUsername())
                .setPassword(databaseConfig.getPassword());
        if (databaseConfig.isUseSSL()) {
            setting.setDriverProperty("useSSL", "true");
        }
        client = new JavaSqlClient(setting);
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
