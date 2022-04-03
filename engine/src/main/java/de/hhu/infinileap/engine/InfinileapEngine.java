package de.hhu.infinileap.engine;


import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.infinileap.engine.agent.WorkerAgent;
import de.hhu.infinileap.engine.message.Handler;
import de.hhu.infinileap.engine.message.HandlerAdapter;
import de.hhu.infinileap.engine.message.HandlerList;
import de.hhu.infinileap.engine.message.MessageHandler;
import de.hhu.infinileap.engine.multiplex.EventLoopGroup;
import de.hhu.infinileap.engine.util.EndpointResolver;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.NoOpIdleStrategy;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static de.hhu.bsinfo.infinileap.binding.HandlerParameters.Flag.WHOLE_MESSAGE;

@Slf4j
public class InfinileapEngine implements ConnectionHandler {

    private static final String EVENT_LOOP_PREFIX = "infinileap";

    private static final Set<ContextParameters.Feature> FEATURE_SET = Set.of(
            ContextParameters.Feature.RMA,       // Remote Memory Access
            ContextParameters.Feature.WAKEUP,    // Event Processing
            ContextParameters.Feature.AM,        // Active Messaging
            ContextParameters.Feature.ATOMIC_32, // 32-bit Atomic Operations
            ContextParameters.Feature.ATOMIC_64  // 64-bit Atomic Operations
    );

    private final WorkerParameters workerParameters;

    private final EventLoopGroup<WorkerAgent> loopGroup;

    private final InetSocketAddress listenAddress;

    private ListenerParameters listenerParameters;

    private Listener listener;

    private final Context context;

    private final Object serviceInstance;

    private final HandlerList handlerList;

    private final ConcurrentMap<MemoryAddress, Endpoint> endpointMap;

    @Builder
    private InfinileapEngine(int threadCount, Class<?> serviceClass, InetSocketAddress listenAddress) {

        // Redirect native logs through Slf4j
        NativeLogger.enable();

        log.info("Using UCX version {}", Context.getVersion());

        // Instantiate new event loop group
        this.loopGroup = new EventLoopGroup<>(EVENT_LOOP_PREFIX, threadCount, () -> NoOpIdleStrategy.INSTANCE);
        this.listenAddress = listenAddress;

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
            this.serviceInstance = serviceClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Instantiating service failed", e);
        }

        // Collect user-defined RPC handlers
        endpointMap = new ConcurrentHashMap<>();
        handlerList = HandlerList.forServiceInstance(this.serviceInstance, this.endpointMap::get);
    }


    public void start() throws ControlException {

        // Start and wait until all event loops are active
        loopGroup.start();
        loopGroup.waitOnStart();

        // Add worker to each event loop
        for (var eventLoop : loopGroup) {
            var worker = context.createWorker(workerParameters);
            var agent = new WorkerAgent(worker);

            handlerList.registerWith(worker);
            eventLoop.add(agent);
        }

        // Parameters used for listener on first worker
        listenerParameters = new ListenerParameters()
                .setConnectionHandler(this)
                .setListenAddress(listenAddress);

        // Register listener on first worker
        listener = loopGroup.first()
                .getAgent()
                .getWorker()
                .createListener(listenerParameters);
    }

    public Endpoint connect(InetSocketAddress remoteAddress) throws ControlException {
        var endpointParameters = new EndpointParameters()
                .setRemoteAddress(remoteAddress)
                .enableClientIdentifier();

        var endpoint = loopGroup.next()
                .getAgent()
                .getWorker()
                .createEndpoint(endpointParameters);

        endpointMap.put(endpoint.address(), endpoint);

        return endpoint;
    }

    public void join() throws InterruptedException {
        loopGroup.join();
    }

    @Override
    public void onConnection(ConnectionRequest request) {
        var endpointParameters = new EndpointParameters()
                .setConnectionRequest(request);

        try {
            // Accept connection
            var endpoint = loopGroup.next()
                    .getAgent()
                    .getWorker()
                    .createEndpoint(endpointParameters);

            endpointMap.put(endpoint.address(), endpoint);

            log.info("Accepted new connection from {}", request.getClientAddress());
            log.info("Remote client id is {}", Long.toHexString(request.getClientIdentifier()));
        } catch (ControlException e) {
            log.error("Accepting connection failed", e);
        } catch (ArithmeticException e) {
            log.error("Client id must be between {} and {}", Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
    }
}
