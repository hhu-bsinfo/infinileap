package de.hhu.bsinfo.infinileap.engine.message;

import de.hhu.bsinfo.infinileap.binding.ActiveMessageCallback;
import de.hhu.bsinfo.infinileap.binding.Status;
import de.hhu.bsinfo.infinileap.engine.util.ChannelResolver;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openucx.ucp_am_recv_param_t;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter
@RequiredArgsConstructor
@Slf4j
public class HandlerAdapter extends ActiveMessageCallback {

    /**
     * The identifier associated with this handler.
     */
    private final int identifier;

    /**
     * The actual handler, which gets called whenever a message arrives.
     */
    private final MessageHandler handler;

    /**
     * Maps memory addresses to {@link de.hhu.bsinfo.infinileap.engine.channel.Channel} instances.
     */
    private final ChannelResolver resolver;

    private static final Executor EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public Status onActiveMessage(MemoryAddress argument, MemorySegment header, MemorySegment body, MemoryAddress parameters) {
        var params = ucp_am_recv_param_t.ofAddress(parameters, MemorySession.global());
        var endpoint = ucp_am_recv_param_t.reply_ep$get(params);

        EXECUTOR.execute(() -> handler.onMessage(header, body, resolver.resolve(endpoint.toRawLongValue())));
        return Status.OK;
    }
}
