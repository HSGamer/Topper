package me.hsgamer.topper.spigot.plugin.builder;

import me.hsgamer.hscore.builder.FunctionalMassBuilder;
import me.hsgamer.topper.spigot.plugin.holder.provider.StatisticValueProvider;
import me.hsgamer.topper.spigot.plugin.holder.provider.ValueProvider;

import java.util.Map;
import java.util.Objects;

public class ValueProviderBuilder extends FunctionalMassBuilder<Map<String, Object>, ValueProvider> {
    public ValueProviderBuilder() {
        register(StatisticValueProvider::new, "statistic", "stat");
    }

    @Override
    protected String getType(Map<String, Object> map) {
        return Objects.toString(map.get("type"), "");
    }
}
