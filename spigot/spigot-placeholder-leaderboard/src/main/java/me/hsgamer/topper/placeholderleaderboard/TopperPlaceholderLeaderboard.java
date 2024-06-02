package me.hsgamer.topper.placeholderleaderboard;

import io.github.projectunified.minelib.plugin.base.BasePlugin;
import io.github.projectunified.minelib.plugin.command.CommandComponent;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.checker.spigotmc.SpigotVersionChecker;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
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
import me.hsgamer.topper.spigot.block.BlockEntryConverterRegistry;
import me.hsgamer.topper.spigot.config.DefaultConverterRegistry;
import me.hsgamer.topper.spigot.number.NumberConverterRegistry;
import me.hsgamer.topper.spigot.number.NumberStorageBuilder;
import org.bstats.bukkit.Metrics;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class TopperPlaceholderLeaderboard extends BasePlugin {
    static {
        DefaultConverterRegistry.register();
        NumberConverterRegistry.register();
        BlockEntryConverterRegistry.register();
    }

    @Override
    protected List<Object> getComponents() {
        return Arrays.asList(
                new NumberStorageBuilder(this, new File(getDataFolder(), "top")),
                ConfigGenerator.newInstance(MainConfig.class, new BukkitConfig(this)),
                ConfigGenerator.newInstance(MessageConfig.class, new BukkitConfig(this, "messages.yml")),

                new TopManager(this),
                new SignManager(this),
                new SkullManager(this),
                new TopPlaceholderExpansion(this),

                new Permissions(this),
                new CommandComponent(this, Arrays.asList(
                        new SetTopSignCommand(this),
                        new SetTopSkullCommand(this),
                        new GetTopListCommand(this),
                        new ReloadCommand(this)
                )),
                new JoinListener(this)
        );
    }

    @Override
    public void load() {
        MessageUtils.setPrefix(get(MessageConfig.class)::getPrefix);
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
}
