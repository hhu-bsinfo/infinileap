package de.hhu.bsinfo.infinileap.example.benchmark.command;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.binding.ContextParameters.Feature;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkDetails;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkInstruction;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkInstruction.OpCode;
import de.hhu.bsinfo.infinileap.example.util.Constants;
import de.hhu.bsinfo.infinileap.example.util.RequestHelpher;
import de.hhu.bsinfo.infinileap.util.CloseException;
import de.hhu.bsinfo.infinileap.util.ResourcePool;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@CommandLine.Command(
        name = "server",
        description = "Runs the server part of the benchmark."
)
public class BenchmarkServer implements Runnable {

    private static final long DEFAULT_REQUEST_SIZE = 1024;

    private static final Feature[] FEATURE_SET = {
            Feature.TAG, Feature.RMA, Feature.WAKEUP,
            Feature.ATOMIC_32, Feature.ATOMIC_64, Feature.STREAM
    };

    @CommandLine.Option(
            names = {"-l", "--listen"},
            description = "The address the server listens on.")
    private InetSocketAddress listenAddress;

    @CommandLine.Option(
            names = {"-p", "--port"},
            description = "The port the server will listen on.")
    private int port = Constants.DEFAULT_PORT;

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
     * The listener used for accepting new connections on the server side.
     */
    private Listener listener;

    /**
     * Handles cleanup of resources created during the demo.
     */
    private final ResourcePool resources = new ResourcePool();

    private static final AtomicBoolean SHOULD_RERUN = new AtomicBoolean(true);
    private static final AtomicInteger LOOP_COUNTER = new AtomicInteger(1);

    @Override
    public void run() {
        while (SHOULD_RERUN.get()) {
            log.info("Running Loop {}", LOOP_COUNTER.get());
            try (resources) {
                initialize();
                serve();

                switch (receiveOpCode()) {
                    case SYNCHRONIZE -> LOOP_COUNTER.incrementAndGet();
                    case FINISH -> SHOULD_RERUN.set(false);
                }
            } catch (ControlException e) {
                log.error("Native operation failed", e);
            } catch (CloseException e) {
                log.error("Closing resource failed", e);
            }
        }
    }

    private void initialize() throws ControlException, CloseException {
        try (var pool = new ResourcePool()) {

            // Create context parameters
            var contextParameters = pool.push(() -> new ContextParameters()
                    .setFeatures(FEATURE_SET)
                    .setRequestSize(DEFAULT_REQUEST_SIZE));

            log.trace("Initializing context");

            // Initialize UCP context
            context = Context.initialize(contextParameters);
            resources.push(context);

            var workerParameters = pool.push(() -> new WorkerParameters()
                    .setThreadMode(ThreadMode.SINGLE));

            log.trace("Creating worker");

            // Create a worker
            worker = context.createWorker(workerParameters);
            resources.push(worker);

            var connectionRequest = accept();

            log.trace("Accepted new client connection");
            var endpointParameters = new EndpointParameters()
                    .setConnectionRequest(connectionRequest);

            endpoint = worker.createEndpoint(endpointParameters);
            resources.push(endpoint);
        }
    }

    private ConnectionRequest accept() throws ControlException, CloseException {
        try (var pool = new ResourcePool()) {
            var connectionRequest = new AtomicReference<ConnectionRequest>();
            var listenerParams = pool.push(() -> new ListenerParameters()
                    .setListenAddress(listenAddress)
                    .setConnectionHandler(connectionRequest::set));

            log.trace("Listening for new connection requests on {}", listenAddress);
            listener = worker.createListener(listenerParams);
            resources.push(listener);

            RequestHelpher.await(worker, connectionRequest);
            return connectionRequest.get();
        }
    }

    private void serve() throws ControlException, CloseException {
        var opCode = receiveOpCode();
        try (var details = receiveDetails()) {
            switch (opCode) {
                case RDMA_LATENCY -> executeMemoryLatency(details);
                case RDMA_THROUGHPUT -> executeMemoryThroughput(details);
                case MESSAGING_LATENCY -> executeMessagingLatency(details);
                case MESSAGING_THROUGHPUT -> executeMessagingThroughput(details);
            }
        }
    }

    private void executeMemoryLatency(BenchmarkDetails details) {

    }

    private void executeMemoryThroughput(BenchmarkDetails details) throws ControlException, CloseException {
        var region = context.allocateMemory(details.getBufferSize());
        resources.push(region);

        log.trace("Allocated {} bytes of memory", region.segment().byteSize());
        sendDescriptor(region.descriptor());

        log.trace("Waiting on synchronization");


        throw new IllegalStateException("Shouldn't reach here");
    }

    private void executeMessagingLatency(BenchmarkDetails details) {

    }

    private void executeMessagingThroughput(BenchmarkDetails details) {
    }

    private OpCode receiveOpCode() throws ControlException, CloseException {
        try (var instruction = new BenchmarkInstruction()) {
            RequestHelpher.await(
                worker, worker.receiveTagged(instruction, Constants.TAG_BENCHMARK_OPCODE)
            );

            return instruction.opCode();
        }
    }

    private BenchmarkDetails receiveDetails() throws ControlException, CloseException {
        var details = new BenchmarkDetails();
        RequestHelpher.await(
            worker, worker.receiveTagged(details, Constants.TAG_BENCHMARK_DETAILS)
        );

        return details;
    }

    private void sendDescriptor(MemoryDescriptor descriptor) {
        RequestHelpher.await(
            worker, endpoint.sendTagged(descriptor, Constants.TAG_BENCHMARK_DESCRIPTOR)
        );
    }
}
