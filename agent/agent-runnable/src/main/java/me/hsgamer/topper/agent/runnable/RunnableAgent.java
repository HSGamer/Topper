package me.hsgamer.topper.agent.runnable;

import me.hsgamer.topper.core.agent.Agent;

import java.util.function.UnaryOperator;

public abstract class RunnableAgent implements Agent {
    private UnaryOperator<Runnable> runTaskFunction;
    private Runnable cancelTaskRunnable;

    protected abstract Runnable getRunnable();

    /**
     * Set the function to run the task.
     * The function provides a {@link Runnable} and should return a {@link Runnable} that cancels the task.
     *
     * @param runTaskFunction the function to run the task
     */
    public void setRunTaskFunction(UnaryOperator<Runnable> runTaskFunction) {
        this.runTaskFunction = runTaskFunction;
    }

    @Override
    public void start() {
        if (runTaskFunction == null) {
            throw new IllegalStateException("runTaskFunction is null");
        }
        cancelTaskRunnable = runTaskFunction.apply(getRunnable());
    }

    @Override
    public void stop() {
        if (cancelTaskRunnable == null) return;
        cancelTaskRunnable.run();
    }
}
