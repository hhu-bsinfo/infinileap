package de.hhu.infinileap.engine.channel;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.infinileap.engine.message.Message;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.openucx.Communication.ucp_request_free;

public class Channel {

    private ConcurrentMap<Long, CompletableFuture<Void>> requestMap = new ConcurrentHashMap<>();

    private final SendCallback sendCallback = (request, status, data) -> {
        requestMap.get(data.toRawLongValue()).complete(null);
        ucp_request_free(request);
    };

    private final RequestParameters requestParameters = new RequestParameters()
            .setDataType(DataType.CONTIGUOUS_8_BIT)
            .setFlags(RequestParameters.Flag.ACTIVE_MESSAGE_REPLY)
            .setSendCallback(sendCallback)
            .disableImmediateCompletion();

    private final Endpoint endpoint;

    private final AtomicLong REQUEST_COUNTER = new AtomicLong();

    public Channel(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public CompletableFuture<Void> send(Identifier identifier, Message message) {
        var id = REQUEST_COUNTER.incrementAndGet();
        var future = new CompletableFuture<Void>();
        requestParameters.setUserData(id);
        requestMap.put(id, future);

        var request = endpoint.sendActive(identifier, message.header(), message.body(), requestParameters);
        if (Status.is(request, Status.OK)) {
            ucp_request_free(request);
            future.complete(null);
        }

        return future;
    }

}
