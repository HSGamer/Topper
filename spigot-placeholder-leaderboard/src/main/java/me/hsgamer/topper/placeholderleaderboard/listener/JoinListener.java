package me.hsgamer.topper.placeholderleaderboard.listener;

import io.github.projectunified.minelib.plugin.listener.ListenerComponent;
import me.hsgamer.topper.placeholderleaderboard.TopperPlaceholderLeaderboard;
import me.hsgamer.topper.placeholderleaderboard.manager.TopManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements ListenerComponent {
    private final TopperPlaceholderLeaderboard instance;

    public JoinListener(TopperPlaceholderLeaderboard instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        instance.get(TopManager.class).create(event.getPlayer().getUniqueId());
    }
}
