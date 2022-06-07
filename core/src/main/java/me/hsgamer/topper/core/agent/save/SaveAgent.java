package me.hsgamer.topper.core.agent.save;

import me.hsgamer.topper.core.agent.TaskAgent;
import me.hsgamer.topper.core.entry.DataEntry;
import me.hsgamer.topper.core.holder.DataHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SaveAgent<T extends Comparable<T>, R> extends TaskAgent<R> {
    private final Queue<UUID> saveQueue = new ConcurrentLinkedQueue<>();
    private final DataHolder<T> holder;
    private int maxEntryPerCall = 10;

    public SaveAgent(DataHolder<T> holder) {
        this.holder = holder;
    }

    @Override
    protected Runnable getRunnable() {
        return () -> {
            List<UUID> list = new ArrayList<>();
            for (int i = 0; i < maxEntryPerCall; i++) {
                UUID uuid = saveQueue.poll();
                if (uuid == null) {
                    break;
                }
                DataEntry<T> entry = holder.getOrCreateEntry(uuid);
                entry.save();
                list.add(uuid);
            }
            if (!list.isEmpty()) {
                saveQueue.addAll(list);
            }
        };
    }

    public void setMaxEntryPerCall(int maxEntryPerCall) {
        this.maxEntryPerCall = maxEntryPerCall;
    }
}
