package de.hhu.bsinfo.infinileap.example.demo.engine;

import de.hhu.bsinfo.infinileap.binding.Identifier;
import de.hhu.bsinfo.infinileap.common.memory.MemoryUtil;
import de.hhu.bsinfo.infinileap.common.util.Distributable;
import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import de.hhu.bsinfo.infinileap.engine.message.Callback;
import de.hhu.bsinfo.infinileap.engine.message.Handler;

import java.lang.foreign.*;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public final class RpcService {

    private static final Identifier REPLY_MESSAGE = Identifier.of(0x02);

    private static final ValueLayout.OfLong UNALIGNED_JAVA_LONG = ValueLayout.JAVA_LONG.withBitAlignment(8L);

    @Handler(identifier = 0x01)
    public void onMessage(MemorySegment header, MemorySegment body, Channel replyChannel) {
        var buffer = replyChannel.claimBuffer();
        buffer.segment().set(
            ValueLayout.JAVA_LONG, 0L, header.get(UNALIGNED_JAVA_LONG, 0) + 1
        );

        replyChannel.send(REPLY_MESSAGE, buffer, (int) ValueLayout.JAVA_LONG.byteSize(), callback);
    }

    @Handler(identifier = 0x02)
    public void onReply(MemorySegment header, MemorySegment body, Channel replyChannel) {
        log.info("{}", header.get(UNALIGNED_JAVA_LONG, 0));
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

        }

        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onComplete() {

        }
    };
}
