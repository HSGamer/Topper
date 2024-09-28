package me.hsgamer.topper.agent.runnable;

import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.core.DataEntry;

public abstract class RunnableAgent<K, V, A extends Agent<K, V> & Runnable> implements Agent<K, V> {
    private final A agent;
    private Runnable cancelTaskRunnable;

    public RunnableAgent(A agent) {
        this.agent = agent;
    }

    /**
     * This method is used to run the task and return a {@link Runnable} to cancel it
     *
     * @param runnable the task to run
     * @return the {@link Runnable} to cancel the task
     */
    protected abstract Runnable run(Runnable runnable);

    @Override
    public void start() {
        agent.start();
        cancelTaskRunnable = run(agent);
    }

    @Override
    public void stop() {
        if (cancelTaskRunnable != null) {
            cancelTaskRunnable.run();
        }
        agent.stop();
    }

    @Override
    public void beforeStop() {
        agent.beforeStop();
    }

    @Override
    public void onCreate(DataEntry<K, V> entry) {
        agent.onCreate(entry);
    }

    @Override
    public void onUpdate(DataEntry<K, V> entry) {
        agent.onUpdate(entry);
    }

    @Override
    public void onRemove(DataEntry<K, V> entry) {
        agent.onRemove(entry);
    }

    @Override
    public void onUnregister(DataEntry<K, V> entry) {
        agent.onUnregister(entry);
    }

    public A getAgent() {
        return agent;
    }
}
