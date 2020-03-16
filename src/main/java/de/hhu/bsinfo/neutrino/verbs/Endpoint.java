package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.NativeByte;
import de.hhu.bsinfo.neutrino.data.NativeEnum;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.NativeError;
import de.hhu.bsinfo.neutrino.util.SystemUtil;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;

@LinkNative("rdma_cm_id")
public final class Endpoint extends Struct implements Closeable {

    private static final int DEFAULT_BACKLOG = 100;

    private final Context verbs = referenceField("verbs", Context::new);
    private final EventChannel eventChannel = referenceField("channel", EventChannel::new);
    private final NativeLong context = longField("context");
    private final QueuePair queuePair = referenceField("qp", QueuePair::new);
    private final Route route = valueField("route", Route::new);
    private final NativeEnum<PortSpace> portSpace = enumField("ps", PortSpace.CONVERTER);
    private final NativeByte portNumber = byteField("port_num");
    private final Event event = referenceField("event", Event::new);
    private final CompletionChannel sendCompletionChannel = referenceField("send_cq_channel", CompletionChannel::new);
    private final CompletionQueue sendCompletionQueue = referenceField("send_cq", CompletionQueue::new);
    private final CompletionChannel receiveCompletionChannel = referenceField("recv_cq_channel", CompletionChannel::new);
    private final CompletionQueue receiveCompletionQueue = referenceField("recv_cq", CompletionQueue::new);
    private final SharedReceiveQueue sharedReceiveQueue = referenceField("srq", SharedReceiveQueue::new);
    private final ProtectionDomain protectionDomain = referenceField("pd",ProtectionDomain::new);
    private final NativeEnum<QueuePair.Type> queuePairType = enumField("qp_type", QueuePair.Type.CONVERTER);

    Endpoint() {}

    Endpoint(long handle) {
        super(handle);
    }

    Endpoint(LocalBuffer buffer, long offset) {
        super(buffer, offset);
    }

    public void connect(@Nullable ConnectionParameters parameters) {
        var result = Result.localInstance();

        CommunicationManager.connect0(getHandle(), parameters != null ? parameters.getHandle() : 0, result.getHandle());
        if (result.isError()) {
            throw new NativeError(SystemUtil.getErrorMessage());
        }
    }

    public void disconnect() {
        var result = Result.localInstance();

        CommunicationManager.disconnect0(getHandle(), result.getHandle());
        if (result.isError()) {
            throw new NativeError(SystemUtil.getErrorMessage());
        }
    }

    public void accept(Endpoint client, ConnectionParameters parameters) {
        var result = Result.localInstance();

        CommunicationManager.accept0(client.getHandle(), parameters.getHandle(), result.getHandle());
        if (result.isError()) {
            throw new NativeError(SystemUtil.getErrorMessage());
        }
    }

    public Endpoint getRequest() {
        var result = Result.localInstance();

        CommunicationManager.getRequest0(getHandle(), result.getHandle());
        if (result.isError()) {
            throw new NativeError(SystemUtil.getErrorMessage());
        }

        return result.get(Endpoint::new);
    }

    public void listen() {
        var result = Result.localInstance();

        CommunicationManager.listen0(getHandle(), DEFAULT_BACKLOG, result.getHandle());
        if (result.isError()) {
            throw new NativeError(SystemUtil.getErrorMessage());
        }
    }

    @Override
    public void close() {
        var result = Result.localInstance();

        CommunicationManager.destroyEndpoint0(getHandle(), result.getHandle());
        if (result.isError()) {
            throw new NativeError(SystemUtil.getErrorMessage());
        }
    }

    public Context getVerbs() {
        return verbs;
    }

    public EventChannel getEventChannel() {
        return eventChannel;
    }

    public long getContext() {
        return context.get();
    }

    public QueuePair getQueuePair() {
        return queuePair;
    }

    public Route getRoute() {
        return route;
    }

    public PortSpace getPortSpace() {
        return portSpace.get();
    }

    public byte getPortNumber() {
        return portNumber.get();
    }

    public Event getEvent() {
        return event;
    }

    public CompletionChannel getSendCompletionChannel() {
        return sendCompletionChannel;
    }

    public CompletionQueue getSendCompletionQueue() {
        return sendCompletionQueue;
    }

    public CompletionChannel getReceiveCompletionChannel() {
        return receiveCompletionChannel;
    }

    public CompletionQueue getReceiveCompletionQueue() {
        return receiveCompletionQueue;
    }

    public SharedReceiveQueue getSharedReceiveQueue() {
        return sharedReceiveQueue;
    }

    public ProtectionDomain getProtectionDomain() {
        return protectionDomain;
    }

    public QueuePair.Type getQueuePairType() {
        return queuePairType.get();
    }
}
