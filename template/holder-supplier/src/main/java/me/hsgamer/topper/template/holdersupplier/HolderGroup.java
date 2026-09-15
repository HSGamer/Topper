package me.hsgamer.topper.template.holdersupplier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class HolderGroup<H> {
    private final Map<Supplier<?>, Function<H, ?>> holderSuppliers = new HashMap<>();
    private final Map<Supplier<?>, Supplier<?>> groupSuppliers = new HashMap<>();

    public abstract List<String> getHolderNames();

    protected abstract Optional<H> getHolder(String name);

    protected <T> void register(Supplier<T> entry, Supplier<T> supplier) {
        groupSuppliers.put(entry, supplier);
    }

    protected <T> void register(Supplier<T> entry, Function<H, T> function) {
        holderSuppliers.put(entry, function);
    }

    public boolean isAvailable(String name) {
        return getHolder(name).isPresent();
    }

    public <T> SupplierType getType(Supplier<T> entry) {
        if (groupSuppliers.containsKey(entry)) {
            return SupplierType.GROUP;
        } else if (holderSuppliers.containsKey(entry)) {
            return SupplierType.HOLDER;
        } else {
            return SupplierType.UNSUPPORTED;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Supplier<T> entry) {
        Supplier<?> supplier = groupSuppliers.get(entry);
        return supplier == null ? entry.get() : (T) supplier.get();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name, Supplier<T> entry) {
        Function<H, ?> function = holderSuppliers.get(entry);
        if (function == null) return entry.get();
        return this.getHolder(name)
                .map(function)
                .map(o -> (T) o)
                .orElseGet(entry);
    }
}
