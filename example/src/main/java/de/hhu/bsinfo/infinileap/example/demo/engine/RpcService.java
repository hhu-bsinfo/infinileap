package de.hhu.bsinfo.infinileap.example.demo.engine;

import de.hhu.bsinfo.infinileap.binding.Endpoint;
import de.hhu.bsinfo.infinileap.binding.Identifier;
import de.hhu.bsinfo.infinileap.util.MemoryUtil;
import de.hhu.infinileap.engine.channel.Channel;
import de.hhu.infinileap.engine.message.Callback;
import de.hhu.infinileap.engine.message.Handler;
import de.hhu.infinileap.engine.message.Message;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;
import jdk.incubator.foreign.SegmentAllocator;
import jdk.incubator.foreign.ValueLayout;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public final class RpcService {

    private final AtomicInteger atomicInteger = new AtomicInteger();

    @Handler(identifier = 0x01)
    public void onMessage(MemorySegment header, MemorySegment body, Channel replyChannel) {
        logPayload(body);

        try (var scope = ResourceScope.newConfinedScope()) {
            var allocator = SegmentAllocator.nativeAllocator(scope);
            var message = new Message(
                    Identifier.of(0x02),
                    allocator.allocate(4L),
                    allocator.allocateUtf8String(String.format("Reply %d", body.get(ValueLayout.JAVA_LONG, 0L)))
            );

            replyChannel.send(message, callback);
        }
    }

    @Handler(identifier = 0x02)
    public void onReply(MemorySegment header, MemorySegment body, Channel replyChannel) {
        logPayload(body);
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
            log.info("Message sent");
        }
    };
}
