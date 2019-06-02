package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.EnumConverter;
import java.util.Arrays;

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
