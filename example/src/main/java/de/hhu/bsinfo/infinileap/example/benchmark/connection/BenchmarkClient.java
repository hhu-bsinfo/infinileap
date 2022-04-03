package de.hhu.bsinfo.infinileap.example.benchmark.connection;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkDetails;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkInstruction.OpCode;
import de.hhu.bsinfo.infinileap.example.util.*;
import de.hhu.bsinfo.infinileap.primitive.NativeInteger;
import de.hhu.bsinfo.infinileap.primitive.NativeLong;
import de.hhu.bsinfo.infinileap.util.CloseException;
import de.hhu.bsinfo.infinileap.util.MemoryAlignment;
import de.hhu.bsinfo.infinileap.util.ResourcePool;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ValueLayout;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

import static de.hhu.bsinfo.infinileap.binding.AtomicOperation.*;

@Slf4j
public class BenchmarkClient implements AutoCloseable {

    /**
     * The number of atomics operations before the
     * endpoint's internal queue is flushed.
     */
    private static final int ATOMIC_FLUSH_THRESHOLD = 1000;

    private static final int MAX_OUTSTANDING = 32;

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
     * The mode this benchmark is running.
     */
    private BenchmarkDetails.Mode benchmarkMode;

    /**
     * 32 bit integer used for atomic operations.
     */
    private NativeInteger nativeInteger;

    /**
     * 64 bit long used for atomic operations.
     */
    private NativeLong nativeLong;

    /**
     * A pool of requests for performing multiple operations at once.
     */
    private RequestPool requestPool;

    /**
     * The number of operations to perform simultaneously.
     */
    private int operationCount;

    /**
     * Used for flushing the endpoint's internal queue.
     */
    private int atomicCounter = 0;

    private NativeInteger integerReply;
    private RequestParameters atomicIntegerParameters;

    private NativeLong longReply;
    private RequestParameters atomicLongParameters;

    private final ResourcePool resources = new ResourcePool();

    private BenchmarkClient(Context context, Worker worker, Endpoint endpoint) {
        this.context = resources.push(context);
        this.worker = resources.push(worker);
        this.endpoint = endpoint;
    }

    public static BenchmarkClient connect(InetSocketAddress serverAddress) throws ControlException {
        var connectionResources = ConnectionResources.create();
        var context = connectionResources.context();
        var worker = connectionResources.worker();
        var endpointParameters = new EndpointParameters()
                .setRemoteAddress(serverAddress);

        return new BenchmarkClient(context, worker, worker.createEndpoint(endpointParameters));
    }

    public void prepare(OpCode initialInstruction, BenchmarkDetails details) throws ControlException, InterruptedException {
        // Remember initial instructions for exit signal
        this.initialInstruction = initialInstruction;
        this.benchmarkMode = details.getBenchmarkMode();
        this.operationCount = details.getOperationCount();

        // Initialize Server
        Requests.sendOpCode(worker, endpoint, initialInstruction);
        Requests.sendDetails(worker, endpoint, details);

        // Initialize local buffers
        initBuffers(details);

        // Initialize request pool
        requestPool = new RequestPool(details.getOperationCount() * 2);

        // Initialize benchmark-specific resources
        BenchmarkBarrier.signal(worker, endpoint);
        switch (this.initialInstruction) {
            case RUN_READ,
                    RUN_WRITE,
                    RUN_ATOMIC
                    -> initMemory(details);

            case RUN_SEND,
                 RUN_PINGPONG
                    -> initMessaging(details);
        }
    }

    private void initMemory(BenchmarkDetails details) throws ControlException {

    }

    private void initMessaging(BenchmarkDetails details) throws ControlException {
        RandomBytes.fill(sendBuffer);

        // Clear first byte because it is used as
        // an exit signal at the end of the benchmark
        sendBuffer.set(ValueLayout.JAVA_BYTE, 0L, (byte) 0x00);
    }

