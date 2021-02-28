package de.hhu.bsinfo.infinileap.example.bench;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.example.base.CommunicationDemo;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@CommandLine.Command(
        name = "memory-bench",
        description = "Benchmarks reading memory from a remote machine."
)
public class MemoryBenchmark extends CommunicationDemo {

    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private static final int DEFAULT_ITERATION_COUNT = 1024;
    private static final int DEFAULT_OPERATION_COUNT = 1024 * 50;

    @CommandLine.Option(
            names = "--bytes",
            description = "The number of bytes to read.")
    private int bufferSize = DEFAULT_BUFFER_SIZE;

    @CommandLine.Option(
            names = "--iterations",
            description = "The number of benchmark iterations.")
    private int iterations = DEFAULT_ITERATION_COUNT;

    @CommandLine.Option(
            names = "--operations",
            description = "The number of operations per iteration.")
    private int operations = DEFAULT_OPERATION_COUNT;

    @Override
    protected void onClientReady(Context context, Worker worker, Endpoint endpoint) throws ControlException {

        // Allocate a memory descriptor
        var descriptor = new MemoryDescriptor();

        // Receive the message
        log.info("Receiving Remote Key");
        worker.receiveTagged(descriptor, Tag.of(0L), new RequestParameters()
                .setReceiveCallback(this::releaseBarrier));

        barrier();

        // Unpack remote key and extract remote address
        var remoteKey = endpoint.unpack(descriptor);
        var remoteAddress = descriptor.remoteAddress();

        // Create buffer and request parameters for subsequent get operations
        var targetBuffer = MemorySegment.allocateNative(descriptor.remoteSize());
        var requestParameters = new RequestParameters()
                .setReceiveCallback(this::releaseBarrier);

        log.info("Using a buffer size of {} bytes", bufferSize);
        log.info("Running for {} iterations with {} operations each", iterations, operations);

        long timer = 0L;
        for (int iteration = 0; iteration < iterations; iteration++) {
            timer = System.nanoTime();
            for (int operation = 0; operation < operations; operation++) {

                // Retrieve memory from remote
                endpoint.get(targetBuffer, remoteAddress, remoteKey, requestParameters);

                // Wait until request completes
                barrier();
            }

            log.info("[{}/{}] {}ms", iteration + 1, iterations, Duration.ofNanos(System.nanoTime() - timer).toMillis());
        }

        // Signal completion
        final var completion = MemorySegment.allocateNative(Byte.BYTES);
        endpoint.sendTagged(completion, Tag.of(0L));
    }

    @Override
    protected void onServerReady(Context context, Worker worker, Endpoint endpoint) throws ControlException {

        // Create initial data buffer
        var content = new byte[bufferSize];
        ThreadLocalRandom.current().nextBytes(content);

        // Create memory segment and fill it with data
        final var source = MemorySegment.ofArray(content);
        final var memoryRegion = context.allocateMemory(bufferSize);
        memoryRegion.segment().copyFrom(source);

        // Send remote key to server
        log.info("Sending remote key");
        final var descriptor = memoryRegion.descriptor();
        endpoint.sendTagged(descriptor, Tag.of(0L), new RequestParameters()
                .setSendCallback(this::releaseBarrier));

        barrier();

        // Wait until remote signals completion
        final var completion = MemorySegment.allocateNative(Byte.BYTES);
        worker.receiveTagged(completion, Tag.of(0L), new RequestParameters()
                .setReceiveCallback(this::releaseBarrier));

        barrier();
    }
}
