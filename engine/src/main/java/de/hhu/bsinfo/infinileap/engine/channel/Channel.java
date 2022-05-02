package de.hhu.bsinfo.infinileap.engine.channel;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.engine.util.BufferPool;
import de.hhu.bsinfo.infinileap.common.util.Distributable;
import de.hhu.bsinfo.infinileap.engine.message.Callback;
import jdk.incubator.foreign.MemorySegment;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.openucx.Communication.ucp_request_free;

public class Channel {

    private static final int REQUEST_SHIFT = Integer.SIZE;

    private final ConcurrentMap<Integer, Callback<?>> requestMap = new ConcurrentHashMap<>();

    private final SendCallback sendCallback = (request, status, data) -> {
        completeRequest(request, data.toRawLongValue());
    };

    private final SendCallback testCallback = (request, status, data) -> {
        ucp_request_free(request);
        requestMap.remove((int) data.toRawLongValue()).onComplete();
    };

    private final Endpoint endpoint;

    private final AtomicInteger REQUEST_COUNTER = new AtomicInteger();

    private final BufferPool bufferPool;

    public Channel(Endpoint endpoint, BufferPool bufferPool) {
        this.endpoint = endpoint;
        this.bufferPool = bufferPool;
    }

    public void sendHeader(Identifier identifier, Distributable header, Callback<Void> callback) {

        // Reserve buffer for outgoing data and register callback
        var pooledBuffer = bufferPool.claim();
        var userData = register(callback, pooledBuffer);

        // Write data to buffer and send it
        var bytes = header.writeTo(pooledBuffer.segment());
        var request = endpoint.sendActive(
                identifier,
                pooledBuffer.segment().asSlice(0L, bytes),
                null,
                pooledBuffer.requestParameters()
        );

        if (Status.is(request, Status.OK)) {
            completeRequest(request, userData);
        }
    }

    public void sendData(Identifier identifier, Distributable data, Callback<Void> callback) {

        // Reserve buffer for outgoing data and register callback
        var pooledBuffer = bufferPool.claim();
        var userData = register(callback, pooledBuffer);

        // Write data to buffer and send it
        var bytes = data.writeTo(pooledBuffer.segment());
        var request = endpoint.sendActive(
                identifier,
                null,
                pooledBuffer.segment().asSlice(0L, bytes),
                pooledBuffer.requestParameters()
        );

        if (Status.is(request, Status.OK)) {
            completeRequest(request, userData);
        }
    }

    private final RequestParameters requestParameters = new RequestParameters()
            .setDataType(DataType.CONTIGUOUS_8_BIT)
            .setFlags(RequestParameters.Flag.ACTIVE_MESSAGE_REPLY)
            .setSendCallback(testCallback);

    public void send(Identifier identifier, MemorySegment header, MemorySegment data, Callback<Void> callback) {
        // Register callback
        var requestIdentifier = REQUEST_COUNTER.incrementAndGet();
        requestParameters.setUserData(requestIdentifier);
        requestMap.put(requestIdentifier, callback);

        // Send message
        var request = endpoint.sendActive(
                identifier,
                header,
                data,
                requestParameters
        );

        if (Status.is(request, Status.OK)) {
            requestMap.remove(requestIdentifier).onComplete();
        }
    }

    public void send(Identifier identifier, Distributable header, Distributable data, Callback<Void> callback) {

        // Reserve buffer for outgoing data and register callback
        var pooledBuffer = bufferPool.claim();
        var userData = register(callback, pooledBuffer);

        // Write data to buffer and send it
        var headerBytes = header.writeTo(pooledBuffer.segment());
        var dataBytes = data.writeTo(pooledBuffer.segment().asSlice(headerBytes));
        var request = endpoint.sendActive(
                identifier,
                pooledBuffer.segment().asSlice(0L, headerBytes),
                pooledBuffer.segment().asSlice(headerBytes, dataBytes),
                pooledBuffer.requestParameters()
        );

        if (Status.is(request, Status.OK)) {
            completeRequest(request, userData);
        }
    }

    private long register(Callback<?> callback, BufferPool.PooledBuffer buffer) {
        var requestIdentifier = REQUEST_COUNTER.incrementAndGet();
        var userData = pack(requestIdentifier, buffer.identifier());
        buffer.requestParameters()
                .setUserData(userData)
                .setSendCallback(sendCallback);
        requestMap.put(requestIdentifier, callback);
        return userData;
    }

    private static long pack(int requestIdentifier, int bufferIdentifier) {
        return (long) requestIdentifier << REQUEST_SHIFT | bufferIdentifier;
    }

    private static int getBufferIdentifier(long userData) {
        return (int) userData;
    }

    private static int getRequestIdentifier(long userData) {
        return (int) (userData >> REQUEST_SHIFT);
    }

    private void completeRequest(long request, long data) {
        if (!Status.isStatus(request)) {
            ucp_request_free(request);
        }

        bufferPool.release(getBufferIdentifier(data));
        requestMap.remove(getRequestIdentifier(data)).onComplete();
    }
}
