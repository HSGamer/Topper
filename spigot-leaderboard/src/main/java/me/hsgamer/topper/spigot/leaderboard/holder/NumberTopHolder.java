package me.hsgamer.topper.spigot.leaderboard.holder;

import me.hsgamer.topper.core.agent.save.SaveAgent;
import me.hsgamer.topper.core.agent.snapshot.SnapshotAgent;
import me.hsgamer.topper.core.agent.update.UpdateAgent;
import me.hsgamer.topper.core.holder.DataHolder;
import me.hsgamer.topper.core.storage.DataStorage;
import me.hsgamer.topper.spigot.leaderboard.TopperLeaderboard;
import me.hsgamer.topper.spigot.leaderboard.config.MainConfig;
import org.bukkit.scheduler.BukkitTask;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class NumberTopHolder extends DataHolder<Double> {
    protected final TopperLeaderboard instance;
    private final UpdateAgent<Double, BukkitTask> updateAgent;
    private final SaveAgent<Double, BukkitTask> saveAgent;
    private final SnapshotAgent<Double, BukkitTask> snapshotAgent;

    protected NumberTopHolder(TopperLeaderboard instance, Function<DataHolder<Double>, DataStorage<Double>> storageSupplier, String name) {
        super(storageSupplier, name);
        this.instance = instance;
        this.updateAgent = new UpdateAgent<>(this);
        this.saveAgent = new SaveAgent<>(this);
        this.snapshotAgent = new SnapshotAgent<>(this);
    }

    @Override
    public Double getDefaultValue() {
        return 0D;
    }

    protected abstract CompletableFuture<Optional<Double>> updateNewValue(UUID uuid);

    @Override
    public void onRegister() {
        updateAgent.setMaxEntryPerCall(MainConfig.TASK_UPDATE_ENTRY_PER_TICK.getValue());
        updateAgent.setUpdateFunction(this::updateNewValue);
        updateAgent.setRunTaskFunction(runnable -> {
            int updateDelay = MainConfig.TASK_UPDATE_DELAY.getValue();
            return instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, runnable, updateDelay, updateDelay);
        });
        updateAgent.setCancelTaskConsumer(BukkitTask::cancel);
        updateAgent.start();

        saveAgent.setMaxEntryPerCall(MainConfig.TASK_SAVE_ENTRY_PER_TICK.getValue());
        saveAgent.setRunTaskFunction(runnable -> {
            int saveDelay = MainConfig.TASK_SAVE_DELAY.getValue();
            return instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, runnable, saveDelay, saveDelay);
        });
        saveAgent.setCancelTaskConsumer(BukkitTask::cancel);

        snapshotAgent.setRunTaskFunction(runnable -> instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, runnable, 20L, 20L));
        snapshotAgent.setCancelTaskConsumer(BukkitTask::cancel);
        snapshotAgent.start();
    }

    @Override
    public void onUnregister() {
        updateAgent.stop();
        snapshotAgent.stop();
        saveAgent.stop();
        super.onUnregister();
    }

    public UpdateAgent<Double, BukkitTask> getUpdateAgent() {
        return updateAgent;
    }

    public SaveAgent<Double, BukkitTask> getSaveAgent() {
        return saveAgent;
    }

    public SnapshotAgent<Double, BukkitTask> getSnapshotAgent() {
        return snapshotAgent;
    }
}
