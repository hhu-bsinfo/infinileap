package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.EnumConverter;
import de.hhu.bsinfo.neutrino.data.NativeEnum;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.Poolable;
import java.util.Arrays;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LinkNative("ibv_async_event")
public class AsyncEvent extends Struct implements Poolable {

    public enum EventType {
        CQ_ERR(0), QP_FATAL(1), QP_REQ_ERR(2), QP_ACCESS_ERR(3),
        COMM_EST(4), SQ_DRAINED(5), PATH_MIG(6), PATH_MIG_ERR(7),
        DEVICE_FATAL(8), PORT_ACTIVE(9), PORT_ERR(10), LID_CHANGE(11),
        PKEY_CHANGE(12), SM_CHANGE(13), SRQ_ERR(14), SRQ_LIMIT_REACHED(15),
        QP_LAST_WQE_REACHED(16), CLIENT_REREGISTER(17), GID_CHANGE(18), WQ_FATAL(19);

        private static final EventType[] VALUES;

        static {
            int arrayLength = Arrays.stream(values()).mapToInt(element -> element.value).max().orElseThrow() + 1;

            VALUES = new EventType[arrayLength];

            for (EventType element : EventType.values()) {
                VALUES[element.value] = element;
            }
        }

        private final int value;

        EventType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public boolean isDeviceEvent() {
            return value == DEVICE_FATAL.value;
        }

        public boolean isPortEvent() {
            return value == PORT_ACTIVE.value || value == LID_CHANGE.value || value == PKEY_CHANGE.value ||
                value == GID_CHANGE.value || value == SM_CHANGE.value || value == CLIENT_REREGISTER.value ||
                value == PORT_ERR.value;
        }

        public boolean isCompletionQueueEvent() {
            return value == CQ_ERR.value;
        }

        public boolean isSharedReceiveQueueEvent() {
            return value == SRQ_ERR.value || value == SRQ_LIMIT_REACHED.value;
        }

        public boolean isQueuePairEvent() {
            return value == COMM_EST.value || value == SQ_DRAINED.value || value == PATH_MIG.value ||
                value == QP_LAST_WQE_REACHED.value || value == QP_FATAL.value || value == QP_REQ_ERR.value ||
                value == QP_ACCESS_ERR.value || value == PATH_MIG_ERR.value;
        }

        public boolean isWorkQueueEvent() {
            return value == WQ_FATAL.value;
        }

        public static final EnumConverter<EventType> CONVERTER = new EnumConverter<>() {

            @Override
            public int toInt(EventType enumeration) {
                return enumeration.value;
            }

            @Override
            public EventType toEnum(int integer) {
                if (integer < CQ_ERR.value || integer > WQ_FATAL.value) {
                    throw new IllegalArgumentException(String.format("Unknown operation code provided %d", integer));
                }

                return VALUES[integer];
            }
        };
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncEvent.class);

    // These five values are part of a struct. At any given time, only one of the is valid (depending in the eventType)
    private final NativeLong completionQueueHandle = longField("element.cq");
    private final NativeLong queuePairHandle = longField("element.qp");
    private final NativeLong sharedReceiveQueueHandle = longField("element.srq");
    private final NativeLong workQueueHandle = longField("element.wq"); // TODO: Implement WorkQueue in neutrino
    private final NativeInteger portNumber = integerField("element.port_num");

    private final NativeEnum<EventType> eventType = enumField("event_type", EventType.CONVERTER);

    AsyncEvent() {}

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
