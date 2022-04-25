package de.hhu.bsinfo.infinileap.engine.message;

import de.hhu.bsinfo.infinileap.binding.ActiveMessageCallback;
import de.hhu.bsinfo.infinileap.binding.Status;
import de.hhu.bsinfo.infinileap.engine.util.ChannelResolver;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openucx.ucp_am_recv_param_t;

@Getter
@RequiredArgsConstructor
public class HandlerAdapter extends ActiveMessageCallback {

    private final int identifier;

    private final MessageHandler handler;

    private final ChannelResolver resolver;

    @Override
    public Status onActiveMessage(MemoryAddress argument, MemorySegment header, MemorySegment body, MemoryAddress parameters) {
        var params = ucp_am_recv_param_t.ofAddress(parameters, ResourceScope.globalScope());
        var endpoint = ucp_am_recv_param_t.reply_ep$get(params);
        handler.onMessage(header, body, resolver.resolve(endpoint));
        return Status.OK;
    }
}
