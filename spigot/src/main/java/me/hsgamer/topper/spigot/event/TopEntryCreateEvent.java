package me.hsgamer.topper.spigot.event;

import me.hsgamer.topper.core.TopEntry;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TopEntryCreateEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final TopEntry topEntry;

    public TopEntryCreateEvent(TopEntry topEntry) {
        this.topEntry = topEntry;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public TopEntry getTopEntry() {
        return topEntry;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
