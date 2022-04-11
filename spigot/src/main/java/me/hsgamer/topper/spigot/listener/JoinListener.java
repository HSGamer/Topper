package me.hsgamer.topper.spigot.listener;

import me.hsgamer.topper.spigot.TopperPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final TopperPlugin instance;

    public JoinListener(TopperPlugin instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        instance.getTopManager().create(event.getPlayer().getUniqueId());
    }
}
