package de.hhu.bsinfo.infinileap.example.command;

import de.hhu.bsinfo.infinileap.communication.AddressInfo;
import de.hhu.bsinfo.infinileap.communication.CommunicationManager;
import de.hhu.bsinfo.infinileap.communication.PortSpace;
import de.hhu.bsinfo.infinileap.verbs.Device;
import de.hhu.bsinfo.infinileap.verbs.QueuePair;
import de.hhu.bsinfo.infinileap.verbs.Verbs;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.IOException;
import java.net.InetSocketAddress;

@Slf4j
@CommandLine.Command(
        name = "communication",
        description = "Demonstrates the rdma communication manager.%n",
        showDefaultValues = true,
        separator = " ")
public class Communication implements Runnable {

    private static final int DEFAULT_SERVER_PORT = 2998;

    @CommandLine.Option(
            names = "--server",
            description = "Runs this instance in server mode.")
    private boolean isServer;

    @CommandLine.Option(
            names = {"-c", "--connect"},
            description = "The server to connect to.")
    private InetSocketAddress serverAddress;

    @CommandLine.Option(
            names = {"-p", "--port"},
            description = "The port the server will listen on.")
    private int port = DEFAULT_SERVER_PORT;

    @Override
    public void run() {
        try {
            if (isServer) {
                runServer();
            } else {
                runClient();
            }
        } catch (IOException e) {
            log.error("Unexpected error", e);
        }
    }

    private void runServer() throws IOException {

        // Create hints
        var hints = new AddressInfo();
        hints.setFlags(AddressInfo.Flag.PASSIVE);
        hints.setPortSpace(PortSpace.TCP);

        // Get local address information
        var localAddress = new InetSocketAddress("0.0.0.0", port);
        var addressInfo = CommunicationManager.getAddressInfo(localAddress, hints);

        // Create initial attributes used for new queue pairs
        var attributes = new QueuePair.InitialAttributes();
        var capabilities = attributes.getCapabilities();
        capabilities.setMaxSendWorkRequests(1);
        capabilities.setMaxReceiveWorkRequests(1);
        capabilities.setMaxInlineData(16);
        attributes.setSignalingLevel(1);

        // Create server endpoint
        var serverEndpoint = CommunicationManager.createEndpoint(addressInfo, null, attributes);

        // Set endpoint into listening mode
        serverEndpoint.listen();

        // Wait for client connection
        log.info("Waiting for client connection");
        var client = serverEndpoint.getRequest();

        serverEndpoint.accept(client);

        log.info("Accepted client connection");
    }

    private void runClient() throws IOException {

        // Create hints
        var hints = new AddressInfo();
        hints.setPortSpace(PortSpace.TCP);

        // Get server address information
        var addressInfo = CommunicationManager.getAddressInfo(serverAddress, hints);

        // Create initial attributes used for new queue pairs
        var attributes = new QueuePair.InitialAttributes();
        var capabilities = attributes.getCapabilities();
        capabilities.setMaxSendWorkRequests(1);
        capabilities.setMaxReceiveWorkRequests(1);
        capabilities.setMaxInlineData(16);
        attributes.setSignalingLevel(1);

        // Create server endpoint
        var clientEndpoint = CommunicationManager.createEndpoint(addressInfo, null, attributes);

        clientEndpoint.connect();

        log.info("Connected to server");
    }
}
