package me.hsgamer.topper.agent.storage.simple.supplier;

import me.hsgamer.hscore.logger.common.LogLevel;
import me.hsgamer.hscore.logger.common.Logger;
import me.hsgamer.hscore.logger.provider.LoggerProvider;
import me.hsgamer.topper.agent.storage.simple.converter.SqlEntryConverter;
import me.hsgamer.topper.agent.storage.supplier.DataStorage;
import me.hsgamer.topper.agent.storage.supplier.DataStorageSupplier;
import me.hsgamer.topper.core.DataHolder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class SqlStorageSupplier<K, V> implements DataStorageSupplier<K, V> {
    protected final Logger logger = LoggerProvider.getLogger(getClass());
    private final SqlEntryConverter<K, V> converter;

    protected SqlStorageSupplier(SqlEntryConverter<K, V> converter) {
        this.converter = converter;
    }

    public abstract Connection getConnection(String name) throws SQLException;

    public abstract void flushConnection(Connection connection);

    public Connection getAndCreateTable(String name) throws SQLException {
        Connection connection = getConnection(name);
        converter.createTable(connection, name).update();
        return connection;
    }

    @Override
    public DataStorage<K, V> getStorage(DataHolder<K, V> holder) {
        return new DataStorage<K, V>(holder) {
            @Override
            public CompletableFuture<Map<K, V>> load() {
                String name = holder.getName();
                return CompletableFuture.supplyAsync(() -> {
                    Connection connection = null;
                    try {
                        connection = getAndCreateTable(name);
                        return converter.selectAll(connection, name).querySafe(converter::getMap).orElseGet(Collections::emptyMap);
                    } catch (SQLException e) {
                        logger.log(LogLevel.ERROR, "Failed to load top holder", e);
                        return Collections.emptyMap();
                    } finally {
                        if (connection != null) {
                            flushConnection(connection);
                        }
                    }
                });
            }

            @Override
            public CompletableFuture<Void> save(K key, V value, boolean urgent) {
                String name = holder.getName();
                Runnable runnable = () -> {
                    Connection connection = null;
                    try {
                        connection = getAndCreateTable(name);
                        boolean exists = converter.select(connection, name, key).query(ResultSet::next);
                        if (exists) {
                            converter.update(connection, name, key, value).update();
                        } else {
                            converter.insert(connection, name, key, value).update();
                        }
                    } catch (SQLException e) {
                        logger.log(LogLevel.ERROR, "Failed to save entry", e);
                    } finally {
                        if (connection != null) {
                            flushConnection(connection);
                        }
                    }
                };
                if (urgent) {
                    runnable.run();
                    return CompletableFuture.completedFuture(null);
                } else {
                    return CompletableFuture.runAsync(runnable);
                }
            }

            @Override
            public CompletableFuture<Optional<V>> load(K key, boolean urgent) {
                String name = holder.getName();
                return CompletableFuture.supplyAsync(() -> {
                    Connection connection = null;
                    try {
                        connection = getAndCreateTable(name);
                        V value = converter.select(connection, name, key).query(resultSet -> resultSet.next() ? converter.getValue(key, resultSet) : null);
                        return Optional.ofNullable(value);
                    } catch (SQLException e) {
                        logger.log(LogLevel.ERROR, "Failed to load top entry", e);
                        return Optional.empty();
                    } finally {
                        if (connection != null) {
                            flushConnection(connection);
                        }
                    }
                });
            }
        };
    }
}
