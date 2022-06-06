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

    private final int identifier;

    private final MessageHandler handler;

    private final ChannelResolver resolver;

    @Override
    public Status onActiveMessage(MemoryAddress argument, MemorySegment header, MemorySegment body, MemoryAddress parameters) {
        var params = ucp_am_recv_param_t.ofAddress(parameters, ResourceScope.globalScope());
        var endpoint = ucp_am_recv_param_t.reply_ep$get(params);
        var channel = resolver.resolve(endpoint);

        try {
            var any = Any.parseFrom(header.asByteBuffer());
            if (any.is(TextMessage.class)) {
                handler.onMessage(any.unpack(TextMessage.class), channel);
            } else {
                log.error("Unrecognized message type");
            }

            return Status.OK;
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }

    }
}
