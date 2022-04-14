package me.hsgamer.topper.spigot;

import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.topper.spigot.command.GetTopListCommand;
import me.hsgamer.topper.spigot.command.ReloadCommand;
import me.hsgamer.topper.spigot.command.SetTopSignCommand;
import me.hsgamer.topper.spigot.command.SetTopSkullCommand;
import me.hsgamer.topper.spigot.config.DatabaseConfig;
import me.hsgamer.topper.spigot.config.MainConfig;
import me.hsgamer.topper.spigot.config.MessageConfig;
import me.hsgamer.topper.spigot.hook.TopPlaceholderExpansion;
import me.hsgamer.topper.spigot.listener.JoinListener;
import me.hsgamer.topper.spigot.manager.SignManager;
import me.hsgamer.topper.spigot.manager.SkullManager;
import me.hsgamer.topper.spigot.manager.TopManager;

public class TopperPlugin extends BasePlugin {
    private final MainConfig mainConfig = new MainConfig(this);
    private final MessageConfig messageConfig = new MessageConfig(this);
    private final DatabaseConfig databaseConfig = new DatabaseConfig(this);
    private final TopManager topManager = new TopManager(this);
    private final SignManager signManager = new SignManager(this);
    private final SkullManager skullManager = new SkullManager(this);
    private final TopPlaceholderExpansion topPlaceholderExpansion = new TopPlaceholderExpansion(this);

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
