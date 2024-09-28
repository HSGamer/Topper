package me.hsgamer.topper.agent.storage.number;

import me.hsgamer.topper.agent.storage.simple.converter.SqlEntryConverter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public interface SqlNumberEntryConverter<K> extends SqlEntryConverter<K, Double> {
    String[] getKeyColumnDefinitions();

    K getKey(ResultSet resultSet) throws SQLException;

    @Override
    default String[] getValueColumns() {
        return new String[]{"value"};
    }

    @Override
    default String[] getColumnDefinitions() {
        String[] keyColumnDefinitions = getKeyColumnDefinitions();
        String valueColumnDefinition = "`value` double DEFAULT 0";
        String[] columnDefinitions = new String[keyColumnDefinitions.length + 1];
        System.arraycopy(keyColumnDefinitions, 0, columnDefinitions, 0, keyColumnDefinitions.length);
        columnDefinitions[keyColumnDefinitions.length] = valueColumnDefinition;
        return columnDefinitions;
    }

    @Override
    default Object[] toValueQueryValues(Double value) {
        return new Object[]{value};
    }

    @Override
    default Double getValue(ResultSet resultSet) throws SQLException {
        return resultSet.getDouble("value");
    }

    @Override
    default Map<K, Double> getMap(ResultSet resultSet) throws SQLException {
        Map<K, Double> map = new HashMap<>();
        while (resultSet.next()) {
            map.put(getKey(resultSet), resultSet.getDouble("value"));
        }
        return map;
    }
}
