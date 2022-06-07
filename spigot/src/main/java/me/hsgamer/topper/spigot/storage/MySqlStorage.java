package me.hsgamer.topper.spigot.storage;

import me.hsgamer.hscore.database.Setting;
import me.hsgamer.hscore.database.client.sql.java.JavaSqlClient;
import me.hsgamer.hscore.database.driver.mysql.MySqlDriver;
import me.hsgamer.topper.core.holder.DataHolder;
import me.hsgamer.topper.spigot.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class MySqlStorage extends SqlStorage {
    private static final JavaSqlClient client;

    static {
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

    public MySqlStorage(DataHolder<Double> holder) {
        super(holder);
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
            LOGGER.log(Level.WARNING, "Failed to close connection", e);
        }
    }
}
