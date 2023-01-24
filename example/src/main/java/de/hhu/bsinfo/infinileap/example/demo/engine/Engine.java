package de.hhu.bsinfo.infinileap.example.demo.engine;

import de.hhu.bsinfo.infinileap.binding.ControlException;
import de.hhu.bsinfo.infinileap.binding.Identifier;
import de.hhu.bsinfo.infinileap.engine.InfinileapEngine;
import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import de.hhu.bsinfo.infinileap.engine.message.Callback;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;
import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

@Slf4j
@CommandLine.Command(
        name = "engine",
        description = "starts the infinileap engine"
)
public class Engine implements Callable<Void> {

    @CommandLine.Option(
            names = {"--listen"},
            description = "The address the server listens on.")
    private InetSocketAddress listenAddress;

    @CommandLine.Option(
            names = {"--connect"},
            description = "The address this instance connects to.")
    private InetSocketAddress remoteAddress;

    @CommandLine.Option(
            names = {"--messages"},
            description = "The number of messages to send.")
    private int messageCount = 1;

    @CommandLine.Option(
            names = {"--connections"},
            description = "The number of messages to send.")
    private int connectionCount = 1;

    @CommandLine.Option(
            names = {"--concurrency"},
            description = "The maximum number of parallel requests."
    )
    private int maxParallelMessages = 16;

    @CommandLine.Option(
            names = {"--size"},
            description = "The maximum number of bytes a message can hold."
    )
    private int maxMessageSize = 16;

    @CommandLine.Option(
            names = {"--workers"},
            description = "The number of worker threads to use."
    )
    private int workers = 4;

    private final MemorySegment data = MemorySegment.allocateNative(16L, MemorySession.openImplicit());

    private static final Identifier SIMPLE_MESSAGE = Identifier.of(0x01);

    @Override
    public Void call() throws Exception {

//        var loop = new PhasedEventLoop(Duration.ofSeconds(5), Duration.ofSeconds(6), Duration.ofMillis(100)) {
//
//            private final long now = System.currentTimeMillis();
//
//            @Override
//            protected LoopStatus doWork() throws Exception {
//                return System.currentTimeMillis() - now > 5000L ? LoopStatus.IDLE : LoopStatus.ACTIVE;
//            }
//        };
//
//        loop.start(Thread::new);
//        loop.waitOnStart();
//        loop.join();
//
//        log.info("Thread termianted");

        data.set(ValueLayout.JAVA_INT, 0L, 42);

        var engine = InfinileapEngine.builder()
                .serviceClass(RpcService.class)
                .listenAddress(listenAddress)
                .maxMessageSize(maxMessageSize)
                .maxParallelRequests(maxParallelMessages)
                .threadCount(workers)
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

        engine.close();

        return null;
    }

    private void runClient(InfinileapEngine engine) throws ControlException {
        var channels = new Channel[connectionCount];
        for (int i = 0; i < connectionCount; i++) {
            channels[i] = engine.connect(remoteAddress);
        }

        var size = (int) ValueLayout.JAVA_LONG.byteSize();

        log.info("Established {} connection(s) with {}", connectionCount, remoteAddress);
        for (int i = 0; i < messageCount; i++) {
            for (int j = 0; j < connectionCount; j++) {
                var buffer = channels[j].claimBuffer();
                buffer.segment().set(ValueLayout.JAVA_LONG, 0L, i);
                channels[j].send(SIMPLE_MESSAGE, buffer, size, callback);
            }
        }
    }

    private final Callback<Void> callback = new Callback<>() {

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
