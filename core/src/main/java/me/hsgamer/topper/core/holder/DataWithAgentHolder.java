package me.hsgamer.topper.core.holder;

import me.hsgamer.topper.core.agent.Agent;

import java.util.ArrayList;
import java.util.List;

public abstract class DataWithAgentHolder<T extends Comparable<T>> extends DataHolder<T> {
    private final List<Agent> agentList = new ArrayList<>();

    protected DataWithAgentHolder(String name) {
        super(name);
    }

    public abstract List<Agent> getAgentList();

    @Override
    public void onRegister() {
        super.onRegister();
        agentList.addAll(getAgentList());
        agentList.forEach(Agent::start);
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
        agentList.forEach(Agent::stop);
        agentList.clear();
    }
}
