package de.hhu.bsinfo.infinileap.engine.util;

import org.agrona.concurrent.Agent;

@FunctionalInterface
public interface AgentProvider<T extends Agent> {
    T provide();
}
