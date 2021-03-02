package de.hhu.bsinfo.infinileap.example.benchmark.connection;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.binding.ContextParameters.Feature;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkDetails;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkInstruction;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkInstruction.OpCode;
import de.hhu.bsinfo.infinileap.example.util.BenchmarkType;
import de.hhu.bsinfo.infinileap.example.util.Constants;
import de.hhu.bsinfo.infinileap.example.util.RequestHelpher;
import de.hhu.bsinfo.infinileap.util.CloseException;
import de.hhu.bsinfo.infinileap.util.ResourcePool;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class BenchmarkConnection implements AutoCloseable {

    private static final long DEFAULT_REQUEST_SIZE = 1024;

    private static final Feature[] FEATURE_SET = {
            Feature.TAG, Feature.RMA, Feature.WAKEUP,
            Feature.ATOMIC_32, Feature.ATOMIC_64, Feature.STREAM
    };

    /**
     * This node's context.
     */
    private final Context context;

    /**
     * This node's worker instance.
     */
    private final Worker worker;

    /**
     * The endpoint used for communication with the other side.
     */
    private final Endpoint endpoint;

    /**
     * The remote address used for memory access operations.
     */
    private MemoryAddress remoteAddress;

    /**
     * Key used for accessing remote memory.
     */
    private RemoteKey remoteKey;

    /**
     * The local buffer used for remote memory access operations.
     */
    private MemorySegment localBuffer;

    private final ResourcePool resources = new ResourcePool();

    private BenchmarkConnection(Context context, Worker worker, Endpoint endpoint) {
        this.context = resources.push(() -> context);
        this.worker = resources.push(() -> worker);
        this.endpoint = resources.push(() -> endpoint);
    }

    public static BenchmarkConnection establish(InetSocketAddress serverAddress) throws ControlException {
        try (var pool = new ResourcePool()) {

            // Create context parameters
            var contextParameters = pool.push(() -> new ContextParameters()
                    .setFeatures(FEATURE_SET)
                    .setRequestSize(DEFAULT_REQUEST_SIZE));

            // Initialize UCP context
            var context = Context.initialize(contextParameters);

            var workerParameters = pool.push(() -> new WorkerParameters()
                    .setThreadMode(ThreadMode.SINGLE));

            // Create a worker
            var worker = context.createWorker(workerParameters);

            var endpointParameters = pool.push(() -> new EndpointParameters()
                    .setRemoteAddress(serverAddress));

            var endpoint = worker.createEndpoint(endpointParameters);

            return new BenchmarkConnection(context, worker, endpoint);
        } catch (CloseException e) {
            throw new RuntimeException(e);
        }
    }

    public void prepare(BenchmarkType type, BenchmarkDetails details) throws ControlException {
        switch (type) {
            case RDMA_LATENCY, RDMA_THROUGHPUT -> prepareMemory(details);
            case MESSAGING_LATENCY -> prepareMessagingLatency(details);
            case MESSAGING_THROUGHPUT -> prepareMessagingThroughput(details);
        }
    }

    private void prepareMemory(BenchmarkDetails details) throws ControlException {

        // Prepare remote for benchmark
        sendOpCode(OpCode.RDMA_THROUGHPUT);
        sendDetails(details);

        // Receive memory region for access operations
        try (var descriptor = receiveDescriptor()) {
            remoteKey = endpoint.unpack(descriptor);
            resources.push(remoteKey);

            localBuffer = MemorySegment.allocateNative(descriptor.remoteSize());
            resources.push(localBuffer);

            remoteAddress = descriptor.remoteAddress();
        }
    }

    private void prepareMessagingLatency(BenchmarkDetails details) {
        sendOpCode(OpCode.MESSAGING_LATENCY);
        sendDetails(details);
    }

    private void prepareMessagingThroughput(BenchmarkDetails details) {
        sendOpCode(OpCode.MESSAGING_THROUGHPUT);
        sendDetails(details);
    }

    private void sendOpCode(OpCode opCode) {
        try (var instruction = new BenchmarkInstruction(opCode)) {
            RequestHelpher.await(
                worker, endpoint.sendTagged(instruction, Constants.TAG_BENCHMARK_OPCODE)
            );
        }
    }

    private void sendDetails(BenchmarkDetails details) {
        RequestHelpher.await(
            worker, endpoint.sendTagged(details, Constants.TAG_BENCHMARK_DETAILS)
        );
    }

    private MemoryDescriptor receiveDescriptor() {
        var descriptor = new MemoryDescriptor();

        RequestHelpher.await(
            worker, worker.receiveTagged(descriptor, Constants.TAG_BENCHMARK_DESCRIPTOR)
        );

        return descriptor;
    }

    /*--- BENCHMARK COMMANDS ---*/

    public final void synchronize() {
        sendOpCode(OpCode.SYNCHRONIZE);
    }

    /*--- BENCHMARK OPERATIONS ---*/

    public final void blockingGet() {
        RequestHelpher.poll(
            worker, endpoint.get(localBuffer, remoteAddress, remoteKey)
        );
    }

    public final void blockingPut() {
        RequestHelpher.poll(
            worker, endpoint.put(localBuffer, remoteAddress, remoteKey)
        );
    }

    @Override
    public void close() {
        try {
            resources.close();
        } catch (CloseException e) {
            log.warn("Closing one or more resources failed", e);
        }
    }
}
