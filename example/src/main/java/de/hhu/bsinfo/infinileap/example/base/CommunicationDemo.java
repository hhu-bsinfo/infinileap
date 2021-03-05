package de.hhu.bsinfo.infinileap.example.base;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.binding.ContextParameters.Feature;
import de.hhu.bsinfo.infinileap.example.util.Requests;
import de.hhu.bsinfo.infinileap.util.CloseException;
import de.hhu.bsinfo.infinileap.util.ResourcePool;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public abstract class CommunicationDemo implements Runnable {

    private static final long DEFAULT_REQUEST_SIZE = 1024;

    private static final Feature[] FEATURE_SET = {
            Feature.TAG, Feature.RMA, Feature.WAKEUP,
            Feature.ATOMIC_32, Feature.ATOMIC_64, Feature.STREAM
    };

    @CommandLine.Option(
            names = {"-c", "--connect"},
            description = "The server to connect to.")
    private InetSocketAddress serverAddress;

    @CommandLine.Option(
            names = {"-l", "--listen"},
            description = "The address the server listens on.")
    private InetSocketAddress listenAddress;

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

    private final AtomicBoolean barrier = new AtomicBoolean();

    @Override
    public void run() {

        NativeLogger.enable();

        log.info("Using UCX version {}", Context.getVersion());

        try (resources) {
            initialize();
        } catch (ControlException e) {
            log.error("Native operation failed", e);
        } catch (CloseException e) {
            log.error("Closing resource failed", e);
        }
    }

    private void initialize() throws ControlException {

        // Create context parameters
        var contextParameters = new ContextParameters()
                .setFeatures(FEATURE_SET)
                .setRequestSize(DEFAULT_REQUEST_SIZE);

        // Read configuration (Environment Variables)
        var configuration = pushResource(
                Configuration.read()
        );

        log.info("Initializing context");

        // Initialize UCP context
        context = pushResource(
                Context.initialize(contextParameters, configuration)
        );

        var workerParameters = new WorkerParameters()
                .setThreadMode(ThreadMode.SINGLE);

        log.info("Creating worker");

        // Create a worker
        worker = pushResource(
                context.createWorker(workerParameters)
        );

        if (serverAddress != null) {
            initializeClient();
        } else {
            initializeServer();
        }
    }

    private void initializeClient() throws ControlException {
        var endpointParameters = new EndpointParameters()
                .setRemoteAddress(serverAddress);

        log.info("Connecting to {}", serverAddress);
        endpoint = pushResource(
                worker.createEndpoint(endpointParameters)
        );

        onClientReady(context, worker, endpoint);
    }

    private void initializeServer() throws ControlException {
        var connectionRequest = new AtomicReference<ConnectionRequest>();
        var listenerParams = new ListenerParameters()
                .setListenAddress(listenAddress)
                .setConnectionHandler(connectionRequest::set);

        log.info("Listening for new connection requests on {}", listenAddress);
        listener = pushResource(worker.createListener(listenerParams));

        Requests.await(worker, connectionRequest);

        var endpointParameters = new EndpointParameters()
                .setConnectionRequest(connectionRequest.get());

        endpoint = pushResource(worker.createEndpoint(endpointParameters));
        onServerReady(context, worker, endpoint);
    }

    protected <T extends AutoCloseable> T pushResource(T resource) {
        resources.push(resource);
        return resource;
    }

    protected abstract void onClientReady(Context context, Worker worker, Endpoint endpoint) throws ControlException;

    protected abstract void onServerReady(Context context, Worker worker, Endpoint endpoint) throws ControlException;
}
