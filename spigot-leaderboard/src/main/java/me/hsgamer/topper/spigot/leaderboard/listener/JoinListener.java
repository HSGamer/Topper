package me.hsgamer.topper.spigot.leaderboard.listener;

import me.hsgamer.topper.spigot.leaderboard.TopperLeaderboard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final TopperLeaderboard instance;

    public JoinListener(TopperLeaderboard instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        instance.getTopManager().create(event.getPlayer().getUniqueId());
    }
}
