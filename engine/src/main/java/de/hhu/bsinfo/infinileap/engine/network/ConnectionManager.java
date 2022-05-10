package de.hhu.bsinfo.infinileap.engine.network;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.engine.agent.ConnectionAgent;
import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import de.hhu.bsinfo.infinileap.engine.util.AgentProvider;
import de.hhu.bsinfo.infinileap.engine.util.BufferPool;
import jdk.incubator.foreign.MemoryAddress;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class ConnectionManager {

    private final ConcurrentMap<MemoryAddress, Channel> channelMap;

    private ListenerParameters listenerParameters;

    private Listener listener;


    public ConnectionManager() {
        this.channelMap = new ConcurrentHashMap<>();
    }

    public Channel resolve(MemoryAddress memoryAddress) {
        return channelMap.get(memoryAddress);
    }
}
