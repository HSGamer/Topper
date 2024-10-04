package me.hsgamer.topper.spigot.plugin.hook;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.hook.papi.PlaceholderAPIHook;

import java.util.ArrayList;
import java.util.List;

public class HookSystem implements Loadable {
    private final TopperPlugin instance;
    private final List<Loadable> hooks = new ArrayList<>();

    public HookSystem(TopperPlugin instance) {
        this.instance = instance;
    }

    private void registerHooks() {
        if (instance.getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            hooks.add(new PlaceholderAPIHook(instance));
        }
    }

    @Override
    public void load() {
        registerHooks();
        hooks.forEach(Loadable::load);
    }

    @Override
    public void enable() {
        hooks.forEach(Loadable::enable);
    }

    @Override
    public void disable() {
        hooks.forEach(Loadable::disable);
    }
}