    private void initBuffers(BenchmarkDetails details) throws ControlException, InterruptedException {
        sendRegion = context.allocateMemory(details.getBufferSize(), MemoryAlignment.PAGE);
        receiveRegion = context.allocateMemory(details.getBufferSize(), MemoryAlignment.PAGE);
        resources.push(sendRegion);
        resources.push(receiveRegion);

        sendBuffer = sendRegion.segment();
        receiveBuffer = receiveRegion.segment();


        // Initialize resources for atomic operations
        if (details.getBufferSize() == Constants.ATOMIC_32_BIT) {
            nativeInteger = NativeInteger.map(sendBuffer);
            nativeInteger.set(1);
        }

        if (details.getBufferSize() == Constants.ATOMIC_64_BIT) {
            nativeLong = NativeLong.map(sendBuffer);
            nativeLong.set(1);
        }

        var integerRegion = context.allocateMemory(Integer.BYTES, MemoryAlignment.PAGE);
        resources.push(integerRegion);
        integerReply = NativeInteger.map(integerRegion.segment());
        atomicIntegerParameters = new RequestParameters()
                .setDataType(DataType.CONTIGUOUS_32_BIT)
                .setReplyBuffer(integerRegion.segment());

        var longRegion = context.allocateMemory(Long.BYTES, MemoryAlignment.PAGE);
        resources.push(longRegion);
        longReply = NativeLong.map(longRegion.segment());
        atomicLongParameters = new RequestParameters()
                .setDataType(DataType.CONTIGUOUS_32_BIT)
                .setReplyBuffer(longRegion.segment());

        // Exchange memory region information
        Requests.sendDescriptor(worker, endpoint, receiveRegion.descriptor());
        var descriptor = Requests.receiveDescriptor(worker);

        remoteKey = endpoint.unpack(descriptor);
        remoteAddress = descriptor.remoteAddress();
        resources.push(remoteKey);
    }

    /*--- BENCHMARK COMMANDS ---*/

    public final void synchronize() throws InterruptedException {


        // Signal that the current run has finished
        BenchmarkSignal.send(worker, endpoint);

        // Perform one more operation to get
        // the server out of its blocking state
        switch (benchmarkMode) {
            case THROUGHPUT:
                switch (initialInstruction) {
                    case RUN_SEND -> sendThroughput();
                    case RUN_WRITE -> putThroughput();
                    case RUN_PINGPONG -> pingPongThroughput();
                }

            case LATENCY:
                switch (initialInstruction) {
                    case RUN_SEND -> blockingSendTagged();
                    case RUN_WRITE -> putLatency();
                    case RUN_PINGPONG -> pingPongLatency();
                }
        }

        // Barrier
        BenchmarkBarrier.signal(worker, endpoint);
        BenchmarkBarrier.await(worker);
    }

    /*--- BENCHMARK OPERATIONS ---*/

    public final void getLatency() {
        Requests.blockingGet(worker, endpoint, receiveBuffer, remoteAddress, remoteKey);
    }

    public final void getThroughput() {
        var outstanding = 0;
        for (int i = 0; i < operationCount; i++) {

            // Only increment outstanding requests if current request wasn't completed immediately
            if (requestPool.add(Requests.get(endpoint, receiveBuffer, remoteAddress, remoteKey))) {
                outstanding++;
            }

            // Wait until one request finishes
            if (outstanding > Constants.MAX_OUTSTANDING_REQUESTS) {
                requestPool.poll(worker);
                outstanding--;
            }
        }

        requestPool.pollRemaining(worker);
    }

    private long writeCounter = 1;

    public final void putLatency() {
        sendBuffer.set(ValueLayout.JAVA_LONG, 0L, writeCounter);
        Requests.blockingPut(worker, endpoint, sendBuffer, remoteAddress, remoteKey);
        while (receiveBuffer.get(ValueLayout.JAVA_LONG, 0L) != writeCounter) {
            worker.progress();
        }

        writeCounter++;
    }

    public final void putThroughput() {
        for (int i = 0; i < operationCount; i++) {
            sendBuffer.set(ValueLayout.JAVA_LONG, 0L, writeCounter++);
            requestPool.add(Requests.put(endpoint, sendBuffer, remoteAddress, remoteKey));
        }

        requestPool.pollRemaining(worker);
        writeCounter -= 1;

        while (receiveBuffer.get(ValueLayout.JAVA_LONG, 0L) != writeCounter) {
            worker.progress();
        }
    }

