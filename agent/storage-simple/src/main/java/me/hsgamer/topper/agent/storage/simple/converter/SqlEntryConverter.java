package me.hsgamer.topper.agent.storage.simple.converter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public interface SqlEntryConverter<K, V> {
    String[] getKeyColumns();

    String[] getValueColumns();

    String[] getKeyColumnDefinitions();

    String[] getValueColumnDefinitions();

    Object[] toKeyQueryValues(K key);

    Object[] toValueQueryValues(V value);

    V getValue(ResultSet resultSet) throws SQLException;

    Map<K, V> getMap(ResultSet resultSet) throws SQLException;
}
