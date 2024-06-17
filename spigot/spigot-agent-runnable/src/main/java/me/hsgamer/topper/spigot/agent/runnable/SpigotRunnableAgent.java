package me.hsgamer.topper.spigot.agent.runnable;

import io.github.projectunified.minelib.scheduler.common.scheduler.Scheduler;
import io.github.projectunified.minelib.scheduler.common.task.Task;
import me.hsgamer.topper.agent.runnable.RunnableAgent;
import me.hsgamer.topper.core.agent.Agent;

public class SpigotRunnableAgent<A extends Agent & Runnable> extends RunnableAgent<A> {
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
