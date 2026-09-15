package me.hsgamer.topper.template.holdersupplier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class HolderSupplierTemplate {
    private final Map<String, HolderGroup<?>> groupMap = new HashMap<>();

    public <H> Runnable addGroup(String name, HolderGroup<H> group) {
        groupMap.put(name, group);
        return () -> groupMap.remove(name);
    }

    public Optional<HolderGroup<?>> getGroup(String name) {
        return Optional.ofNullable(groupMap.get(name));
    }

    public void clear() {
        groupMap.clear();
    }

    public <T> SupplierType getType(String group, Supplier<T> entry) {
        return getGroup(group).map(g -> g.getType(entry)).orElse(SupplierType.UNSUPPORTED);
    }

    public <T> T getValue(String group, Supplier<T> entry) {
        return getGroup(group).map(g -> g.get(entry)).orElseGet(entry);
    }

    public <T> T getValue(String group, String holder, Supplier<T> entry) {
        return getGroup(group).map(g -> g.get(holder, entry)).orElseGet(entry);
    }
}
