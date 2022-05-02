package de.hhu.bsinfo.infinileap.example.demo.engine;

import de.hhu.bsinfo.infinileap.binding.Identifier;
import de.hhu.bsinfo.infinileap.common.memory.MemoryUtil;
import de.hhu.bsinfo.infinileap.common.util.Distributable;
import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import de.hhu.bsinfo.infinileap.engine.message.Callback;
import de.hhu.bsinfo.infinileap.engine.message.Handler;
import de.hhu.bsinfo.infinileap.engine.message.Message;
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

    private final Distributable replyMessage = new SimpleMessage(0xCAFE, 0xF00D);
    private final Identifier replyIdentifier = Identifier.of(0x02);

    private final MemorySegment data = MemorySegment.allocateNative(16L, ResourceScope.newImplicitScope());
    private final MemorySegment header = MemorySegment.allocateNative(8L, ResourceScope.newImplicitScope());

    @Handler(identifier = 0x01)
    public void onMessage(MemorySegment header, MemorySegment body, Channel replyChannel) {
        if (header != null) logPayload(header);
        if (body != null) logPayload(body);

        replyChannel.send(replyIdentifier, this.replyMessage, this.replyMessage, callback);
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

    public static final class SimpleMessage implements Distributable {

        private int firstNumber;
        private int secondNumber;

        public SimpleMessage(int firstNumber, int secondNumber) {
            this.firstNumber = firstNumber;
            this.secondNumber = secondNumber;
        }

        @Override
        public long writeTo(MemorySegment target) {
            target.set(ValueLayout.JAVA_INT, 0L, firstNumber);
            target.set(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT.byteSize(), secondNumber);
            return ValueLayout.JAVA_INT.byteSize() * 2;
        }

        @Override
        public void readFrom(MemorySegment source) {
            firstNumber = source.get(ValueLayout.JAVA_INT, 0L);
            secondNumber = source.get(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT.byteSize());
        }
    }
}
