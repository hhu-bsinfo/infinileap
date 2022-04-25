package de.hhu.bsinfo.infinileap.example.demo.engine;

import de.hhu.bsinfo.infinileap.binding.ControlException;
import de.hhu.bsinfo.infinileap.binding.Identifier;
import de.hhu.bsinfo.infinileap.engine.InfinileapEngine;
import de.hhu.bsinfo.infinileap.engine.message.Callback;
import de.hhu.bsinfo.infinileap.engine.message.Message;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;
import jdk.incubator.foreign.ValueLayout;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

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

    private final ResourceScope resourceScope = ResourceScope.newImplicitScope();

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
        var channel = engine.connect(remoteAddress);
        var message = new Message(
                Identifier.of(0x01),
                MemorySegment.allocateNative(4L, resourceScope),
                MemorySegment.allocateNative(8L, resourceScope)
        );

        message.body().set(ValueLayout.JAVA_LONG, 0L, 0L);
        channel.send(message, new Callback<>() {
            @Override
            public void onNext(Void message) {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {
                log.info("Message sent");
            }
        });
    }
}
