package me.hsgamer.topper.spigot.storage;

import me.hsgamer.hscore.database.client.sql.StatementBuilder;
import me.hsgamer.topper.core.holder.DataHolder;
import me.hsgamer.topper.core.storage.DataStorage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Level;

public abstract class SqlStorageSupplier<T> implements Function<DataHolder<T>, DataStorage<T>> {
    private final Converter<T> converter;

    protected SqlStorageSupplier(Converter<T> converter) {
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
    public DataStorage<T> apply(DataHolder<T> holder) {
        return new DataStorage<T>(holder) {
            @Override
            public CompletableFuture<Map<UUID, T>> load() {
                String name = holder.getName();
                return CompletableFuture.supplyAsync(() -> {
                    Connection connection = null;
                    try {
                        connection = getAndCreateTable(name);
                        return converter.selectAll(connection, name).querySafe(converter::getMap).orElseGet(Collections::emptyMap);
                    } catch (SQLException e) {
                        LOGGER.log(Level.SEVERE, "Failed to load top holder", e);
                        return Collections.emptyMap();
                    } finally {
                        if (connection != null) {
                            flushConnection(connection);
                        }
                    }
                });
            }

            @Override
            public CompletableFuture<Void> save(UUID uuid, T value, boolean urgent) {
                String name = holder.getName();
                Runnable runnable = () -> {
                    Connection connection = null;
                    try {
                        connection = getAndCreateTable(name);
                        boolean exists = converter.select(connection, name, uuid).query(ResultSet::next);
                        if (exists) {
                            converter.update(connection, name, uuid, value).update();
                        } else {
                            converter.insert(connection, name, uuid, value).update();
                        }
                    } catch (SQLException e) {
                        LOGGER.log(Level.SEVERE, "Failed to save entry", e);
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
            public CompletableFuture<Optional<T>> load(UUID uuid, boolean urgent) {
                String name = holder.getName();
                return CompletableFuture.supplyAsync(() -> {
                    Connection connection = null;
                    try {
                        connection = getAndCreateTable(name);
                        return converter.select(connection, name, uuid).querySafe(resultSet -> resultSet.next() ? converter.getValue(uuid, resultSet) : converter.getDefaultValue(uuid));
                    } catch (SQLException e) {
                        LOGGER.log(Level.SEVERE, "Failed to load top entry", e);
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

    public interface Converter<T> {
        StatementBuilder createTable(Connection connection, String name);

        StatementBuilder selectAll(Connection connection, String name);

        StatementBuilder select(Connection connection, String name, UUID uuid);

        StatementBuilder insert(Connection connection, String name, UUID uuid, T value);

        StatementBuilder update(Connection connection, String name, UUID uuid, T value);

        T getValue(UUID uuid, ResultSet resultSet) throws SQLException;

        Map<UUID, T> getMap(ResultSet resultSet) throws SQLException;

        T getDefaultValue(UUID uuid);
    }
}
