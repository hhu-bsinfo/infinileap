package de.hhu.bsinfo.infinileap.example.command;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.binding.ThreadMode;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
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

        // Create client socket for oob connection establishment
        log.info("Connecting to server at {}", serverAddress);
        var socket = new Socket(serverAddress.getAddress(), serverAddress.getPort());

        // Exchange worker address
        log.info("Exchanging worker address");
        remoteAddress = localAddress.exchange(socket);

        remoteAddress.hexDump();
        var endpointParameters = new EndpointParameters()
                .setRemoteAddress(serverAddress);

        var endpoint = worker.createEndpoint(endpointParameters);

        while (worker.progress() == WorkerProgress.IDLE) {
            LockSupport.parkNanos(Duration.ofSeconds(1).toNanos());
        }
    }

    private void runServer() throws IOException {

        var listenerParams = new ListenerParameters()
                .setListenAddress(listenAddress)
                .setConnectionHandler(((request, data) -> {
                    log.info("new request");
                }));

        var listener = worker.createListener(listenerParams);
        while (worker.progress() == WorkerProgress.IDLE) {
            LockSupport.parkNanos(Duration.ofSeconds(1).toNanos());
        }

        remoteAddress.hexDump();
    }
}
