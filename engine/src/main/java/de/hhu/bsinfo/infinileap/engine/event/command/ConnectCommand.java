package de.hhu.bsinfo.infinileap.engine.event.command;

import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.InetSocketAddress;

@EqualsAndHashCode(callSuper = true)
public @Data class ConnectCommand extends EventLoopCommand<Channel> {

    private final InetSocketAddress remoteAddress;

    @Override
    public Type type() {
        return Type.CONNECT;
    }
}
