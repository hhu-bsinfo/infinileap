package de.hhu.bsinfo.infinileap.example.demo.engine;

import de.hhu.bsinfo.infinileap.binding.ControlException;
import de.hhu.bsinfo.infinileap.binding.Identifier;
import de.hhu.bsinfo.infinileap.common.util.Distributable;
import de.hhu.bsinfo.infinileap.engine.InfinileapEngine;
import de.hhu.bsinfo.infinileap.engine.message.Callback;
import de.hhu.bsinfo.infinileap.example.demo.Atomic;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;
import jdk.incubator.foreign.ValueLayout;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

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

    private final MemorySegment data = MemorySegment.allocateNative(16L, ResourceScope.newImplicitScope());
    private final MemorySegment header = MemorySegment.allocateNative(8L, ResourceScope.newImplicitScope());

    @Override
    public Void call() throws Exception {

        data.set(ValueLayout.JAVA_INT, 0L, 42);
        header.set(ValueLayout.JAVA_INT, 0L, 42);

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

    private void runClient(InfinileapEngine engine) throws ControlException {
        var channel = engine.connect(remoteAddress);
        final Identifier identifier = Identifier.of(0x1);
        final Distributable message = new RpcService.SimpleMessage(0xDEAD, 0xBEEF);


        while (true) {
            channel.send(identifier, message, message, callback);
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
