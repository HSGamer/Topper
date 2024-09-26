package me.hsgamer.topper.spigot.agent.runnable;

import io.github.projectunified.minelib.scheduler.common.scheduler.Scheduler;
import io.github.projectunified.minelib.scheduler.common.task.Task;
import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.agent.runnable.RunnableAgent;

public class SpigotRunnableAgent<K, V, A extends Agent<K, V> & Runnable> extends RunnableAgent<K, V, A> {
    private final Scheduler scheduler;
    private final long interval;

    public SpigotRunnableAgent(A agent, Scheduler scheduler, long interval) {
        super(agent);
        this.scheduler = scheduler;
        this.interval = interval;
    }

    @Override
    protected Runnable run(Runnable runnable) {
        Task task = scheduler.runTimer(runnable, interval, interval);
        return task::cancel;
    }
}
