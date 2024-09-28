package me.hsgamer.topper.agent.storage.number;

import me.hsgamer.topper.agent.storage.simple.converter.SqlEntryConverter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public interface SqlNumberEntryConverter<K> extends SqlEntryConverter<K, Double> {
    K getKey(ResultSet resultSet) throws SQLException;

    @Override
    default String[] getValueColumns() {
        return new String[]{"value"};
    }

    @Override
    default String[] getValueColumnDefinitions() {
        return new String[]{"`value` double DEFAULT 0"};
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
