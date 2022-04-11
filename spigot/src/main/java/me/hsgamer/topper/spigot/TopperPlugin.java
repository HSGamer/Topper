package me.hsgamer.topper.spigot;

import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.topper.spigot.config.MainConfig;
import me.hsgamer.topper.spigot.listener.JoinListener;
import me.hsgamer.topper.spigot.manager.TopManager;

public class TopperPlugin extends BasePlugin {
    private final MainConfig mainConfig = new MainConfig(this);
    private final TopManager topManager = new TopManager(this);

    @Override
    public void load() {
        mainConfig.setup();
    }

    @Override
    public void enable() {
        topManager.setup();

        registerListener(new JoinListener(this));
    }

    @Override
    public void disable() {
        topManager.clear();
    }

    public TopManager getTopManager() {
        return topManager;
    }
}
