package de.hhu.bsinfo.infinileap.engine.message;

import com.google.protobuf.*;
import de.hhu.bsinfo.infinileap.binding.ActiveMessageCallback;
import de.hhu.bsinfo.infinileap.binding.Status;
import de.hhu.bsinfo.infinileap.engine.util.ChannelResolver;
import de.hhu.bsinfo.infinileap.message.TextMessage;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openucx.ucp_am_recv_param_t;

import java.util.concurrent.ExecutorService;

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

    /**
     * Used for deserializing incoming messages.
     */
    private final Parser<? extends MessageLite> parser;

    @Override
    public Status onActiveMessage(MemoryAddress argument, MemorySegment header, MemorySegment body, MemoryAddress parameters) {
        var params = ucp_am_recv_param_t.ofAddress(parameters, ResourceScope.globalScope());
        var endpoint = ucp_am_recv_param_t.reply_ep$get(params);
        var channel = resolver.resolve(endpoint);


        try {
            var parsed = parser.parseFrom(header.asByteBuffer());
            handler.onMessage(parsed, channel);

            return Status.OK;
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }

    }
}
