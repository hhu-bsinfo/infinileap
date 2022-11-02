package de.hhu.bsinfo.infinileap.engine.event.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.InetSocketAddress;

@EqualsAndHashCode(callSuper = true)
public @Data class ListenCommand extends EventLoopCommand<Void> {

    private final InetSocketAddress listenAddress;

    @Override
    public Type type() {
        return Type.LISTEN;
    }
}
