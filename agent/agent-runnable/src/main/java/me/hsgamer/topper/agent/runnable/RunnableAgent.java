package me.hsgamer.topper.agent.runnable;

import me.hsgamer.topper.core.agent.Agent;

public abstract class RunnableAgent<A extends Agent & Runnable> implements Agent {
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
