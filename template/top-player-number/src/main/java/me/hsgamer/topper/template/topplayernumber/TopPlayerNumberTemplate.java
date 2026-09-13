package me.hsgamer.topper.template.topplayernumber;

import me.hsgamer.topper.agent.core.AgentHolder;
import me.hsgamer.topper.storage.core.DataStorage;
import me.hsgamer.topper.template.topplayernumber.holder.NumberTopHolder;
import me.hsgamer.topper.template.topplayernumber.manager.EntryConsumeManager;
import me.hsgamer.topper.template.topplayernumber.manager.QueryForwardManager;
import me.hsgamer.topper.template.topplayernumber.manager.TopManager;
import me.hsgamer.topper.template.topplayernumber.manager.TopQueryManager;
import me.hsgamer.topper.value.core.ValueProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public abstract class TopPlayerNumberTemplate {
    private final Settings settings;
    private final TopManager topManager;
    private final TopQueryManager topQueryManager;
    private final EntryConsumeManager entryConsumeManager;
    private final QueryForwardManager queryForwardManager;

    protected TopPlayerNumberTemplate(Settings settings) {
        this.settings = settings;
        this.topManager = new TopManager(this);
        this.topQueryManager = new TopQueryManager(this);
        this.entryConsumeManager = new EntryConsumeManager(this);
        this.queryForwardManager = new QueryForwardManager(this);
    }

    public abstract Function<String, DataStorage<UUID, Double>> getStorageSupplier();

    public abstract Optional<ValueProvider<UUID, Double>> createValueProvider(Map<String, Object> settings);

    public abstract Runnable bindTask(AgentHolder<UUID, Double> holder, Runnable runnable, NumberTopHolder.TaskType taskType, Map<String, Object> settings);

    public abstract void logWarning(String message, @Nullable Throwable throwable);

    public abstract String getName(UUID uuid);

    public void logWarning(String message) {
        logWarning(message, null);
    }

    public void modifyNotifiers(NumberTopHolder holder) {

    }

    public void enable() {
        topManager.enable();
        entryConsumeManager.enable();
        queryForwardManager.enable();
    }

    public void disable() {
        queryForwardManager.disable();
        entryConsumeManager.disable();
        topManager.disable();
    }

    public Settings getSettings() {
        return settings;
    }

    public TopManager getTopManager() {
        return topManager;
    }

    public TopQueryManager getTopQueryManager() {
        return topQueryManager;
    }

    public EntryConsumeManager getEntryConsumeManager() {
        return entryConsumeManager;
    }

    public QueryForwardManager getQueryForwardManager() {
        return queryForwardManager;
    }

    public interface Settings {
        Map<String, NumberTopHolder.Settings> holders();

        int taskSaveEntryPerTick();

        int taskUpdateEntryPerTick();

        int taskUpdateMaxSkips();
    }
}
