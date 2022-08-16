package de.hhu.bsinfo.infinileap.example.demo.engine;

import de.hhu.bsinfo.infinileap.binding.ControlException;
import de.hhu.bsinfo.infinileap.binding.Identifier;
import de.hhu.bsinfo.infinileap.common.memory.MemoryUtil;
import de.hhu.bsinfo.infinileap.engine.InfinileapEngine;
import de.hhu.bsinfo.infinileap.engine.message.Callback;
import de.hhu.bsinfo.infinileap.engine.pipeline.ChannelPipeline;
import de.hhu.bsinfo.infinileap.message.TextMessage;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

@Slf4j
@CommandLine.Command(
        name = "engine",
        description = "starts the infinileap engine"
)
public class Engine implements Callable<Void> {

    @CommandLine.Option(
            names = {"-l", "--listen"},
            description = "The address the server listens on.")
    private InetSocketAddress listenAddress;

    @CommandLine.Option(
            names = {"-c", "--connect"},
            description = "The address this instance connects to.")
    private InetSocketAddress remoteAddress;

    private final MemorySegment data = MemorySegment.allocateNative(16L, MemorySession.openImplicit());

    @Override
    public Void call() throws Exception {

        data.set(ValueLayout.JAVA_INT, 0L, 42);

        var engine = InfinileapEngine.builder()
                .pipelineSupplier(() -> {
                    var pipeline = new ChannelPipeline();
                    pipeline.add((source, segment) -> {
                        log.info("Received new message from {}", source.identifier());
                        MemoryUtil.dump(segment);
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
        var channel = engine.connect(remoteAddress);
        log.info("Established connection with {}", remoteAddress);

        final Identifier identifier = Identifier.of(0x1);
        channel.send(identifier, data, callback);
    }

    private final Callback<Void> callback = new Callback<>() {

        private long counter = 0L;

        @Override
        public void onNext(Void message) {

        }

        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onComplete() {
            log.info("Message {} sent", counter++);
        }
    };
}
