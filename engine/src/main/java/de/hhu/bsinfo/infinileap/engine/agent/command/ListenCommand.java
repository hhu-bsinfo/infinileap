package de.hhu.bsinfo.infinileap.engine.agent.command;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

public @Data class ListenCommand extends AgentCommand<Void> {

    private final InetSocketAddress listenAddress;

    @Override
    public Type type() {
        return Type.LISTEN;
    }
}
