package me.hsgamer.topper.agent.storage.simple.supplier;

import me.hsgamer.hscore.database.client.sql.BatchBuilder;
import me.hsgamer.hscore.database.client.sql.StatementBuilder;
import me.hsgamer.hscore.logger.common.LogLevel;
import me.hsgamer.hscore.logger.common.Logger;
import me.hsgamer.hscore.logger.provider.LoggerProvider;
import me.hsgamer.topper.agent.storage.DataStorage;
import me.hsgamer.topper.agent.storage.simple.converter.SqlEntryConverter;
import me.hsgamer.topper.core.DataHolder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class SqlStorageSupplier<K, V> implements DataStorageSupplier<K, V> {
    protected final Logger logger = LoggerProvider.getLogger(getClass());
    private final SqlEntryConverter<K, V> converter;

    protected SqlStorageSupplier(SqlEntryConverter<K, V> converter) {
        this.converter = converter;
    }

    protected abstract Connection getConnection() throws SQLException;

    protected abstract void flushConnection(Connection connection);

    protected abstract String toSaveStatement(String name, String[] keyColumns, String[] valueColumns);

    protected abstract Object[] toSaveValues(Object[] keys, Object[] values);

    @Override
    public DataStorage<K, V> getStorage(DataHolder<K, V> dataHolder) {
        return new DataStorage<K, V>() {
            @Override
            public Map<K, V> load() {
                Connection connection = null;
                try {
                    connection = getConnection();
                    return StatementBuilder.create(connection)
                            .setStatement("SELECT * FROM `" + dataHolder.getName() + "`;")
                            .query(converter::getMap);
                } catch (SQLException e) {
                    logger.log(LogLevel.ERROR, "Failed to load top holder", e);
                    return Collections.emptyMap();
                } finally {
                    if (connection != null) {
                        flushConnection(connection);
                    }
                }
            }

            @Override
            public CompletableFuture<Void> save(Map<K, V> map, boolean urgent) {
                Runnable runnable = () -> {
                    Connection connection = null;
                    try {
                        connection = getConnection();
                        String[] keyColumns = converter.getKeyColumns();
                        String[] valueColumns = converter.getValueColumns();

                        String statement = toSaveStatement(dataHolder.getName(), keyColumns, valueColumns);

                        BatchBuilder batchBuilder = BatchBuilder.create(connection, statement);
                        map.forEach((key, value) -> {
                            Object[] keyQueryValues = converter.toKeyQueryValues(key);
                            Object[] valueQueryValues = converter.toValueQueryValues(value);
                            Object[] queryValues = toSaveValues(keyQueryValues, valueQueryValues);
                            batchBuilder.addValues(queryValues);
                        });
                        batchBuilder.execute();
                    } catch (SQLException e) {
                        logger.log(LogLevel.ERROR, "Failed to save top holder", e);
                    } finally {
                        if (connection != null) {
                            flushConnection(connection);
                        }
                    }
                };
                if (urgent) {
                    return CompletableFuture.runAsync(runnable);
                } else {
                    runnable.run();
                    return CompletableFuture.completedFuture(null);
                }
            }

            @Override
            public CompletableFuture<Optional<V>> load(K key, boolean urgent) {
                Supplier<Optional<V>> supplier = () -> {
                    Connection connection = null;
                    try {
                        connection = getConnection();
                        String[] keyColumns = converter.getKeyColumns();
                        Object[] keyValues = converter.toKeyQueryValues(key);

                        StringBuilder statement = new StringBuilder("SELECT * FROM `")
                                .append(dataHolder.getName())
                                .append("` WHERE ");
                        for (int i = 0; i < keyColumns.length; i++) {
                            statement.append("`")
                                    .append(keyColumns[i])
                                    .append("` = ?");
                            if (i != keyColumns.length - 1) {
                                statement.append(" AND ");
                            }
                        }
                        return StatementBuilder.create(connection)
                                .setStatement(statement.toString())
                                .addValues(keyValues)
                                .query(resultSet -> resultSet.next()
                                        ? Optional.of(converter.getValue(resultSet))
                                        : Optional.empty()
                                );
                    } catch (SQLException e) {
                        logger.log(LogLevel.ERROR, "Failed to load top holder", e);
                        return Optional.empty();
                    } finally {
                        if (connection != null) {
                            flushConnection(connection);
                        }
                    }
                };
                if (urgent) {
                    return CompletableFuture.supplyAsync(supplier);
                } else {
                    return CompletableFuture.completedFuture(supplier.get());
                }
            }

            @Override
            public void onRegister() {
                Connection connection = null;
                try {
                    connection = getConnection();
                    String[] columnsDefinition = converter.getColumnDefinitions();
                    StringBuilder statement = new StringBuilder("CREATE TABLE IF NOT EXISTS `")
                            .append(dataHolder.getName())
                            .append("` (");
                    for (int i = 0; i < columnsDefinition.length; i++) {
                        statement.append(columnsDefinition[i]);
                        if (i != columnsDefinition.length - 1) {
                            statement.append(", ");
                        }
                    }
                    statement.append(");");
                    StatementBuilder.create(connection)
                            .setStatement(statement.toString())
                            .update();
                } catch (SQLException e) {
                    logger.log(LogLevel.ERROR, "Failed to create table", e);
                } finally {
                    if (connection != null) {
                        flushConnection(connection);
                    }
                }
            }
        };
    }
}
