package de.hhu.bsinfo.infinileap.engine.network;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.engine.agent.WorkerAgent;
import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import de.hhu.bsinfo.infinileap.engine.util.AgentProvider;
import jdk.incubator.foreign.MemoryAddress;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class ConnectionManager extends ConnectionHandler {

    private final ConcurrentMap<MemoryAddress, Channel> channelMap;

    private final AgentProvider<WorkerAgent> agentProvider;

    private ListenerParameters listenerParameters;

    private Listener listener;

    public ConnectionManager(AgentProvider<WorkerAgent> provider) {
        this.channelMap = new ConcurrentHashMap<>();
        this.agentProvider = provider;
    }

    @Override
    public void onConnection(ConnectionRequest request) {
        var endpointParameters = new EndpointParameters()
                .setConnectionRequest(request);

        try {
            // Accept connection
            var endpoint = agentProvider
                    .provide()
                    .getWorker()
                    .createEndpoint(endpointParameters);

            var channel = new Channel(endpoint);
            channelMap.put(endpoint.address(), channel);

            log.info("Accepted new connection from {}", request.getClientAddress());
        } catch (ControlException e) {
            log.error("Accepting connection failed", e);
        } catch (ArithmeticException e) {
            log.error("Client id must be between {} and {}", Integer.MIN_VALUE, Integer.MAX_VALUE);
        }

    }

    public void listen(Worker worker, InetSocketAddress listenAddress) throws ControlException {
        this.listenerParameters = new ListenerParameters()
                .setConnectionHandler(this)
                .setListenAddress(listenAddress);

        this.listener = worker.createListener(listenerParameters);
    }

    public Channel connect(Worker worker, InetSocketAddress remoteAddress) throws ControlException {
        var endpointParameters = new EndpointParameters()
                .setRemoteAddress(remoteAddress)
                .enableClientIdentifier();

        var endpoint = worker.createEndpoint(endpointParameters);
        var channel = new Channel(endpoint);
        channelMap.put(endpoint.address(), channel);

        return channel;
    }

    public Channel resolve(MemoryAddress memoryAddress) {
        return channelMap.get(memoryAddress);
    }
}
