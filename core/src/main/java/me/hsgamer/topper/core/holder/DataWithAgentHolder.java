package me.hsgamer.topper.core.holder;

import me.hsgamer.topper.core.agent.Agent;

import java.util.ArrayList;
import java.util.List;

public class DataWithAgentHolder<K, V> extends DataHolder<K, V> {
    private final List<Agent> agentList = new ArrayList<>();

    protected DataWithAgentHolder(String name) {
        super(name);
        getListenerManager().add(EventStates.REGISTER, () -> agentList.forEach(Agent::start));
        getListenerManager().add(EventStates.BEFORE_UNREGISTER, () -> agentList.forEach(Agent::beforeStop));
        getListenerManager().add(EventStates.UNREGISTER, () -> {
            agentList.forEach(Agent::stop);
            agentList.clear();
        });
    }

    public void addAgent(Agent agent) {
        agentList.add(agent);
    }
}
