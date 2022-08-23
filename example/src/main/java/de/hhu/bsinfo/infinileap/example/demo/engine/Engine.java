package de.hhu.bsinfo.infinileap.example.demo.engine;

import de.hhu.bsinfo.infinileap.binding.ControlException;
import de.hhu.bsinfo.infinileap.binding.Identifier;
import de.hhu.bsinfo.infinileap.common.memory.MemoryUtil;
import de.hhu.bsinfo.infinileap.engine.InfinileapEngine;
import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import de.hhu.bsinfo.infinileap.engine.message.Callback;
import de.hhu.bsinfo.infinileap.engine.pipeline.ChannelPipeline;
import de.hhu.bsinfo.infinileap.message.TextMessage;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

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

    private final MemorySegment data = MemorySegment.allocateNative(16L, MemorySession.openImplicit());

    @Override
    public Void call() throws Exception {

        data.set(ValueLayout.JAVA_INT, 0L, 42);

        var engine = InfinileapEngine.builder()
                .pipelineSupplier(() -> {
                    var pipeline = new ChannelPipeline();
                    pipeline.add((source, segment) -> {
                        log.info("Received new message from {}", source.identifier());
                    });

                    return pipeline;
                })
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

        engine.close();

        return null;
    }

    private void runClient(InfinileapEngine engine) throws ControlException {

        var channels = new Channel[connectionCount];
        for (int i = 0; i < connectionCount; i++) {
            channels[i] = engine.connect(remoteAddress);
        }

        log.info("Established {} connection(s) with {}", connectionCount, remoteAddress);
        final Identifier identifier = Identifier.of(0x1);
        for (int i = 0; i < messageCount; i++) {
            for (int j = 0; j < connectionCount; j++) {
                channels[j].send(identifier, data, callback);
            }
        }
    }

    private final Callback<Void> callback = new Callback<>() {

        private final AtomicLong counter = new AtomicLong(0);

        @Override
        public void onNext(Void message) {

        }

        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onComplete() {
            log.info("Message {} sent", counter.incrementAndGet());
        }
    };
}
