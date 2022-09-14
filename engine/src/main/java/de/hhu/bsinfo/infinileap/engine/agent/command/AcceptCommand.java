package de.hhu.bsinfo.infinileap.engine.agent.command;

import de.hhu.bsinfo.infinileap.binding.ConnectionRequest;
import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import de.hhu.bsinfo.infinileap.engine.pipeline.ChannelPipeline;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@EqualsAndHashCode(callSuper = true)
public @Data class AcceptCommand extends AgentCommand<Channel> {

    private final ConnectionRequest connectionRequest;

    @Override
    public Type type() {
        return Type.ACCEPT;
    }
}
