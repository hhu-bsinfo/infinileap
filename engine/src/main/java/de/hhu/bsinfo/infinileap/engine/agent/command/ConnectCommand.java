package de.hhu.bsinfo.infinileap.engine.agent.command;

import lombok.Data;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public @Data class ConnectCommand extends AgentCommand {

    private final InetSocketAddress remoteAddress;

    private final AtomicInteger channelId = new AtomicInteger();

    private final CountDownLatch latch = new CountDownLatch(1);

    @Override
    public Type type() {
        return Type.CONNECT;
    }
}
