package me.hsgamer.topper.query;

import me.hsgamer.topper.core.DataHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public abstract class QueryManager<K, V, H extends DataHolder<K, V>, A> {
    private final Map<String, QueryAction<K, V, H, A>> actions = new HashMap<>();

    protected abstract Optional<H> getHolder(String name);

    protected void registerAction(String name, QueryAction<K, V, H, A> action) {
        actions.put(name, action);
    }

    protected void registerFunction(String name, BiFunction<@NotNull H, @NotNull String[], @Nullable String> function) {
        registerAction(name, (actor, holder, args) -> function.apply(holder, args));
    }

    protected void registerActorFunction(String name, BiFunction<@NotNull A, @NotNull H, @Nullable String> function) {
        registerAction(name, (actor, holder, args) -> {
            if (actor == null) return null;
            return function.apply(actor, holder);
        });
    }

    @Nullable
    public String get(@Nullable A actor, String query) {
        String[] args = query.split(";");
        if (args.length < 2) return null;
        Optional<H> optionalHolder = getHolder(args[0]);
        if (!optionalHolder.isPresent()) return null;
        H holder = optionalHolder.get();

        QueryAction<K, V, H, A> action = actions.get(args[1]);
        if (action == null) return null;

        String[] newArgs = new String[args.length - 2];
        System.arraycopy(args, 2, newArgs, 0, newArgs.length);

        return action.get(actor, holder, newArgs);
    }
}
