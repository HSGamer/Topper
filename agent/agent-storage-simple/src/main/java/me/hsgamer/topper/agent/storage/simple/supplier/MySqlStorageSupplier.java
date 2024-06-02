package me.hsgamer.topper.agent.storage.simple.supplier;

import me.hsgamer.hscore.database.Setting;
import me.hsgamer.hscore.database.client.sql.java.JavaSqlClient;
import me.hsgamer.hscore.database.driver.mysql.MySqlDriver;
import me.hsgamer.hscore.logger.common.LogLevel;
import me.hsgamer.topper.agent.storage.simple.config.DatabaseConfig;
import me.hsgamer.topper.agent.storage.simple.converter.SqlEntryConverter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class MySqlStorageSupplier<T> extends SqlStorageSupplier<T> {
    private final JavaSqlClient client;

    public MySqlStorageSupplier(DatabaseConfig databaseConfig, SqlEntryConverter<UUID, T> converter) {
        super(converter);
        Setting setting = Setting.create(new MySqlDriver())
                .setDatabaseName(databaseConfig.getDatabase())
                .setHost(databaseConfig.getHost())
                .setPort(databaseConfig.getPort())
                .setUsername(databaseConfig.getUsername())
                .setPassword(databaseConfig.getPassword())
                .setClientProperties(databaseConfig.getClientProperties())
                .setDriverProperties(databaseConfig.getDriverProperties());
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
            logger.log(LogLevel.ERROR, "Failed to close connection", e);
        }
    }
}
