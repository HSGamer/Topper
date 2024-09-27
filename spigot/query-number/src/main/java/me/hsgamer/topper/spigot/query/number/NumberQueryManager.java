package me.hsgamer.topper.spigot.query.number;

import me.hsgamer.topper.agent.snapshot.SnapshotAgent;
import me.hsgamer.topper.core.DataEntry;
import me.hsgamer.topper.core.DataHolder;
import me.hsgamer.topper.query.QueryManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class NumberQueryManager<K, V, H extends DataHolder<K, V>, A> extends QueryManager<K, V, H, A> {
    protected NumberQueryManager() {
        registerFunction("top_name", (holder, args) -> {
            int i = 1;
            try {
                i = Integer.parseInt(args);
            } catch (NumberFormatException ignored) {
                // IGNORED
            }
            K key = getSnapshotAgent(holder).getEntryByIndex(i - 1).map(DataEntry::getKey).orElse(null);
            return getDisplayName(key);
        });
        registerFunction("top_key", (holder, args) -> {
            int i = 1;
            try {
                i = Integer.parseInt(args);
            } catch (NumberFormatException ignored) {
                // IGNORED
            }
            K key = getSnapshotAgent(holder).getEntryByIndex(i - 1).map(DataEntry::getKey).orElse(null);
            return getDisplayKey(key);
        });
        registerFunction("top_value", (holder, args) -> {
            int i = 1;
            try {
                i = Integer.parseInt(args);
            } catch (NumberFormatException ignored) {
                // IGNORED
            }
            V value = getSnapshotAgent(holder).getEntryByIndex(i - 1).map(DataEntry::getValue).orElse(null);
            return getDisplayValue(value);
        });
        registerFunction("top_value_raw", (holder, args) -> {
            int i = 1;
            try {
                i = Integer.parseInt(args);
            } catch (NumberFormatException ignored) {
                // IGNORED
            }
            V value = getSnapshotAgent(holder).getEntryByIndex(i - 1).map(DataEntry::getValue).orElse(null);
            return getDisplayRawValue(value);
        });
        registerActorFunction("top_rank", (actor, holder) -> {
            int index = getSnapshotAgent(holder).getSnapshotIndex(getKeyFromActor(actor));
            return Integer.toString(index + 1);
        });
        registerActorFunction("value", (actor, holder) -> {
            V value = holder.getEntry(getKeyFromActor(actor)).map(DataEntry::getValue).orElse(null);
            return getDisplayValue(value);
        });
        registerActorFunction("raw_value", (actor, holder) -> {
            V value = holder.getEntry(getKeyFromActor(actor)).map(DataEntry::getValue).orElse(null);
            return getDisplayRawValue(value);
        });
    }

    @NotNull
    protected abstract SnapshotAgent<K, V> getSnapshotAgent(@NotNull H holder);

    @NotNull
    protected abstract String getDisplayName(@Nullable K key);

    @NotNull
    protected abstract String getDisplayValue(@Nullable V value);

    @NotNull
    protected abstract String getDisplayKey(@Nullable K key);

    @NotNull
    protected abstract String getDisplayRawValue(@Nullable V value);

    @NotNull
    protected abstract K getKeyFromActor(@NotNull A actor);
}
