package me.hsgamer.topper.placeholderleaderboard.holder;

import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import io.github.projectunified.minelib.scheduler.global.GlobalScheduler;
import me.hsgamer.topper.agent.agent.update.UpdateAgent;
import me.hsgamer.topper.agent.snapshot.SnapshotAgent;
import me.hsgamer.topper.agent.storage.StorageAgent;
import me.hsgamer.topper.core.holder.DataWithAgentHolder;
import me.hsgamer.topper.placeholderleaderboard.TopperPlaceholderLeaderboard;
import me.hsgamer.topper.placeholderleaderboard.config.MainConfig;
import me.hsgamer.topper.placeholderleaderboard.manager.TopManager;
import me.hsgamer.topper.placeholderleaderboard.provider.ValueProvider;
import me.hsgamer.topper.spigot.agent.runnable.SpigotRunnableAgent;
import org.bukkit.OfflinePlayer;

import java.util.Comparator;
import java.util.UUID;

public class NumberTopHolder extends DataWithAgentHolder<UUID, Double> {
    private final ValueProvider valueProvider;
    private final UpdateAgent<UUID, Double> updateAgent;
    private final StorageAgent<UUID, Double> storageAgent;
    private final SnapshotAgent<UUID, Double> snapshotAgent;

    public NumberTopHolder(TopperPlaceholderLeaderboard instance, String name, ValueProvider valueProvider) {
        super(name);
        this.valueProvider = valueProvider;

        this.updateAgent = new UpdateAgent<>(this);
        updateAgent.setMaxEntryPerCall(instance.get(MainConfig.class).getTaskUpdateEntryPerTick());
        updateAgent.setUpdateFunction(valueProvider::getValue);
        addAgent(new SpigotRunnableAgent<>(updateAgent, AsyncScheduler.get(instance), instance.get(MainConfig.class).getTaskUpdateDelay()));

        this.storageAgent = new StorageAgent<>(instance.getLogger(), instance.get(TopManager.class).getStorageSupplier().getStorage(this));
        storageAgent.setMaxEntryPerCall(instance.get(MainConfig.class).getTaskSaveEntryPerTick());
        getListenerManager().add(StorageAgent.LOAD_EVENT, () -> {
            if (instance.get(MainConfig.class).isLoadAllOfflinePlayers()) {
                GlobalScheduler.get(instance).run(() -> {
                    for (OfflinePlayer player : instance.getServer().getOfflinePlayers()) {
                        getOrCreateEntry(player.getUniqueId());
                    }
                });
            }
        });
        addAgent(new SpigotRunnableAgent<>(storageAgent, AsyncScheduler.get(instance), instance.get(MainConfig.class).getTaskSaveDelay()));

        this.snapshotAgent = new SnapshotAgent<>(this);
        snapshotAgent.setComparator(Comparator.reverseOrder());
        addAgent(new SpigotRunnableAgent<>(snapshotAgent, AsyncScheduler.get(instance), 20L));
    }

    @Override
    public Double getDefaultValue() {
        return valueProvider.getDefaultValue();
    }

    public UpdateAgent<UUID, Double> getUpdateAgent() {
        return updateAgent;
    }

    public StorageAgent<UUID, Double> getStorageAgent() {
        return storageAgent;
    }

    public SnapshotAgent<UUID, Double> getSnapshotAgent() {
        return snapshotAgent;
    }
}
