package me.hsgamer.topper.spigot.plugin;

import io.github.projectunified.minelib.plugin.base.BasePlugin;
import io.github.projectunified.minelib.plugin.command.CommandComponent;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.checker.spigotmc.SpigotVersionChecker;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.PathString;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
import me.hsgamer.topper.spigot.plugin.builder.NumberStorageBuilder;
import me.hsgamer.topper.spigot.plugin.builder.ValueProviderBuilder;
import me.hsgamer.topper.spigot.plugin.command.GetTopListCommand;
import me.hsgamer.topper.spigot.plugin.command.ReloadCommand;
import me.hsgamer.topper.spigot.plugin.config.DatabaseConfig;
import me.hsgamer.topper.spigot.plugin.config.MainConfig;
import me.hsgamer.topper.spigot.plugin.config.MessageConfig;
import me.hsgamer.topper.spigot.plugin.hook.HookSystem;
import me.hsgamer.topper.spigot.plugin.listener.JoinListener;
import me.hsgamer.topper.spigot.plugin.manager.TopManager;
import me.hsgamer.topper.spigot.plugin.manager.TopQueryManager;
import org.bstats.bukkit.Metrics;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TopperPlugin extends BasePlugin {
    @Override
    protected List<Object> getComponents() {
        return Arrays.asList(
                new NumberStorageBuilder(
                        this,
                        new File(getDataFolder(), "top"),
                        () -> ConfigGenerator.newInstance(DatabaseConfig.class, new BukkitConfig(this, "database.yml")).toDatabaseSetting()
                ),
                new ValueProviderBuilder(),
                ConfigGenerator.newInstance(MainConfig.class, new BukkitConfig(this)),
                ConfigGenerator.newInstance(MessageConfig.class, new BukkitConfig(this, "messages.yml")),

                new HookSystem(this),

                new TopManager(this),
                new TopQueryManager(this),

                new Permissions(this),
                new CommandComponent(this,
                        new ReloadCommand(this),
                        new GetTopListCommand(this)
                ),
                new JoinListener(this)
        );
    }

    @Override
    public void load() {
        MessageUtils.setPrefix(get(MessageConfig.class)::getPrefix);
        migrateConfig();
    }

    @Override
    public void enable() {
        new Metrics(this, 14938);
        if (getDescription().getVersion().contains("SNAPSHOT")) {
            getLogger().warning("You are using the development version");
            getLogger().warning("This is not ready for production");
            getLogger().warning("Use in your own risk");
        } else {
            new SpigotVersionChecker(101325).getVersion().whenComplete((output, throwable) -> {
                if (throwable != null) {
                    getLogger().log(Level.WARNING, "Failed to check spigot version", throwable);
                } else if (output != null) {
                    if (this.getDescription().getVersion().equalsIgnoreCase(output)) {
                        getLogger().info("You are using the latest version");
                    } else {
                        getLogger().warning("There is an available update");
                        getLogger().warning("New Version: " + output);
                    }
                }
            });
        }
    }

    private void migrateConfig() {
        MainConfig mainConfig = get(MainConfig.class);
        Config config = mainConfig.getConfig();

        boolean migrated = false;

        Map<String[], Object> placeholders = config.getValues(false, "placeholders");
        if (!placeholders.isEmpty()) {
            migrated = true;
            Map<String, Map<String, Object>> holders = new HashMap<>();
            Pattern placeholderPattern = Pattern.compile("\\s*(\\[.*])?\\s*(.*)\\s*");
            for (Map.Entry<String[], Object> entry : placeholders.entrySet()) {
                String key = PathString.joinDefault(entry.getKey());
                String value = entry.getValue().toString();
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("type", "placeholder");
                Matcher matcher = placeholderPattern.matcher(value);
                if (matcher.matches()) {
                    String placeholder = Optional.ofNullable(matcher.group(2)).orElse("");
                    String prefix = Optional.ofNullable(matcher.group(1)).map(String::toLowerCase).orElse("");
                    boolean isOnlineOnly = prefix.contains("[online]");
                    boolean isAsync = prefix.contains("[async]");
                    boolean isLenient = prefix.contains("[lenient]");
                    map.put("placeholder", placeholder);
                    if (isOnlineOnly) {
                        map.put("online", true);
                    }
                    if (isAsync) {
                        map.put("async", true);
                    }
                    if (isLenient) {
                        map.put("lenient", true);
                    }
                } else {
                    map.put("placeholder", value);
                }
                holders.put(key, map);
            }
            config.set(holders, "holders");
            config.set(null, "placeholders");
            config.save();
        }

        if (migrated) {
            getLogger().info("The config has been migrated");
            mainConfig.reloadConfig();
        }
    }
}
