package de.hhu.bsinfo.infinileap.benchmark.connection;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.util.CloseException;
import de.hhu.bsinfo.infinileap.util.RequestPool;
import de.hhu.bsinfo.infinileap.util.Requests;
import de.hhu.bsinfo.infinileap.util.ResourcePool;
import de.hhu.bsinfo.infinileap.benchmark.message.BenchmarkDetails;
import de.hhu.bsinfo.infinileap.benchmark.util.*;
import de.hhu.bsinfo.infinileap.common.memory.MemoryAlignment;
import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class BenchmarkServer {


    /**
     * The network address this server listens on.
     */
    private final InetSocketAddress listenAddress;

    /**
     * This node's context.
     */
    private Context context;

    /**
     * This node's worker instance.
     */
    private Worker worker;

    /**
     * The endpoint used for communication with the other side.
     */
    private Endpoint endpoint;

    /**
     * The listener used for accepting new connections.
     */
    private Listener listener;

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
     * The remote address used for memory access operations.
     */
    private MemoryAddress remoteAddress;

    /**
     * Key used for accessing remote memory.
     */
    private RemoteKey remoteKey;

    /**
     * A pool of requests for performing multiple operations at once.
     */
    private RequestPool requestPool;

    /**
     * Used to signal the end of a benchmark run.
     */
    private BenchmarkSignal signal;

    /**
     * Used for ping-pong based write operations.
     */
    private long writeCounter = 1;

    /**
     * Handles cleanup of resources created during the demo.
     */
    private final ResourcePool resources = new ResourcePool();

    private final AtomicBoolean controlledShutdown = new AtomicBoolean(false);

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private final AtomicReference<Thread> workerThread = new AtomicReference<>(null);

    private BenchmarkServer(InetSocketAddress listenAddress) {
        this.listenAddress = listenAddress;
    }

    public void start() {

        // Remember current thread for interruption
        workerThread.set(Thread.currentThread());
        isRunning.set(true);

        while (true) {
            try (resources) {

                // Initialize and execute benchmark
                initialize();
                executeBenchmark();

                // Barrier
                BenchmarkBarrier.await(worker);
                BenchmarkBarrier.signal(worker, endpoint);
            } catch (ControlException e) {
                log.error("Native operation failed", e);
                return;
            } catch (CloseException e) {
                log.error("Closing resource failed", e);
                return;
            } catch (InterruptedException e) {
                if (controlledShutdown.get()) {
                    return;
                }

                log.error("Unexpected interrupt", e);
                return;
            } finally {
                isRunning.set(false);
            }
        }
    }

    private void initialize() throws ControlException, CloseException, InterruptedException {
        var connectionResources = ConnectionResources.create();
        context = resources.push(connectionResources.context());
        worker = resources.push(connectionResources.worker());
        var connectionRequest = accept();

        log.trace("Accepted new client connection");
        var endpointParameters = new EndpointParameters()
                .setConnectionRequest(connectionRequest);

        endpoint = resources.push(
                worker.createEndpoint(endpointParameters)
        );
    }

    private ConnectionRequest accept() throws ControlException, CloseException, InterruptedException {
        var connectionRequest = new AtomicReference<ConnectionRequest>();
        var listenerParams = new ListenerParameters()
                .setListenAddress(listenAddress)
                .setConnectionHandler(new ConnectionHandler() {
                    @Override
                    protected void onConnection(ConnectionRequest request) {
                        connectionRequest.set(request);
                    }
                });

        log.trace("Listening for new connection requests on {}", listenAddress);
        listener = worker.createListener(listenerParams);
        resources.push(listener);

        Requests.await(worker, connectionRequest);
        return connectionRequest.get();
    }

    private void executeBenchmark() throws ControlException, CloseException, InterruptedException {
        var opCode = BenchmarkInstructions.receiveOpCode(worker);
        var details = BenchmarkInstructions.receiveDetails(worker);

        // Initialize send and receive buffers
        initBuffers(details);

        // Initialize request pool
        requestPool = new RequestPool(details.getOperationCount() * 2);

        // Initialize benchmark signal
        signal = BenchmarkSignal.listen(worker);
        resources.push(signal);

        // Execute operation
        BenchmarkBarrier.await(worker);
        switch (details.getBenchmarkMode()) {

            case LATENCY:
                switch (opCode) {
                    case RUN_READ -> readLatency(details);
                    case RUN_WRITE -> writeLatency(details);
                    case RUN_SEND -> sendLatency(details);
                    case RUN_PINGPONG -> pingPongLatency(details);
                    case RUN_ATOMIC -> atomicLatency(details);
                }
                break;

            case THROUGHPUT:
                switch (opCode) {
                    case RUN_READ -> readThroughput(details);
                    case RUN_WRITE -> writeThroughput(details);
                    case RUN_SEND -> sendThroughput(details);
                    case RUN_PINGPONG -> pingPongThroughput(details);
                    case RUN_ATOMIC -> atomicThroughput(details);
                }
                break;
        }
    }

    private void readLatency(BenchmarkDetails details) throws ControlException {
        // Nothing to do
    }

    private void readThroughput(BenchmarkDetails details) throws ControlException {
        // Nothing to do
    }

    private void writeLatency(BenchmarkDetails details) throws ControlException {
        while (signal.isCleared()) {
            while (receiveBuffer.get(ValueLayout.JAVA_LONG, 0L) != writeCounter) {
                worker.progress();
            }

            sendBuffer.set(ValueLayout.JAVA_LONG, 0L, writeCounter++);
            Requests.blockingPut(worker, endpoint, sendBuffer, remoteAddress, remoteKey);
        }

        writeCounter = 1;
    }

    private void writeThroughput(BenchmarkDetails details) throws ControlException {
        while (signal.isCleared()) {

            // Wait until last write was performed
            writeCounter += details.getOperationCount() - 1;
            while (receiveBuffer.get(ValueLayout.JAVA_LONG, 0L) != writeCounter) {
                worker.progress();
            }

            // Send back the current counter value
            sendBuffer.set(ValueLayout.JAVA_LONG, 0L, writeCounter);
            Requests.blockingPut(worker, endpoint, sendBuffer, remoteAddress, remoteKey);
        }

        writeCounter = 1;
    }

    private void sendThroughput(BenchmarkDetails details) throws ControlException {
        while (signal.isCleared()) {
            var outstanding = 0;
            for (int i = 0; i < details.getOperationCount(); i++) {

                // Only increment outstanding requests if current request wasn't completed immediately
                if (requestPool.add(BenchmarkInstructions.receiveTagged(worker, receiveBuffer))) {
                    outstanding++;
                }

                // Wait until one request finishes
                if (outstanding > Constants.MAX_OUTSTANDING_REQUESTS) {
                    requestPool.poll(worker);
                    outstanding--;
                }
            }

            requestPool.pollRemaining(worker);
            BenchmarkInstructions.blockingSendTagged(worker, endpoint, sendBuffer);
        }

        // TODO(krakowski)
        // Find out why there are still ${details.getOperationCount}
        // messages pending inside the queue.
        for (int i = 0; i < details.getOperationCount(); i++) {
            requestPool.add(BenchmarkInstructions.receiveTagged(worker, receiveBuffer));
        }

        requestPool.pollRemaining(worker);
    }

    private void sendLatency(BenchmarkDetails details) throws ControlException {
        while (signal.isCleared()) {
            BenchmarkInstructions.blockingReceiveTagged(worker, endpoint, receiveBuffer);
            BenchmarkInstructions.blockingSendTagged(worker, endpoint, sendBuffer);
        }
    }

    private void pingPongLatency(BenchmarkDetails details) throws ControlException {
        while (signal.isCleared()) {
            for (int i = 0; i < details.getOperationCount(); i++) {
                BenchmarkInstructions.blockingReceiveTagged(worker, endpoint, receiveBuffer);
                BenchmarkInstructions.blockingSendTagged(worker, endpoint, sendBuffer);
            }
        }
    }

    private void pingPongThroughput(BenchmarkDetails details) throws ControlException {
        while (signal.isCleared()) {
            for (int i = 0; i < details.getOperationCount(); i++) {
                BenchmarkInstructions.blockingReceiveTagged(worker, endpoint, receiveBuffer);
                BenchmarkInstructions.blockingSendTagged(worker, endpoint, sendBuffer);
            }
        }
    }

    private void atomicThroughput(BenchmarkDetails details) throws ControlException {

    }

    private void atomicLatency(BenchmarkDetails details) throws ControlException {

    }

    private void initBuffers(BenchmarkDetails details) throws ControlException, InterruptedException {
        sendRegion = context.allocateMemory(details.getBufferSize(), MemoryAlignment.PAGE);
        receiveRegion = context.allocateMemory(details.getBufferSize(), MemoryAlignment.PAGE);
        resources.push(sendRegion);
        resources.push(receiveRegion);

        sendBuffer = sendRegion.segment();
        receiveBuffer = receiveRegion.segment();

        // Exchange memory region information
        BenchmarkInstructions.sendDescriptor(worker, endpoint, receiveRegion.descriptor());
        var descriptor = BenchmarkInstructions.receiveDescriptor(worker);

        remoteKey = endpoint.unpack(descriptor);
        remoteAddress = descriptor.remoteAddress();
        resources.push(remoteKey);
    }

    public static BenchmarkServer create(InetSocketAddress listenAddress) throws ControlException {
        return new BenchmarkServer(listenAddress);
    }

    public void shutdown() {
        controlledShutdown.set(true);
        workerThread.get().interrupt();
        worker.signal();
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public Thread getWorkerThread() {
        return workerThread.get();
    }
}
