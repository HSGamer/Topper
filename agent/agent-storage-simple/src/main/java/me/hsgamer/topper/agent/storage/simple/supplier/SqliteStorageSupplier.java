package me.hsgamer.topper.agent.storage.simple.supplier;

import me.hsgamer.hscore.database.Setting;
import me.hsgamer.hscore.database.client.sql.java.JavaSqlClient;
import me.hsgamer.hscore.database.driver.sqlite.SqliteFileDriver;
import me.hsgamer.hscore.logger.common.LogLevel;
import me.hsgamer.topper.agent.storage.simple.config.DatabaseConfig;
import me.hsgamer.topper.agent.storage.simple.converter.SqlEntryConverter;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

public class SqliteStorageSupplier<K, V> extends SqlStorageSupplier<K, V> {
    private final JavaSqlClient client;
    private final AtomicReference<Connection> connectionReference = new AtomicReference<>();

    public SqliteStorageSupplier(DatabaseConfig databaseConfig, File baseFolder, SqlEntryConverter<K, V> converter) {
        super(converter);
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
            logger.log(LogLevel.ERROR, "disable()", e);
        }
    }
}
