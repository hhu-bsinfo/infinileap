package de.hhu.bsinfo.infinileap.engine.network;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.lang.foreign.MemorySegment;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class ConnectionManager {

    private final ConcurrentMap<MemorySegment, Channel> channelMap;

    private ListenerParameters listenerParameters;

    private Listener listener;


    public ConnectionManager() {
        this.channelMap = new ConcurrentHashMap<>();
    }

    public Channel resolve(MemorySegment memoryAddress) {
        return channelMap.get(memoryAddress);
    }
}
