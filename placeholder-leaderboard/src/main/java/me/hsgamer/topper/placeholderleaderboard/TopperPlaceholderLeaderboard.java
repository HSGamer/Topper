package me.hsgamer.topper.placeholderleaderboard;

import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.checker.spigotmc.SpigotVersionChecker;
import me.hsgamer.topper.placeholderleaderboard.command.GetTopListCommand;
import me.hsgamer.topper.placeholderleaderboard.command.ReloadCommand;
import me.hsgamer.topper.placeholderleaderboard.command.SetTopSignCommand;
import me.hsgamer.topper.placeholderleaderboard.command.SetTopSkullCommand;
import me.hsgamer.topper.placeholderleaderboard.config.MainConfig;
import me.hsgamer.topper.placeholderleaderboard.config.MessageConfig;
import me.hsgamer.topper.placeholderleaderboard.hook.TopPlaceholderExpansion;
import me.hsgamer.topper.placeholderleaderboard.listener.JoinListener;
import me.hsgamer.topper.placeholderleaderboard.manager.SignManager;
import me.hsgamer.topper.placeholderleaderboard.manager.SkullManager;
import me.hsgamer.topper.placeholderleaderboard.manager.TopManager;
import me.hsgamer.topper.spigot.config.DatabaseConfig;
import org.bstats.bukkit.Metrics;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class TopperPlaceholderLeaderboard extends BasePlugin {
    private final MainConfig mainConfig = new MainConfig(this);
    private final MessageConfig messageConfig = new MessageConfig(this);
    private final DatabaseConfig databaseConfig = new DatabaseConfig(this);
    private final TopManager topManager = new TopManager(this);
    private final SignManager signManager = new SignManager(this);
    private final SkullManager skullManager = new SkullManager(this);
    private final TopPlaceholderExpansion topPlaceholderExpansion = new TopPlaceholderExpansion(this);

    @Override
    public void preLoad() {
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

    @Override
    public void load() {
        MessageUtils.setPrefix(MessageConfig.PREFIX::getValue);
        mainConfig.setup();
        messageConfig.setup();
        databaseConfig.setup();
    }

    @Override
    public void enable() {
        topManager.register();
        registerListener(new JoinListener(this));
        topPlaceholderExpansion.register();

        if (Boolean.TRUE.equals(MainConfig.METRICS.getValue())) {
            new Metrics(this, 14938);
        }
    }

    @Override
    public void postEnable() {
        signManager.register();
        skullManager.register();
        registerCommand(new SetTopSignCommand(this));
        registerCommand(new SetTopSkullCommand(this));
        registerCommand(new GetTopListCommand(this));
        registerCommand(new ReloadCommand(this));
    }

    @Override
    public void disable() {
        signManager.unregister();
        skullManager.unregister();
        topPlaceholderExpansion.unregister();
        topManager.unregister();
    }

    @Override
    protected List<Class<?>> getPermissionClasses() {
        return Collections.singletonList(Permissions.class);
    }

    public TopManager getTopManager() {
        return topManager;
    }

    public SignManager getSignManager() {
        return signManager;
    }

    public SkullManager getSkullManager() {
        return skullManager;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public MessageConfig getMessageConfig() {
        return messageConfig;
    }

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }
}
