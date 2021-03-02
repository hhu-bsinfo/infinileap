package de.hhu.bsinfo.infinileap.example.benchmark.command;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.example.base.CommunicationDemo;
import de.hhu.bsinfo.infinileap.example.util.CommunicationBarrier;
import de.hhu.bsinfo.infinileap.example.util.RequestHelpher;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.LongStream;

@Slf4j
@CommandLine.Command(
        name = "memory-bench",
        description = "Benchmarks reading memory from a remote machine."
)
public class MemoryBenchmarkRunner extends CommunicationDemo {

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

    private final CommunicationBarrier barrier = new CommunicationBarrier();

    @Override
    protected void onClientReady(Context context, Worker worker, Endpoint endpoint) throws ControlException {

        // Allocate a memory descriptor
        var descriptor = new MemoryDescriptor();

        // Receive the message
        log.info("Receiving Remote Key");
        var request = worker.receiveTagged(descriptor, Tag.of(0L), new RequestParameters()
                .setReceiveCallback(barrier::release));

        RequestHelpher.await(worker, barrier);
        request.release();

        // Unpack remote key and extract remote address
        var remoteKey = endpoint.unpack(descriptor);
        var remoteAddress = descriptor.remoteAddress();
        pushResource(remoteKey);

        // Create buffer and request parameters for subsequent get operations
        var targetBuffer = MemorySegment.allocateNative(descriptor.remoteSize());

        log.info("Using a buffer size of {} bytes", bufferSize);
        log.info("Running for {} iterations with {} operations each", iterations, operations);

        var timer = 0L;
        final var measurements = new long[operations];
        for (int iteration = 0; iteration < iterations; iteration++) {

            for (int operation = 0; operation < operations; operation++) {

                // Measure start time
                timer = System.nanoTime();

                // Read from remote
                RequestHelpher.poll(worker, endpoint.put(targetBuffer, remoteAddress, remoteKey));

                // Save measured time
                measurements[operation] = System.nanoTime() - timer;
            }

            log.info("[{}/{}] {}us", iteration + 1, iterations, LongStream.of(measurements).average().getAsDouble() / 1000.0);
        }

        // Signal completion
        final var completion = MemorySegment.allocateNative(Byte.BYTES);
        RequestHelpher.await(worker, endpoint.sendTagged(completion, Tag.of(0L)));
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
        pushResource(memoryRegion);

        // Send remote key to server
        log.info("Sending remote key");
        final var descriptor = memoryRegion.descriptor();
        var request = endpoint.sendTagged(descriptor, Tag.of(0L), new RequestParameters()
                .setSendCallback(barrier::release));

        RequestHelpher.await(worker, barrier);
        request.release();

        // Wait until remote signals completion
        final var completion = MemorySegment.allocateNative(Byte.BYTES);
        request = worker.receiveTagged(completion, Tag.of(0L), new RequestParameters()
                .setReceiveCallback(barrier::release));

        RequestHelpher.await(worker, barrier);
        request.release();
    }
}
