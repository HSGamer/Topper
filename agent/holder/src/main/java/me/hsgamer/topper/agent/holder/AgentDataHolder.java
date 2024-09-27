package me.hsgamer.topper.agent.holder;

import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.core.DataEntry;
import me.hsgamer.topper.core.DataHolder;

import java.util.ArrayList;
import java.util.List;

public class AgentDataHolder<K, V> extends DataHolder<K, V> {
    private final List<Agent<K, V>> agentList = new ArrayList<>();

    public AgentDataHolder(String name) {
        super(name);
    }

    public void addAgent(Agent<K, V> agent) {
        agentList.add(agent);
    }

    public void removeAgent(Agent<K, V> agent) {
        agentList.remove(agent);
    }

    @Override
    protected final void onCreate(DataEntry<K, V> entry) {
        agentList.forEach(agent -> agent.onCreate(entry));
    }

    @Override
    protected final void onUpdate(DataEntry<K, V> entry) {
        agentList.forEach(agent -> agent.onUpdate(entry));
    }

    @Override
    protected final void onRemove(DataEntry<K, V> entry) {
        agentList.forEach(agent -> agent.onRemove(entry));
    }

    public final void register() {
        agentList.forEach(Agent::start);
    }

    public final void unregister() {
        agentList.forEach(Agent::beforeStop);

        getEntryMap().values().forEach(entry -> agentList.forEach(agent -> agent.onUnregister(entry)));
        clear();

        agentList.forEach(Agent::stop);
    }
}