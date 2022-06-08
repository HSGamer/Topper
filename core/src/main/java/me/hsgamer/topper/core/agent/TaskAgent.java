package me.hsgamer.topper.core.agent;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class TaskAgent<R> implements Agent {
    private R task;
    private Function<Runnable, R> runTaskFunction;
    private Consumer<R> cancelTaskConsumer;

    protected abstract Runnable getRunnable();

    public void setRunTaskFunction(Function<Runnable, R> runTaskFunction) {
        this.runTaskFunction = runTaskFunction;
    }

    public void setCancelTaskConsumer(Consumer<R> cancelTaskConsumer) {
        this.cancelTaskConsumer = cancelTaskConsumer;
    }

    @Override
    public void start() {
        if (runTaskFunction == null) {
            throw new IllegalStateException("runTaskFunction is null");
        }
        if (cancelTaskConsumer == null) {
            throw new IllegalStateException("cancelTaskConsumer is null");
        }
        task = runTaskFunction.apply(getRunnable());
    }

    @Override
    public void stop() {
        if (task == null) return;
        cancelTaskConsumer.accept(task);
    }

    public R getTask() {
        return task;
    }
}
