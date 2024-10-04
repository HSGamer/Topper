package me.hsgamer.topper.spigot.plugin.builder;

import me.hsgamer.hscore.builder.FunctionalMassBuilder;
import me.hsgamer.topper.spigot.plugin.provider.ValueProvider;

import java.util.Map;
import java.util.Objects;

public class ValueProviderBuilder extends FunctionalMassBuilder<Map<String, Object>, ValueProvider> {
    @Override
    protected String getType(Map<String, Object> map) {
        return Objects.toString(map.get("type"), "");
    }
}
