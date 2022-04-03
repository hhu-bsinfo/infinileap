package de.hhu.infinileap.engine.channel;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.infinileap.engine.message.Message;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Channel {

    private ConcurrentMap<Long, CompletableFuture<Void>> requestMap = new ConcurrentHashMap<>();

    private final SendCallback sendCallback = (request, status, data) -> {
        requestMap.get(request).complete(null);
    };

    private final RequestParameters REQUEST_PARAMETERS = new RequestParameters()
            .setDataType(DataType.CONTIGUOUS_8_BIT)
            .setFlags(RequestParameters.Flag.ACTIVE_MESSAGE_REPLY)
            .setSendCallback(sendCallback)
            .disableImmediateCompletion();

    private final Endpoint endpoint;

    public Channel(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public CompletableFuture<Void> send(Identifier identifier, Message message) {
        var request = endpoint.sendActive(identifier, message.header(), message.body(), REQUEST_PARAMETERS);
        if (Status.is(request, Status.OK)) {
            return CompletableFuture.completedFuture(null);
        }

        var future = new CompletableFuture<Void>();
        requestMap.put(request, future);

        return future;
    }

}
