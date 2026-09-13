package me.hsgamer.topper.template.nameprovider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class NameProviderTemplate<K> {
    private final List<Function<K, String>> nameProviders = new ArrayList<>();
    private final Function<K, String> defaultNameProvider;

    public NameProviderTemplate(Function<K, String> defaultNameProvider) {
        this.defaultNameProvider = defaultNameProvider;
    }

    public Runnable addNameProvider(Function<K, String> nameProvider) {
        nameProviders.add(nameProvider);
        return () -> nameProviders.remove(nameProvider);
    }

    public String getName(K uuid) {
        for (Function<K, String> nameProvider : nameProviders) {
            String name = nameProvider.apply(uuid);
            if (name != null) {
                return name;
            }
        }
        return defaultNameProvider.apply(uuid);
    }

    public void clear() {
        nameProviders.clear();
    }
}
