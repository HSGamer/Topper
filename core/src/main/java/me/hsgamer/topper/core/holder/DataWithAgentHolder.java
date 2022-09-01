package me.hsgamer.topper.core.holder;

import me.hsgamer.topper.core.agent.Agent;

import java.util.ArrayList;
import java.util.List;

public class DataWithAgentHolder<T> extends DataHolder<T> {
    private final List<Agent> agentList = new ArrayList<>();

    protected DataWithAgentHolder(String name) {
        super(name);
        addRegisterListener(() -> agentList.forEach(Agent::start));
        addBeforeUnregisterListener(() -> agentList.forEach(Agent::beforeStop));
        addUnregisterListener(() -> {
            agentList.forEach(Agent::stop);
            agentList.clear();
        });
    }

    public void addAgent(Agent agent) {
        agentList.add(agent);
    }
}
