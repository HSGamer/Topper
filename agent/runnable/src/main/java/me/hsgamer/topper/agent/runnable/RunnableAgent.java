package me.hsgamer.topper.agent.runnable;

import me.hsgamer.topper.agent.core.Agent;

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

    public A getAgent() {
        return agent;
    }
}
