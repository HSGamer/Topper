package me.hsgamer.topper.extra.storage.converter;

import me.hsgamer.hscore.database.client.sql.StatementBuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public interface SqlEntryConverter<T> {
    StatementBuilder createTable(Connection connection, String name);

    StatementBuilder selectAll(Connection connection, String name);

    StatementBuilder select(Connection connection, String name, UUID uuid);

    StatementBuilder insert(Connection connection, String name, UUID uuid, T value);

    StatementBuilder update(Connection connection, String name, UUID uuid, T value);

    T getValue(UUID uuid, ResultSet resultSet) throws SQLException;

    Map<UUID, T> getMap(ResultSet resultSet) throws SQLException;

    T getDefaultValue(UUID uuid);
}
