package me.hsgamer.topper.spigot.config;

import com.google.common.reflect.TypeToken;
import me.hsgamer.hscore.config.annotation.converter.Converter;
import me.hsgamer.hscore.config.annotation.converter.manager.DefaultConverterManager;
import me.hsgamer.topper.spigot.block.BlockEntry;
import me.hsgamer.topper.spigot.config.converter.*;
import me.hsgamer.topper.spigot.formatter.NumberFormatter;

import java.util.List;
import java.util.Map;

public final class DefaultConverterRegistry {
    private DefaultConverterRegistry() {
        // EMPTY
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void register() {
        register(new TypeToken<List<BlockEntry>>() {
        }, new BlockEntryListConverter());
        register(new TypeToken<Map<String, NumberFormatter>>() {
        }, new NumberFormatterMapConverter());
        register(new TypeToken<Map<String, Object>>() {
        }, new StringObjectMapConverter());
        register(new TypeToken<Map<String, String>>() {
        }, new StringValueMapConverter());
        register(new TypeToken<List<String>>() {
        }, new StringListConverter());
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void register(TypeToken<?> typeToken, Converter converter) {
        DefaultConverterManager.registerConverter(typeToken.getType(), converter);
    }
}
