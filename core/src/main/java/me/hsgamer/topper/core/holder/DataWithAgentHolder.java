package me.hsgamer.topper.core.holder;

import me.hsgamer.topper.core.agent.Agent;

import java.util.ArrayList;
import java.util.List;

public class DataWithAgentHolder<T> extends DataHolder<T> {
    private final List<Agent> agentList = new ArrayList<>();

    protected DataWithAgentHolder(String name) {
        super(name);
        getRegisterListenerManager().add(() -> agentList.forEach(Agent::start));
        getBeforeUnregisterListenerManager().add(() -> agentList.forEach(Agent::beforeStop));
        getUnregisterListenerManager().add(() -> {
            agentList.forEach(Agent::stop);
            agentList.clear();
        });
    }

    public void addAgent(Agent agent) {
        agentList.add(agent);
    }
}
