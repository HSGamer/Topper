package me.hsgamer.topper.spigot;

import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.topper.spigot.config.MainConfig;
import me.hsgamer.topper.spigot.hooks.TopPlaceholderExpansion;
import me.hsgamer.topper.spigot.listener.JoinListener;
import me.hsgamer.topper.spigot.manager.TopManager;

public class TopperPlugin extends BasePlugin {
    private final MainConfig mainConfig = new MainConfig(this);
    private final TopManager topManager = new TopManager(this);
    private final TopPlaceholderExpansion topPlaceholderExpansion = new TopPlaceholderExpansion(this);

    @Override
    public void load() {
        mainConfig.setup();
    }

    @Override
    public void enable() {
        topManager.setup();
        registerListener(new JoinListener(this));
        topPlaceholderExpansion.register();
    }

    @Override
    public void disable() {
        topPlaceholderExpansion.unregister();
        topManager.clear();
    }

    public TopManager getTopManager() {
        return topManager;
    }
}
