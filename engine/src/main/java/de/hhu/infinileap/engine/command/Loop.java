package de.hhu.infinileap.engine.command;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.util.MemoryUtil;
import de.hhu.infinileap.engine.InfinileapEngine;
import de.hhu.infinileap.engine.channel.Channel;
import de.hhu.infinileap.engine.message.Handler;
import de.hhu.infinileap.engine.message.Message;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;
import jdk.incubator.foreign.SegmentAllocator;
import jdk.incubator.foreign.ValueLayout;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

@Slf4j
@CommandLine.Command(
        name = "loop",
        description = "starts the infinileap daemon"
)
public class Loop implements Callable<Void> {

    private static final Identifier IDENTIFIER = Identifier.of(0x01);
    private static final Identifier REPLY_IDENTIFIER = Identifier.of(0x02);
    private static final ResourceScope SCOPE = ResourceScope.newImplicitScope();
    private static final RequestParameters REQUEST_PARAMETERS = new RequestParameters()
            .setDataType(DataType.CONTIGUOUS_8_BIT)
            .setFlags(RequestParameters.Flag.ACTIVE_MESSAGE_REPLY)
            .disableImmediateCompletion();

    @CommandLine.Option(
            names = {"-l", "--listen"},
            description = "The address the server listens on.")
    private InetSocketAddress listenAddress;

    @CommandLine.Option(
            names = {"-c", "--connect"},
            description = "The address this instance connects to.")
    private InetSocketAddress remoteAddress;

    @Override
    public Void call() throws Exception {
        var engine = InfinileapEngine.builder()
                .serviceClass(RpcService.class)
                .listenAddress(listenAddress)
                .threadCount(4)
                .build();

        engine.start();

        if (remoteAddress != null) {
            runClient(engine);
        }

        try {
            engine.join();
        } catch (InterruptedException e) {
            log.error("Unexpected interrupt", e);
        }

        return null;
    }

    private void runClient(InfinileapEngine engine) throws ControlException, InterruptedException, ExecutionException {
        var endpoint = engine.connect(remoteAddress);

        var header = MemorySegment.allocateNative(4L, SCOPE);
        var body = MemorySegment.allocateNative(8L, SCOPE);
        body.set(ValueLayout.JAVA_LONG, 0L, 0L);

        var message = new Message(header, body);

        var channel = new Channel(endpoint);
        channel.send(IDENTIFIER, message).get();

        log.info("SENT");
    }

    public static final class RpcService {

        @Handler(identifier = 0x01)
        public void onMessage(MemorySegment header, MemorySegment body, Endpoint replyEndpoint) {
            logPayload(body);

            try (var scope = ResourceScope.newConfinedScope()) {
                var allocator = SegmentAllocator.nativeAllocator(scope);
                replyEndpoint.sendActive(
                        REPLY_IDENTIFIER,
                        allocator.allocate(4L),
                        allocator.allocateUtf8String(String.format("Reply %d", body.get(ValueLayout.JAVA_LONG, 0L))),
                        REQUEST_PARAMETERS
                );
            }
        }

        @Handler(identifier = 0x02)
        public void onReply(MemorySegment header, MemorySegment body, Endpoint replyEndpoint) {
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
    }
}
