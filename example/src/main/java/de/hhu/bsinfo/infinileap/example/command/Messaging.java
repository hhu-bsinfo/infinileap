package de.hhu.bsinfo.infinileap.example.command;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.binding.ThreadMode;
import de.hhu.bsinfo.infinileap.util.MemoryUtil;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

@Slf4j
@CommandLine.Command(
        name = "messaging",
        description = ""
)
public class Messaging implements Runnable {

    private static final long DEFAULT_REQUEST_SIZE = 1024;

    private static final int DEFAULT_SERVER_PORT = 2998;

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

    private Context context;

    private Worker worker;

    private WorkerAddress localAddress;
    private WorkerAddress remoteAddress;

    @Override
    public void run() {
        // Create context parameters
        var contextParameters = new ContextParameters()
                .setFeatures(ContextParameters.Feature.TAG)
                .setRequestSize(DEFAULT_REQUEST_SIZE);

        // Read configuration (Environment Variables)
        var configuration = Configuration.read();

        // Initialize UCP context
        context = Context.initialize(contextParameters, configuration);

        var workerParameters = new WorkerParameters()
                .setThreadMode(ThreadMode.SINGLE);

        worker = context.createWorker(workerParameters);
        localAddress = worker.getAddress();

        try {
            if (serverAddress != null) {
                runClient();
            } else {
                runServer();
            }

            Thread.sleep(Duration.ofSeconds(5).toMillis());
        } catch (IOException | InterruptedException exception) {
            log.error("caught unexpected exception ", exception);
        }
    }

    private void runClient() throws IOException {
        var endpointParameters = new EndpointParameters()
                .setRemoteAddress(serverAddress);

        log.info("Connecting to {}", serverAddress);
        var endpoint = worker.createEndpoint(endpointParameters);

        var buffer = MemorySegment.allocateNative(Long.BYTES);
        MemoryAccess.setLong(buffer, 42L);

        log.info("Sending buffer");

        var requestParameters = new RequestParameters()
                .setUserData(0L)
                .setSendCallback((request, status, data) -> {
                    log.info("Message sent");
                });

        endpoint.sendTagged(buffer, Tag.of(0L), requestParameters);

        while (true) {
            worker.progress();
            LockSupport.parkNanos(Duration.ofSeconds(1).toNanos());
        }
    }

    private void runServer() throws IOException {

        var connectionRequest = new AtomicReference<ConnectionRequest>();

        var listenerParams = new ListenerParameters()
                .setListenAddress(listenAddress)
                .setConnectionHandler(connectionRequest::set);

        log.info("Listening for new connection requests on {}", listenAddress);
        var listener = worker.createListener(listenerParams);
        while (connectionRequest.get() == null) {
            worker.progress();
        }
        var endpointParameters = new EndpointParameters()
                .setConnectionRequest(connectionRequest.get());

        var endpoint = worker.createEndpoint(endpointParameters);

        log.info("Received new connection request");
        var buffer = MemorySegment.allocateNative(Long.BYTES);

        log.info("Receiving message");
        var messageReceived = new AtomicBoolean();
        var requestParameters = new RequestParameters()
                .setReceiveCallback((request, status, tagInfo, data) -> {
                    messageReceived.set(true);
                });

        worker.receiveTagged(buffer, Tag.of(0L), requestParameters);

        while (!messageReceived.get()) {
            worker.progress();
        }

        log.info("Received {}", MemoryAccess.getLong(buffer));
    }
}