    public final void sendThroughput() {
        var outstanding = 0;
        for (int i = 0; i < operationCount; i++) {

            // Only increment outstanding requests if current request wasn't completed immediately
            if (requestPool.add(Requests.sendTagged(endpoint, sendBuffer))) {
                outstanding++;
            }

            // Wait until one request finishes
            if (outstanding > Constants.MAX_OUTSTANDING_REQUESTS) {
                requestPool.poll(worker);
                outstanding--;
            }
        }

        requestPool.add(Requests.receiveTagged(worker, receiveBuffer));
        requestPool.pollRemaining(worker);
    }

    public final void blockingSendTagged() {
        for (int i = 0; i < operationCount; i++) {
            requestPool.add(Requests.sendTagged(endpoint, sendBuffer));
        }

        requestPool.pollRemaining(worker);
    }

    public final void pingPongLatency() {
        requestPool.add(Requests.sendTagged(endpoint, sendBuffer));
        requestPool.add(Requests.receiveTagged(worker, receiveBuffer));
        requestPool.pollRemaining(worker);
    }

    public final void pingPongThroughput() {
        for (int i = 0; i < operationCount; i++) {
            requestPool.add(Requests.sendTagged(endpoint, sendBuffer));
            requestPool.add(Requests.receiveTagged(worker, receiveBuffer));
        }

        requestPool.pollRemaining(worker);
    }

    public final void add32() {
        Requests.blockingAtomic(ADD, worker, endpoint, nativeInteger, remoteAddress, remoteKey, atomicIntegerParameters);
        flushRequests();
        atomicCounter++;
    }

    public final void swap32() {
        Requests.blockingAtomic(SWAP, worker, endpoint, nativeInteger, remoteAddress, remoteKey, atomicIntegerParameters);
        flushRequests();
        atomicCounter++;
    }

    public final void compareAndSwap32() {
        Requests.blockingAtomic(COMPARE_AND_SWAP, worker, endpoint, nativeInteger, remoteAddress, remoteKey, atomicIntegerParameters);
        flushRequests();
        atomicCounter++;
    }

    public final void and32() {
        Requests.blockingAtomic(AND, worker, endpoint, nativeInteger, remoteAddress, remoteKey, atomicIntegerParameters);
        flushRequests();
        atomicCounter++;
    }

    public final void or32() {
        Requests.blockingAtomic(OR, worker, endpoint, nativeInteger, remoteAddress, remoteKey, atomicIntegerParameters);
        flushRequests();
        atomicCounter++;
    }

    public final void xor32() {
        Requests.blockingAtomic(XOR, worker, endpoint, nativeInteger, remoteAddress, remoteKey, atomicIntegerParameters);
        flushRequests();
        atomicCounter++;
    }

    public final void add64() {
        Requests.blockingAtomic(ADD, worker, endpoint, nativeLong, remoteAddress, remoteKey, atomicLongParameters);
        flushRequests();
        atomicCounter++;
    }

    public final void swap64() {
        Requests.blockingAtomic(SWAP, worker, endpoint, nativeLong, remoteAddress, remoteKey, atomicLongParameters);
        flushRequests();
        atomicCounter++;
    }

    public final void compareAndSwap64() {
        Requests.blockingAtomic(COMPARE_AND_SWAP, worker, endpoint, nativeLong, remoteAddress, remoteKey, atomicLongParameters);
        flushRequests();
        atomicCounter++;
    }

    public final void and64() {
        Requests.blockingAtomic(AND, worker, endpoint, nativeLong, remoteAddress, remoteKey, atomicLongParameters);
        flushRequests();
        atomicCounter++;
    }

    public final void or64() {
        Requests.blockingAtomic(OR, worker, endpoint, nativeLong, remoteAddress, remoteKey, atomicLongParameters);
        flushRequests();
        atomicCounter++;
    }

    public final void xor64() {
        Requests.blockingAtomic(XOR, worker, endpoint, nativeLong, remoteAddress, remoteKey, atomicLongParameters);
        flushRequests();
        atomicCounter++;
    }

    private final void flushRequests() {
        if (atomicCounter >= ATOMIC_FLUSH_THRESHOLD) {
            Requests.poll(worker, endpoint.flush());
            atomicCounter = 0;
        }
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
