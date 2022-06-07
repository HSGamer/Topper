package me.hsgamer.topper.spigot.leaderboard;

import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.checker.spigotmc.SpigotVersionChecker;
import me.hsgamer.topper.spigot.config.DatabaseConfig;
import me.hsgamer.topper.spigot.leaderboard.command.GetTopListCommand;
import me.hsgamer.topper.spigot.leaderboard.command.ReloadCommand;
import me.hsgamer.topper.spigot.leaderboard.command.SetTopSignCommand;
import me.hsgamer.topper.spigot.leaderboard.command.SetTopSkullCommand;
import me.hsgamer.topper.spigot.leaderboard.config.MainConfig;
import me.hsgamer.topper.spigot.leaderboard.config.MessageConfig;
import me.hsgamer.topper.spigot.leaderboard.hook.TopPlaceholderExpansion;
import me.hsgamer.topper.spigot.leaderboard.listener.JoinListener;
import me.hsgamer.topper.spigot.leaderboard.manager.SignManager;
import me.hsgamer.topper.spigot.leaderboard.manager.SkullManager;
import me.hsgamer.topper.spigot.leaderboard.manager.TopManager;
import org.bstats.bukkit.Metrics;

import java.util.logging.Level;

public class TopperLeaderboard extends BasePlugin {
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
        Permissions.register();
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
        Permissions.unregister();
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
