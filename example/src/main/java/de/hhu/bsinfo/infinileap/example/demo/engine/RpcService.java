package de.hhu.bsinfo.infinileap.example.demo.engine;

import de.hhu.bsinfo.infinileap.binding.Identifier;
import de.hhu.bsinfo.infinileap.common.memory.MemoryUtil;
import de.hhu.bsinfo.infinileap.common.util.Distributable;
import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import de.hhu.bsinfo.infinileap.engine.message.Callback;
import de.hhu.bsinfo.infinileap.engine.message.Handler;
import de.hhu.bsinfo.infinileap.message.TextMessage;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public final class RpcService {

    private final Identifier replyIdentifier = Identifier.of(0x02);

    private final MemorySegment data = MemorySegment.allocateNative(16L, MemorySession.openImplicit());
    private final MemorySegment header = MemorySegment.allocateNative(8L, MemorySession.openImplicit());

    @Handler(identifier = 0x01)
    public void onMessage(TextMessage textMessage, Channel replyChannel) {
        log.debug(textMessage.getContent());
//        if (header != null) logPayload(header);
//        if (body != null) logPayload(body);

//        var reply= TextMessage.newBuilder()
//                .setContent("Hello Back!")
//                .build();

//        replyChannel.send(replyIdentifier, reply, callback);
    }

    @Handler(identifier = 0x02)
    public void onReply(TextMessage textMessage, Channel replyChannel) {
//        if (header != null) logPayload(header);
//        if (body != null) logPayload(body);
    }

    private static void logPayload(MemorySegment payload) {
        final var out = new ByteArrayOutputStream();
        try (var stream = new PrintStream(out)) {
            stream.println("Received payload");
            MemoryUtil.dump(payload, "PAYLOAD", stream);
        }

        log.info(out.toString());
    }

    private final Callback<Void> callback = new Callback<Void>() {

        long counter = 0L;

        @Override
        public void onNext(Void message) {
            // Reply is not supported yet
        }

        @Override
        public void onError(Throwable throwable) {
            log.error("An error occured", throwable);
        }

        @Override
        public void onComplete() {
            log.info("Message {} sent", counter++);
        }
    };
}
