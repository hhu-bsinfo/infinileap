package de.hhu.bsinfo.infinileap.engine;


import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.engine.agent.ConnectionAgent;
import de.hhu.bsinfo.infinileap.engine.agent.command.ConnectCommand;
import de.hhu.bsinfo.infinileap.engine.agent.command.ListenCommand;
import de.hhu.bsinfo.infinileap.engine.util.BufferPool;
import de.hhu.bsinfo.infinileap.common.memory.MemoryAlignment;
import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import de.hhu.bsinfo.infinileap.engine.message.MessageDispatcher;
import de.hhu.bsinfo.infinileap.engine.multiplex.EventLoopGroup;
import de.hhu.bsinfo.infinileap.engine.network.ConnectionManager;
import de.hhu.bsinfo.infinileap.common.multiplex.EventType;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.NoOpIdleStrategy;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Set;

@Slf4j
public class InfinileapEngine implements AutoCloseable {

    private static final String EVENT_LOOP_PREFIX = "infinileap";

    private static final Set<ContextParameters.Feature> FEATURE_SET = Set.of(
            ContextParameters.Feature.RMA,       // Remote Memory Access
            ContextParameters.Feature.WAKEUP,    // Event Processing
            ContextParameters.Feature.AM,        // Active Messaging
            ContextParameters.Feature.ATOMIC_32, // 32-bit Atomic Operations
            ContextParameters.Feature.ATOMIC_64  // 64-bit Atomic Operations
    );

    private final Context context;

    private final WorkerParameters workerParameters;

    private final EventLoopGroup<ConnectionAgent> loopGroup;

    private final InetSocketAddress listenAddress;

    private final ConnectionManager connectionManager;

    private final MessageDispatcher messageDispatcher;

    private final BufferPool bufferPool;

    @Builder
    private InfinileapEngine(int threadCount, Class<?> serviceClass, InetSocketAddress listenAddress) {

        // Redirect native logs through Slf4j
        NativeLogger.enable();

        log.info("Using UCX version {}", Context.getVersion());

        // Instantiate new event loop group
        this.loopGroup = new EventLoopGroup<>(EVENT_LOOP_PREFIX, threadCount, () -> NoOpIdleStrategy.INSTANCE);
        this.listenAddress = listenAddress;

        // Allocate buffer pool for outgoing messages
        this.bufferPool = new BufferPool(1024, MemoryAlignment.PAGE.value());

        // Create connection manager
        this.connectionManager = new ConnectionManager(() -> loopGroup.next().getAgent(), bufferPool);

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
            var serviceInstance = serviceClass.getDeclaredConstructor().newInstance();

            // Collect user-defined RPC handlers
            messageDispatcher = MessageDispatcher.forServiceInstance(serviceInstance, connectionManager::resolve);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Instantiating service failed", e);
        }
    }


    public void start() throws ControlException, IOException {

        // Start and wait until all event loops are active
        loopGroup.start();
        loopGroup.waitOnStart();

        // Add worker to each event loop
        for (var eventLoop : loopGroup) {
            var worker = context.createWorker(workerParameters);
            var epollAgent = new ConnectionAgent(worker);

            messageDispatcher.registerOn(worker);
            eventLoop.add(epollAgent);
        }

        // Listen for new connections on first worker
        var firstAgent = loopGroup.first().getAgent();
        firstAgent.pushCommand(new ListenCommand(listenAddress));
    }

    public Channel connect(InetSocketAddress remoteAddress) throws ControlException {
        var agent = loopGroup.next().getAgent();

        agent.pushCommand(new ConnectCommand(remoteAddress));
        return connectionManager.connect(
                loopGroup.next().getAgent().getWorker(),
                remoteAddress
        );
    }

    public void join() throws InterruptedException {
        loopGroup.join();
    }

    @Override
    public void close() {
        this.context.close();
    }
}
