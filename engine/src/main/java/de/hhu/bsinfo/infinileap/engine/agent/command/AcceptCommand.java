package de.hhu.bsinfo.infinileap.engine.agent.command;

import de.hhu.bsinfo.infinileap.binding.ConnectionRequest;
import lombok.Data;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public @Data class AcceptCommand extends AgentCommand {

    private final ConnectionRequest connectionRequest;

    private final AtomicInteger channelId;

    private final CountDownLatch latch = new CountDownLatch(1);

    @Override
    public Type type() {
        return Type.ACCEPT;
    }
}
