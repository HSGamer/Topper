package me.hsgamer.topper.spigot;

import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.topper.spigot.command.SetTopSignCommand;
import me.hsgamer.topper.spigot.config.DatabaseConfig;
import me.hsgamer.topper.spigot.config.MainConfig;
import me.hsgamer.topper.spigot.config.MessageConfig;
import me.hsgamer.topper.spigot.config.SignConfig;
import me.hsgamer.topper.spigot.hook.TopPlaceholderExpansion;
import me.hsgamer.topper.spigot.listener.JoinListener;
import me.hsgamer.topper.spigot.manager.SignManager;
import me.hsgamer.topper.spigot.manager.TopManager;

public class TopperPlugin extends BasePlugin {
    private final MainConfig mainConfig = new MainConfig(this);
    private final SignConfig signConfig = new SignConfig(this);
    private final MessageConfig messageConfig = new MessageConfig(this);
    private final DatabaseConfig databaseConfig = new DatabaseConfig(this);
    private final TopManager topManager = new TopManager(this);
    private final SignManager signManager = new SignManager(this);
    private final TopPlaceholderExpansion topPlaceholderExpansion = new TopPlaceholderExpansion(this);

    @Override
    public void load() {
        MessageUtils.setPrefix(MessageConfig.PREFIX::getValue);
        mainConfig.setup();
        signConfig.setup();
        messageConfig.setup();
        databaseConfig.setup();
    }

    @Override
    public void enable() {
        Permissions.register();
        topManager.register();
        registerListener(new JoinListener(this));
        topPlaceholderExpansion.register();
    }

    @Override
    public void postEnable() {
        signManager.register();
        registerCommand(new SetTopSignCommand(this));
    }

    @Override
    public void disable() {
        signManager.unregister();
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
}
