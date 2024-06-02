package me.hsgamer.topper.agent.storage.simple.converter;

import me.hsgamer.hscore.database.client.sql.StatementBuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public interface SqlEntryConverter<K, V> {
    StatementBuilder createTable(Connection connection, String name);

    StatementBuilder selectAll(Connection connection, String name);

    StatementBuilder select(Connection connection, String name, K key);

    StatementBuilder insert(Connection connection, String name, K key, V value);

    StatementBuilder update(Connection connection, String name, K key, V value);

    V getValue(K key, ResultSet resultSet) throws SQLException;

    Map<K, V> getMap(ResultSet resultSet) throws SQLException;
}
