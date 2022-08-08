package de.hhu.bsinfo.infinileap.engine.agent.command;

import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@EqualsAndHashCode(callSuper = true)
public @Data class ConnectCommand extends AgentCommand<Channel> {

    private final InetSocketAddress remoteAddress;

    @Override
    public Type type() {
        return Type.CONNECT;
    }
}
