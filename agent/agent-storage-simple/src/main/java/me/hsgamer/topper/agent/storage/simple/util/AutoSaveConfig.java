package me.hsgamer.topper.agent.storage.simple.util;

import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.DecorativeConfig;
import me.hsgamer.hscore.config.PathString;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

public class AutoSaveConfig extends DecorativeConfig {
    private final UnaryOperator<Runnable> runTaskFunction;
    private final AtomicBoolean isSaving = new AtomicBoolean(false);
    private final AtomicReference<Runnable> currentSaveCancelTask = new AtomicReference<>();

    public AutoSaveConfig(Config config, UnaryOperator<Runnable> runTaskFunction) {
        super(config);
        this.runTaskFunction = runTaskFunction;
    }

    @Override
    public void set(PathString path, Object value) {
        super.set(path, value);
        if (!isSaving.get()) {
            isSaving.set(true);
            Runnable cancelRunnable = runTaskFunction.apply(() -> {
                save();
                isSaving.set(false);
            });
            currentSaveCancelTask.set(cancelRunnable);
        }
    }

    public void finalSave() {
        Optional.ofNullable(currentSaveCancelTask.getAndSet(null)).ifPresent(task -> {
            try {
                task.run();
            } catch (Exception ignored) {
                // IGNORED
            }
        });
        if (isSaving.get()) return;
        save();
    }
}
