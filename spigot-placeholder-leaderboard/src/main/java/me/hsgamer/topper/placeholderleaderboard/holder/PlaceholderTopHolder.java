package me.hsgamer.topper.placeholderleaderboard.holder;

import me.clip.placeholderapi.PlaceholderAPI;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.topper.placeholderleaderboard.TopperPlaceholderLeaderboard;
import org.bukkit.OfflinePlayer;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderTopHolder extends NumberTopHolder {
    private static final Pattern PATTERN = Pattern.compile("\\s*(\\[.*])?\\s*(.*)\\s*");
    private final String placeholder;
    private final boolean isOnlineOnly;
    private final boolean isAsync;
    private final boolean isLenient;

    public PlaceholderTopHolder(TopperPlaceholderLeaderboard instance, String name, String placeholder) {
        super(instance, name);
        Matcher matcher = PATTERN.matcher(placeholder);
        if (matcher.matches()) {
            this.placeholder = Optional.ofNullable(matcher.group(2)).orElse("");
            String prefix = Optional.ofNullable(matcher.group(1)).map(String::toLowerCase).orElse("");
            isOnlineOnly = prefix.contains("[online]");
            isAsync = prefix.contains("[async]");
            isLenient = prefix.contains("[lenient]");
        } else {
            this.placeholder = placeholder;
            isOnlineOnly = false;
            isAsync = false;
            isLenient = false;
        }
    }

    @Override
    public CompletableFuture<Optional<Double>> updateNewValue(UUID uuid) {
        CompletableFuture<Optional<Double>> future = new CompletableFuture<>();
        OfflinePlayer player = instance.getServer().getOfflinePlayer(uuid);
        if (player.isOnline() || !isOnlineOnly) {
            Scheduler.plugin(instance).runner(isAsync).runTask(() -> {
                try {
                    String parsed = PlaceholderAPI.setPlaceholders(player, placeholder);
                    if (parsed.trim().isEmpty()) {
                        if (!isLenient) {
                            instance.getLogger().warning("The placeholder for " + getName() + " is empty");
                        }
                        future.complete(Optional.empty());
                        return;
                    }
                    future.complete(Optional.of(Double.parseDouble(parsed)));
                } catch (Exception e) {
                    if (!isLenient) {
                        instance.getLogger().log(Level.WARNING, "There is an error while parsing the placeholder for " + getName(), e);
                    }
                    future.complete(Optional.empty());
                }
            });
        } else {
            future.complete(Optional.empty());
        }
        return future;
    }
}
