package de.hhu.bsinfo.infinileap.example.benchmark.command;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.example.base.CommunicationDemo;
import de.hhu.bsinfo.infinileap.example.util.CommunicationBarrier;
import de.hhu.bsinfo.infinileap.example.util.Requests;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

@Slf4j
@CommandLine.Command(
        name = "message-bench",
        description = "Benchmarks sending messages to a remote machine."
)
public class MessageBenchmarkRunner extends CommunicationDemo {

    private static final int DEFAULT_MESSAGE_SIZE = 4096;
    private static final int DEFAULT_ITERATION_COUNT = 1024;
    private static final int DEFAULT_OPERATION_COUNT = 1024 * 50;

    private static final Tag MESSAGE_TAG    = Tag.of(0x1L);
    private static final Tag COMPLETION_TAG = Tag.of(0x2L);

    private static final AtomicLong RECEIVE_COUNTER = new AtomicLong(0L);

    @CommandLine.Option(
            names = "--bytes",
            description = "The number of bytes to send.")
    private int messageSize = DEFAULT_MESSAGE_SIZE;

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

        // Create initial data buffer
        var content = new byte[messageSize];
        ThreadLocalRandom.current().nextBytes(content);

        // Create memory segment and fill it with data
        final var source = MemorySegment.ofArray(content);
        final var memoryRegion = context.allocateMemory(messageSize);
        memoryRegion.segment().copyFrom(source);
        pushResource(memoryRegion);

        log.info("Using a message size of {} bytes", messageSize);
        log.info("Running for {} iterations with {} operations each", iterations, operations);

        var timer = 0L;
        final var segment = memoryRegion.segment();
        final var measurements = new long[operations];
        for (int iteration = 0; iteration < iterations; iteration++) {
            for (int operation = 0; operation < operations; operation++) {

                // Measure start time
                timer = System.nanoTime();

                // Send message
                Requests.poll(worker, endpoint.sendTagged(segment, MESSAGE_TAG));

                // Save measured time
                measurements[operation] = System.nanoTime() - timer;
            }

            log.info("[{}/{}] {}us", iteration + 1, iterations, LongStream.of(measurements).average().getAsDouble() / 1000.0);
        }

        // Signal completion
        final var completion = MemorySegment.allocateNative(Byte.BYTES);
        endpoint.sendTagged(completion, COMPLETION_TAG);
    }

    @Override
    protected void onServerReady(Context context, Worker worker, Endpoint endpoint) throws ControlException {

        // Allocate a buffer for receiving the remote's message
        var buffer = pushResource(MemorySegment.allocateNative(messageSize));

        Request request = null;
        var expectedMessages = (long) iterations * operations;
        log.info("Receiving {} messages", expectedMessages);
        for (long counter = 0; counter < expectedMessages; counter++) {
            Requests.await(worker, worker.receiveTagged(buffer, MESSAGE_TAG));
        }

        // Wait until remote signals completion
        log.info("Waiting for completion signal");
        final var completion = MemorySegment.allocateNative(Byte.BYTES);
        pushResource(
            worker.receiveTagged(completion, COMPLETION_TAG, new RequestParameters()
                .setReceiveCallback(barrier::release))
        );

        Requests.await(worker, barrier);
    }
}
