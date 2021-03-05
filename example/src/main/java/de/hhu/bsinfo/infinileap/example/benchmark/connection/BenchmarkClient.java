package de.hhu.bsinfo.infinileap.example.benchmark.connection;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkDetails;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkInstruction.OpCode;
import de.hhu.bsinfo.infinileap.example.util.*;
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
public class BenchmarkClient implements AutoCloseable {

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
     * The local memory region used for remote memory access operations.
     */
    private MemoryRegion sendRegion;

    /**
     * The local buffer used for remote memory access operations.
     */
    private MemorySegment sendBuffer;

    /**
     * The local memory region used for remote memory access operations.
     */
    private MemoryRegion receiveRegion;

    /**
     * The local buffer used for remote memory access operations.
     */
    private MemorySegment receiveBuffer;

    /**
     * The initial instruction sent to the server.
     */
    private OpCode initialInstruction;

    /**
     * 32 bit integer used for atomic operations.
     */
    private NativeInteger nativeInteger;

    /**
     * 64 bit long used for atomic operations.
     */
    private NativeLong nativeLong;

    private final ResourcePool resources = new ResourcePool();

    private BenchmarkClient(Context context, Worker worker, Endpoint endpoint) {
        this.context = resources.push(() -> context);
        this.worker = resources.push(() -> worker);
        this.endpoint = resources.push(() -> endpoint);
    }

    public static BenchmarkClient connect(InetSocketAddress serverAddress) throws ControlException {
        var connectionResources = ConnectionResources.create();
        var context = connectionResources.context();
        var worker = connectionResources.worker();

        try (var pool = new ResourcePool()) {
            var endpointParameters = pool.push(() -> new EndpointParameters()
                    .setRemoteAddress(serverAddress));

            return new BenchmarkClient(context, worker, worker.createEndpoint(endpointParameters));
        } catch (CloseException e) {
            throw new RuntimeException(e);
        }
    }

    public void prepare(OpCode initialInstruction, BenchmarkDetails details) throws ControlException {

        // Remember initial instructions for exit signal
        this.initialInstruction = initialInstruction;

        // Initialize Server
        Requests.sendOpCode(worker, endpoint, initialInstruction);
        Requests.sendDetails(worker, endpoint, details);

        // Initialize local buffers
        initBuffers(details.getBufferSize());

        switch (this.initialInstruction) {
            case RUN_READ_LATENCY,
                 RUN_WRITE_LATENCY,
                 RUN_ATOMIC_LATENCY
                    -> initMemory(details);

            case RUN_SEND_LATENCY,
                 RUN_PINGPONG_LATENCY
                    -> initMessaging(details);
        }
    }

    private void initMemory(BenchmarkDetails details) throws ControlException {

    }

    private void initMessaging(BenchmarkDetails details) throws ControlException {
        RandomBytes.fill(sendBuffer);

        // Clear first byte because it is used as
        // an exit signal at the end of the benchmark
        MemoryAccess.setByte(sendBuffer, (byte) 0x00);
    }

    private void initBuffers(long size) throws ControlException {
        sendRegion = context.allocateMemory(size);
        receiveRegion = context.allocateMemory(size);
        resources.push(sendRegion);
        resources.push(receiveRegion);

        sendBuffer = sendRegion.segment();
        receiveBuffer = receiveRegion.segment();
        resources.push(sendBuffer);
        resources.push(receiveBuffer);

        if (sendBuffer.byteSize() == Constants.ATOMIC_32_BIT) {
            nativeInteger = NativeInteger.map(sendBuffer);
            nativeInteger.set(1);
        }

        if (sendBuffer.byteSize() == Constants.ATOMIC_64_BIT) {
            nativeLong = NativeLong.map(sendBuffer);
            nativeLong.set(1);
        }

        // Exchange memory region information
        Requests.sendDescriptor(worker, endpoint, receiveRegion.descriptor());
        try (var descriptor = Requests.receiveDescriptor(worker)) {
            remoteKey = endpoint.unpack(descriptor);
            remoteAddress = descriptor.remoteAddress();
            resources.push(remoteKey);
        }
    }

    /*--- BENCHMARK COMMANDS ---*/

    public final void synchronize() {

        // Signal that the last message has been sent.
        // This is necessary because JMH performs as many
        // operations as it can within the specified time frame
        // and the total number of operations cannot be known up front.
        if (initialInstruction == OpCode.RUN_SEND_LATENCY) {
            MemoryAccess.setByte(sendBuffer, Constants.LAST_MESSAGE);
            blockingSendTagged();
        }

        if (initialInstruction == OpCode.RUN_WRITE_LATENCY) {
            MemoryAccess.setByteAtOffset(sendBuffer, 1, Constants.LAST_MESSAGE);
            blockingPut();
        }

        if (initialInstruction == OpCode.RUN_PINGPONG_LATENCY) {
            MemoryAccess.setByte(sendBuffer, Constants.LAST_MESSAGE);
            blockingPingPongTagged();
        }

        // Release barrier at the remote
        BenchmarkBarrier.signal(worker, endpoint);
    }

    /*--- BENCHMARK OPERATIONS ---*/

    public final void blockingGet() {
        Requests.blockingGet(worker, endpoint, receiveBuffer, remoteAddress, remoteKey);
    }

    private byte writeCounter = 1;

    public final void blockingPut() {
        MemoryAccess.setByte(sendBuffer, writeCounter);
        Requests.blockingPut(worker, endpoint, sendBuffer, remoteAddress, remoteKey);
        while (MemoryAccess.getByte(receiveBuffer) != writeCounter) {
            worker.progress();
        }

        writeCounter++;
    }

    public final void blockingSendTagged() {
        Requests.blockingSendTagged(worker, endpoint, sendBuffer);
    }

    public final void blockingReceiveTagged() {
        Requests.blockingReceiveTagged(worker, endpoint, receiveBuffer);
    }

    public final void blockingPingPongTagged() {
        blockingSendTagged();
        blockingReceiveTagged();
    }

    private static final AtomicInteger ATOMIC_COUNTER = new AtomicInteger(0);

    public final void blockingAtomicAdd32() {
        Requests.blockingAtomicAdd(worker, endpoint, nativeInteger, remoteAddress, remoteKey);

        if (ATOMIC_COUNTER.get() == 1000) {
            Requests.poll(worker, endpoint.flush());
            ATOMIC_COUNTER.set(0);
            return;
        }

        ATOMIC_COUNTER.incrementAndGet();
    }

    public final void blockingAtomicAdd64() {
        Requests.blockingAtomicAdd(worker, endpoint, nativeLong, remoteAddress, remoteKey);

        if (ATOMIC_COUNTER.get() == 1000) {
            Requests.poll(worker, endpoint.flush());
            ATOMIC_COUNTER.set(0);
            return;
        }

        ATOMIC_COUNTER.incrementAndGet();
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
