package me.hsgamer.topper.spigot.placeholderleaderboard.listener;

import me.hsgamer.topper.spigot.placeholderleaderboard.TopperPlaceholderLeaderboard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final TopperPlaceholderLeaderboard instance;

    public JoinListener(TopperPlaceholderLeaderboard instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        instance.getTopManager().create(event.getPlayer().getUniqueId());
    }
}
