package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeEnum;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.Poolable;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LinkNative("ibv_async_event")
public class AsyncEvent extends Struct implements Poolable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncEvent.class);

    // These five values are part of a struct. At any given time, only one of the is valid (depending in the eventType)
    private final NativeLong completionQueueHandle = longField("element.cq");
    private final NativeLong queuePairHandle = longField("element.qp");
    private final NativeLong sharedReceiveQueueHandle = longField("element.srq");
    private final NativeLong workQueueHandle = longField("element.wq"); // TODO: Implement WorkQueue in neutrino
    private final NativeInteger portNumber = integerField("element.port_num");

    private final NativeEnum<EventType> eventType = enumField("event_type", EventType.CONVERTER);

    AsyncEvent() {}

    AsyncEvent(final long handle) {
        super(handle);
    }

    public void acknowledge() {
        Verbs.acknowledgeAsyncEvent(getHandle());
        releaseInstance();
    }

    @Nullable
    public CompletionQueue getCompletionQueue() {
        EventType type = eventType.get();

        if(!type.isCompletionQueueEvent()) {
            LOGGER.error("Event of type {} does not allow getting a completion queue", type);
            return null;
        }

        return new CompletionQueue(completionQueueHandle.get());
    }

    @Nullable
    public QueuePair getQueuePair() {
        EventType type = eventType.get();

        if(!type.isQueuePairEvent()) {
            LOGGER.error("Event of type {} does not allow getting a queue pair", type);
            return null;
        }

        return new QueuePair(queuePairHandle.get());
    }

    @Nullable
    public SharedReceiveQueue getSharedReceiveQueue() {
        EventType type = eventType.get();

        if(!type.isSharedReceiveQueueEvent()) {
            LOGGER.error("Event of type {} does not allow getting a shared receive queue", type);
            return null;
        }

        return new SharedReceiveQueue(sharedReceiveQueueHandle.get());
    }

    public int getPortNumber() {
        EventType type = eventType.get();

        if(!type.isPortEvent()) {
            LOGGER.error("Event of type {} does not allow getting a port number", type);
            return -1;
        }

        return portNumber.get();
    }

    public EventType getEventType() {
        return eventType.get();
    }

    @Override
    public String toString() {
        return "AsyncEvent {\n" +
            "\teventType=" + eventType +
            "\n}";
    }
}
