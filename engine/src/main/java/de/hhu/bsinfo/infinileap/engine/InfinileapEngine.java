package de.hhu.bsinfo.infinileap.engine;


import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.engine.agent.AcceptorAgent;
import de.hhu.bsinfo.infinileap.engine.agent.WorkerAgent;
import de.hhu.bsinfo.infinileap.engine.agent.base.CommandableAgent;
import de.hhu.bsinfo.infinileap.engine.agent.command.ConnectCommand;
import de.hhu.bsinfo.infinileap.engine.agent.command.ListenCommand;
import de.hhu.bsinfo.infinileap.engine.util.BufferPool;
import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import de.hhu.bsinfo.infinileap.engine.multiplex.EventLoopGroup;
import de.hhu.bsinfo.infinileap.engine.network.ConnectionManager;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.NoOpIdleStrategy;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Set;

@Slf4j
public class InfinileapEngine implements AutoCloseable {

    private static final int ACCEPTOR_THREADS = 1;

    private static final String ACCEPTOR_PREFIX = "acceptor";
    private static final String WORKER_PREFIX = "worker";

    private static final Set<ContextParameters.Feature> FEATURE_SET = Set.of(
            ContextParameters.Feature.RMA,       // Remote Memory Access
            ContextParameters.Feature.WAKEUP,    // Event Processing
            ContextParameters.Feature.AM,        // Active Messaging
            ContextParameters.Feature.ATOMIC_32, // 32-bit Atomic Operations
            ContextParameters.Feature.ATOMIC_64  // 64-bit Atomic Operations
    );

    private final Context context;

    private final WorkerParameters workerParameters;

    private final EventLoopGroup<CommandableAgent> workerGroup;

    private final EventLoopGroup<CommandableAgent> acceptorGroup;

    private final InetSocketAddress listenAddress;

    private final ConnectionManager connectionManager;

    private final Object serviceInstance;

    private final BufferPool bufferPool;

    @Builder
    private InfinileapEngine(int threadCount, Class<?> serviceClass, InetSocketAddress listenAddress) {

        // Redirect native logs through Slf4j
        NativeLogger.enable();

        log.info("Using UCX version {}", Context.getVersion());

        // Instantiate new event loop groups
        this.acceptorGroup = new EventLoopGroup<>(ACCEPTOR_PREFIX, ACCEPTOR_THREADS, () -> NoOpIdleStrategy.INSTANCE);
        this.workerGroup = new EventLoopGroup<>(WORKER_PREFIX, threadCount, () -> NoOpIdleStrategy.INSTANCE);
        this.listenAddress = listenAddress;

        // Allocate buffer pool for outgoing messages
        this.bufferPool = new BufferPool(4096, 128);

        // Create connection manager
        this.connectionManager = new ConnectionManager();

        try {
            // Create context parameters
            log.info("Using features {}", FEATURE_SET);
            var contextParameters = new ContextParameters()
                    .setFeatures(FEATURE_SET);

            // Read configuration (Environment Variables)
            var configuration = Configuration.read();

            // Initialize UCP context
            this.context = Context.initialize(contextParameters, configuration);

            // Parameters used for each worker
            workerParameters = new WorkerParameters()
                    .setThreadMode(ThreadMode.SINGLE);
        } catch (ControlException e) {
            throw new RuntimeException("Initializing engine failed", e);
        }

        try {
            // Instantiate service class
            serviceInstance = serviceClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Instantiating service failed", e);
        }
    }


    public void start() throws ControlException {

        // Start and wait until all event loops are active
        workerGroup.start();
        workerGroup.waitOnStart();

        // Add worker to each event loop
        for (var eventLoop : workerGroup) {
            var worker = context.createWorker(workerParameters);
            var workerAgent = new WorkerAgent(worker, bufferPool, serviceInstance);
            eventLoop.add(workerAgent);
        }

        acceptorGroup.start();
        acceptorGroup.waitOnStart();

        // Add acceptor to event loop
        var worker = context.createWorker(workerParameters);
        var acceptorAgent = new AcceptorAgent(worker, workerGroup);
        var eventLoop = acceptorGroup.first();
        eventLoop.add(acceptorAgent);

        // Listen for new connections on acceptor
        acceptorAgent.pushCommand(new ListenCommand(listenAddress));
    }

    public Channel connect(InetSocketAddress remoteAddress) {
        var agent = workerGroup.next().getAgent();

        // Instruct agent to connect with the specified remote address
        var command = new ConnectCommand(remoteAddress);
        agent.pushCommand(command);

        try {
            // Wait until command completes
            return command.await();
        } catch (InterruptedException e) {
            log.error("Connection establishment failed", e);
            throw new RuntimeException(e);
        }
    }

    public void join() throws InterruptedException {
        workerGroup.join();
    }

    @Override
    public void close() {
        this.context.close();
    }
}
