package me.hsgamer.topper.spigot.config;

import com.google.common.reflect.TypeToken;
import me.hsgamer.hscore.config.annotation.converter.Converter;
import me.hsgamer.hscore.config.annotation.converter.manager.DefaultConverterManager;
import me.hsgamer.topper.spigot.config.converter.StringListConverter;
import me.hsgamer.topper.spigot.config.converter.StringObjectMapConverter;
import me.hsgamer.topper.spigot.config.converter.StringValueMapConverter;

import java.util.List;
import java.util.Map;

public final class DefaultConverterRegistry {
    private DefaultConverterRegistry() {
        // EMPTY
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void register() {
        register(new TypeToken<Map<String, Object>>() {
        }, new StringObjectMapConverter());
        register(new TypeToken<Map<String, String>>() {
        }, new StringValueMapConverter());
        register(new TypeToken<List<String>>() {
        }, new StringListConverter());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void register(TypeToken<?> typeToken, Converter converter) {
        DefaultConverterManager.registerConverter(typeToken.getType(), converter);
    }
}
