package de.hhu.bsinfo.infinileap.engine.agent.command;

public abstract class AgentCommand {

    public enum Type {
        CONNECT, LISTEN, ACCEPT
    }

    public abstract Type type();
}
