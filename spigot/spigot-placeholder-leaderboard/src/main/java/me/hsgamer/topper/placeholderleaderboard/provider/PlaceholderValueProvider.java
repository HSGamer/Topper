package me.hsgamer.topper.placeholderleaderboard.provider;

import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import io.github.projectunified.minelib.scheduler.global.GlobalScheduler;
import me.clip.placeholderapi.PlaceholderAPI;
import me.hsgamer.topper.placeholderleaderboard.TopperPlaceholderLeaderboard;
import org.bukkit.OfflinePlayer;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderValueProvider implements ValueProvider {
    private final TopperPlaceholderLeaderboard plugin;
    private final Input input;

    public PlaceholderValueProvider(TopperPlaceholderLeaderboard plugin, Input input) {
        this.plugin = plugin;
        this.input = input;
    }

    @Override
    public CompletableFuture<Optional<Double>> getValue(UUID uuid) {
        CompletableFuture<Optional<Double>> future = new CompletableFuture<>();
        OfflinePlayer player = plugin.getServer().getOfflinePlayer(uuid);
        if (player.isOnline() || !input.isOnlineOnly) {
            (input.isAsync ? AsyncScheduler.get(plugin) : GlobalScheduler.get(plugin)).run(() -> {
                try {
                    String parsed = PlaceholderAPI.setPlaceholders(player, input.placeholder).trim();
                    if (parsed.isEmpty()) {
                        if (!input.isLenient) {
                            plugin.getLogger().warning("The placeholder " + input.placeholder + " returns empty");
                        }
                        future.complete(Optional.empty());
                        return;
                    }
                    future.complete(Optional.of(Double.parseDouble(parsed)));
                } catch (Exception e) {
                    if (!input.isLenient) {
                        plugin.getLogger().log(Level.WARNING, "There is an error while parsing the placeholder: " + input.placeholder, e);
                    }
                    future.complete(Optional.empty());
                }
            });
        } else {
            future.complete(Optional.empty());
        }
        return future;
    }

    public static final class Input {
        private static final Pattern PATTERN = Pattern.compile("\\s*(\\[.*])?\\s*(.*)\\s*");
        public final String placeholder;
        public final boolean isOnlineOnly;
        public final boolean isAsync;
        public final boolean isLenient;

        private Input(String placeholder, boolean isOnlineOnly, boolean isAsync, boolean isLenient) {
            this.placeholder = placeholder;
            this.isOnlineOnly = isOnlineOnly;
            this.isAsync = isAsync;
            this.isLenient = isLenient;
        }

        public static Input fromString(String input) {
            Matcher matcher = PATTERN.matcher(input);
            if (matcher.matches()) {
                String placeholder = Optional.ofNullable(matcher.group(2)).orElse("");
                String prefix = Optional.ofNullable(matcher.group(1)).map(String::toLowerCase).orElse("");
                boolean isOnlineOnly = prefix.contains("[online]");
                boolean isAsync = prefix.contains("[async]");
                boolean isLenient = prefix.contains("[lenient]");
                return new Input(placeholder, isOnlineOnly, isAsync, isLenient);
            } else {
                return new Input(input, false, false, false);
            }
        }

        public static Input fromMap(Map<String, Object> map) {
            String placeholder = Optional.ofNullable(map.get("placeholder")).map(Object::toString).orElse("");
            boolean isOnlineOnly = Optional.ofNullable(map.get("online"))
                    .map(Object::toString)
                    .map(String::toLowerCase)
                    .map(Boolean::parseBoolean)
                    .orElse(false);
            boolean isAsync = Optional.ofNullable(map.get("async"))
                    .map(Object::toString)
                    .map(String::toLowerCase)
                    .map(Boolean::parseBoolean)
                    .orElse(false);
            boolean isLenient = Optional.ofNullable(map.get("lenient"))
                    .map(Object::toString)
                    .map(String::toLowerCase)
                    .map(Boolean::parseBoolean)
                    .orElse(false);
            return new Input(placeholder, isOnlineOnly, isAsync, isLenient);
        }
    }
}
