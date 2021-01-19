package de.hhu.bsinfo.infinileap.example.base;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.binding.ContextParameters.Feature;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public abstract class ClientServerDemo implements Runnable {

    private static final int DEFAULT_SERVER_PORT = 2998;

    private static final long DEFAULT_REQUEST_SIZE = 1024;

    private static final Feature[] FEATURE_SET = { Feature.TAG, Feature.RMA };

    @CommandLine.Option(
            names = {"-c", "--connect"},
            description = "The server to connect to.")
    private InetSocketAddress serverAddress;

    @CommandLine.Option(
            names = {"-l", "--listen"},
            description = "The address the server listens on.")
    private InetSocketAddress listenAddress;

    @CommandLine.Option(
            names = {"-p", "--port"},
            description = "The port the server will listen on.")
    private int port = DEFAULT_SERVER_PORT;

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

    @Override
    public void run() {

        // Create context parameters
        var contextParameters = new ContextParameters()
                .setFeatures(FEATURE_SET)
                .setRequestSize(DEFAULT_REQUEST_SIZE);

        // Read configuration (Environment Variables)
        var configuration = Configuration.read();

        // Initialize UCP context
        context = Context.initialize(contextParameters, configuration);

        var workerParameters = new WorkerParameters()
                .setThreadMode(ThreadMode.SINGLE);

        worker = context.createWorker(workerParameters);

        if (serverAddress != null) {
            initializeClient();
        } else {
            initializeServer();
        }
    }

    private void initializeClient() {
        var endpointParameters = new EndpointParameters()
                .setRemoteAddress(serverAddress);

        log.info("Connecting to {}", serverAddress);
        endpoint = worker.createEndpoint(endpointParameters);
        onClientReady();
    }

    private void initializeServer() {
        var connectionRequest = new AtomicReference<ConnectionRequest>();
        var listenerParams = new ListenerParameters()
                .setListenAddress(listenAddress)
                .setConnectionHandler(connectionRequest::set);

        log.info("Listening for new connection requests on {}", listenAddress);
        listener = worker.createListener(listenerParams);
        while (connectionRequest.get() == null) {
            worker.progress();
        }

        var endpointParameters = new EndpointParameters()
                .setConnectionRequest(connectionRequest.get());

        endpoint = worker.createEndpoint(endpointParameters);
        onServerReady();
    }

    protected final Context context() {
        return context;
    }

    protected final Worker worker() {
        return worker;
    }

    protected final Endpoint endpoint() {
        return endpoint;
    }

    protected abstract void onClientReady();

    protected abstract void onServerReady();
}
