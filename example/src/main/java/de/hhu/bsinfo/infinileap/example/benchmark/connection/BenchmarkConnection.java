package de.hhu.bsinfo.infinileap.example.benchmark.connection;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.binding.ContextParameters.Feature;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkDetails;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkInstruction;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkInstruction.OpCode;
import de.hhu.bsinfo.infinileap.example.util.BenchmarkType;
import de.hhu.bsinfo.infinileap.example.util.Constants;
import de.hhu.bsinfo.infinileap.example.util.RandomBytes;
import de.hhu.bsinfo.infinileap.example.util.RequestHelpher;
import de.hhu.bsinfo.infinileap.primitive.NativeInteger;
import de.hhu.bsinfo.infinileap.primitive.NativeLong;
import de.hhu.bsinfo.infinileap.util.CloseException;
import de.hhu.bsinfo.infinileap.util.ResourcePool;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class BenchmarkConnection implements AutoCloseable {

    private static final long DEFAULT_REQUEST_SIZE = 1024;

    private static final Feature[] FEATURE_SET = {
            Feature.TAG, Feature.RMA, Feature.WAKEUP,
            Feature.ATOMIC_32, Feature.ATOMIC_64, Feature.STREAM
    };

    private static final RequestParameters REQUEST_PARAMETERS_ATOMIC_32 = new RequestParameters()
            .setDataType(DataType.CONTIGUOUS_32_BIT);

    private static final RequestParameters REQUEST_PARAMETERS_ATOMIC_64 = new RequestParameters()
            .setDataType(DataType.CONTIGUOUS_64_BIT);

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

    /**
     * The type of benchmark this connection is used for.
     */
    private BenchmarkType type;

    /**
     * 32 bit integer used for atomic operations.
     */
    private NativeInteger nativeInteger;

    /**
     * 64 bit long used for atomic operations.
     */
    private NativeLong nativeLong;

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
        this.type = type;
        switch (type) {
            case MEMORY_ACCESS -> prepareMemory(details);
            case MESSAGING -> prepareMessaging(details);
            case PINGPONG -> preparePingPong(details);
            case ATOMIC -> prepareAtomic(details);
        }
    }

    private void prepareMemory(BenchmarkDetails details) throws ControlException {
        sendOpCode(OpCode.MEMORY_ACCESS);
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

    private void prepareMessaging(BenchmarkDetails details) throws ControlException {
        sendOpCode(OpCode.MESSAGING);
        sendDetails(details);

        localBuffer = MemorySegment.allocateNative(details.getBufferSize());
        resources.push(localBuffer);

        RandomBytes.fill(localBuffer);

        // Clear first byte because it is used as
        // an exit signal at the end of the benchmark
        MemoryAccess.setByte(localBuffer, (byte) 0x00);
    }

    private void preparePingPong(BenchmarkDetails details) throws ControlException {
        sendOpCode(OpCode.PINGPONG);
        sendDetails(details);

        localBuffer = MemorySegment.allocateNative(details.getBufferSize());
        resources.push(localBuffer);

        RandomBytes.fill(localBuffer);

        // Clear first byte because it is used as
        // an exit signal at the end of the benchmark
        MemoryAccess.setByte(localBuffer, (byte) 0x00);
    }

    private void prepareAtomic(BenchmarkDetails details) throws ControlException {
        sendOpCode(OpCode.ATOMIC);
        sendDetails(details);

        // Receive memory region for access operations
        try (var descriptor = receiveDescriptor()) {
            remoteKey = endpoint.unpack(descriptor);
            resources.push(remoteKey);

            var region = context.allocateMemory(descriptor.remoteSize());
            localBuffer = region.segment();

            resources.push(localBuffer);
            resources.push(region);

            if (localBuffer.byteSize() == Constants.ATOMIC_32_BIT) {
                nativeInteger = NativeInteger.map(localBuffer);
                nativeInteger.set(1);
            }

            if (localBuffer.byteSize() == Constants.ATOMIC_64_BIT) {
                nativeLong = NativeLong.map(localBuffer);
                nativeLong.set(1);
            }

            remoteAddress = descriptor.remoteAddress();
        }
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

        // Signal that the last message has been sent.
        // This is necessary because JMH performs as many
        // operations as it can within the specified time frame
        // and the total number of operations cannot be known up front.
        if (type == BenchmarkType.MESSAGING) {
            MemoryAccess.setByte(localBuffer, Constants.LAST_MESSAGE);
            blockingSendTagged();
        }

        if (type == BenchmarkType.PINGPONG) {
            MemoryAccess.setByte(localBuffer, Constants.LAST_MESSAGE);
            blockingPingPongTagged();
        }

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

    public final void blockingSendTagged() {
        RequestHelpher.poll(
            worker, endpoint.sendTagged(localBuffer, Constants.TAG_BENCHMARK_MESSAGE)
        );
    }

    public final void blockingPingPongTagged() {
        RequestHelpher.poll(
                worker, endpoint.sendTagged(localBuffer, Constants.TAG_BENCHMARK_MESSAGE)
        );

        RequestHelpher.poll(
                worker, worker.receiveTagged(localBuffer, Constants.TAG_BENCHMARK_MESSAGE)
        );
    }

    private static final AtomicInteger ATOMIC_COUNTER = new AtomicInteger(0);

    public final void blockingAtomicAdd32() {
        RequestHelpher.poll(
                worker, endpoint.atomic(AtomicOperation.ADD, nativeInteger, remoteAddress, remoteKey, REQUEST_PARAMETERS_ATOMIC_32)
        );

        if (ATOMIC_COUNTER.get() == 1000) {
            RequestHelpher.poll(worker, endpoint.flush());
            ATOMIC_COUNTER.set(0);
        }

        ATOMIC_COUNTER.incrementAndGet();
    }

    public final void blockingAtomicAdd64() {
        RequestHelpher.poll(
                worker, endpoint.atomic(AtomicOperation.ADD, nativeLong, remoteAddress, remoteKey, REQUEST_PARAMETERS_ATOMIC_64)
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
