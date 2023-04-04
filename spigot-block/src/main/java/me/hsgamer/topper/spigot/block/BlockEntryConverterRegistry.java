package me.hsgamer.topper.spigot.block;

import com.google.common.reflect.TypeToken;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.annotation.converter.Converter;
import me.hsgamer.hscore.config.annotation.converter.manager.DefaultConverterManager;
import me.hsgamer.topper.spigot.config.DefaultConverterRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class BlockEntryConverterRegistry {
    private BlockEntryConverterRegistry() {
        // EMPTY
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void register() {
        DefaultConverterRegistry.register(new TypeToken<List<BlockEntry>>() {
        }, new Converter() {
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
        });
        DefaultConverterManager.registerConverter(BlockEntry.class, new Converter() {
            @Override
            public BlockEntry convert(Object raw) {
                if (raw == null) return null;
                try {
                    return BlockEntry.deserialize(raw.toString());
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public String convertToRaw(Object value) {
                if (value instanceof BlockEntry) {
                    return ((BlockEntry) value).serialize();
                }
                return null;
            }
        });
    }
}
