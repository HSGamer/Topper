package me.hsgamer.topper.query;

import me.hsgamer.topper.core.DataHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface QueryAction<K, V, H extends DataHolder<K, V>, A> {
    @Nullable
    String get(@Nullable A actor, @NotNull H holder, @NotNull String[] args);
}
