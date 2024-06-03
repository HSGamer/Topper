package me.hsgamer.topper.spigot.block;

import me.hsgamer.hscore.config.annotation.converter.Converter;
import me.hsgamer.hscore.config.annotation.converter.manager.DefaultConverterManager;

public final class BlockEntryConverterRegistry {
    private BlockEntryConverterRegistry() {
        // EMPTY
    }

    public static void register() {
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
