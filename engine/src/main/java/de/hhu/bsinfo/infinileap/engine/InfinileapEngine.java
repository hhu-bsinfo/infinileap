package de.hhu.bsinfo.infinileap.engine;


import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.engine.buffer.StaticBufferPool;
import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import de.hhu.bsinfo.infinileap.engine.event.command.ConnectCommand;
import de.hhu.bsinfo.infinileap.engine.event.command.ListenCommand;
import de.hhu.bsinfo.infinileap.engine.event.loop.AcceptorEventLoop;
import de.hhu.bsinfo.infinileap.engine.event.loop.CommandableEventLoop;
import de.hhu.bsinfo.infinileap.engine.event.loop.EventLoopGroup;
import de.hhu.bsinfo.infinileap.engine.event.loop.spin.SpinningCommandableEventLoop;
import de.hhu.bsinfo.infinileap.engine.event.loop.spin.SpinningWorkerEventLoop;
import de.hhu.bsinfo.infinileap.engine.util.NamedThreadFactory;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

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

    private final EventLoopGroup<SpinningCommandableEventLoop> workerGroup;

    private final EventLoopGroup<CommandableEventLoop> acceptorGroup;

    private final InetSocketAddress listenAddress;

    private final Object serviceInstance;

    private final StaticBufferPool sharedPool;

    private final int threadCount;

    private final int maxParallelRequests;

    private final int maxMessageSize;

    @Builder
    private InfinileapEngine(int threadCount, int maxParallelRequests, int maxMessageSize, Class<?> serviceClass, InetSocketAddress listenAddress) {

        // Redirect native logs through Slf4j
        NativeLogger.enable();

        log.info("Using UCX version {}", Context.getVersion());

        // Instantiate new event loop groups
        this.acceptorGroup = new EventLoopGroup<>();
        this.workerGroup = new EventLoopGroup<>();
        this.listenAddress = listenAddress;
        this.threadCount = threadCount;
        this.maxParallelRequests = maxParallelRequests;
        this.maxMessageSize = maxMessageSize;

        // Allocate buffer pool for outgoing messages
        this.sharedPool = new StaticBufferPool(maxParallelRequests * threadCount * 32, maxMessageSize);

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


    public void start() {

        // Populate the worker group
        workerGroup.populate(threadCount, () -> {
            try {
                return new SpinningWorkerEventLoop(
                        context.createWorker(workerParameters),
                        sharedPool,
                        serviceInstance,
                        maxMessageSize,
                        maxParallelRequests
                );
            } catch (ControlException e) {
                throw new RuntimeException(e);
            }
        });

        // Start and wait until all worker event loops are active
        workerGroup.start(new NamedThreadFactory(WORKER_PREFIX));
        workerGroup.waitOnStart();

        // Populate acceptor group
        acceptorGroup.populate(ACCEPTOR_THREADS, () -> {
            try {
                return new AcceptorEventLoop(
                        context.createWorker(workerParameters),
                        workerGroup
                );
            } catch (ControlException e) {
                throw new RuntimeException(e);
            }
        });

        // Start and wait until all acceptor event loops are active
        acceptorGroup.start(new NamedThreadFactory(ACCEPTOR_PREFIX));
        acceptorGroup.waitOnStart();

        // Start listening on acceptor event loop
        acceptorGroup.first().pushCommand(new ListenCommand(listenAddress));
    }

    public Channel connect(InetSocketAddress remoteAddress) {
        var loop = workerGroup.next();

        // Instruct agent to connect with the specified remote address
        var command = new ConnectCommand(remoteAddress);
        loop.pushCommand(command);

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
