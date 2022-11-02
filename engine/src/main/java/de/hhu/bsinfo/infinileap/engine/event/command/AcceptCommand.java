package de.hhu.bsinfo.infinileap.engine.event.command;

import de.hhu.bsinfo.infinileap.binding.ConnectionRequest;
import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public @Data class AcceptCommand extends EventLoopCommand<Channel> {

    private final ConnectionRequest connectionRequest;

    @Override
    public Type type() {
        return Type.ACCEPT;
    }
}
