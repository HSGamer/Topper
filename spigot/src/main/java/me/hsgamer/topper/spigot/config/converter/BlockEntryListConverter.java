package me.hsgamer.topper.spigot.config.converter;

import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.annotation.converter.Converter;
import me.hsgamer.topper.spigot.block.BlockEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlockEntryListConverter implements Converter {
    @Override
    public List<BlockEntry> convert(Object raw) {
        if (raw == null) return null;
        return CollectionUtils.createStringListFromObject(raw, true).stream().map(BlockEntry::deserialize).collect(Collectors.toList());
    }

    @Override
    public List<String> convertToRaw(Object value) {
        if (value instanceof List) {
            List<String> list = new ArrayList<>();
            for (Object object : (List<?>) value) {
                if (object instanceof BlockEntry) {
                    list.add(((BlockEntry) object).serialize());
                }
            }
            return list;
        }
        return null;
    }
}
