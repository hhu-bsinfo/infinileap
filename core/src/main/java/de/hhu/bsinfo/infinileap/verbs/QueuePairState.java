package de.hhu.bsinfo.infinileap.verbs;

import de.hhu.bsinfo.infinileap.util.EnumConverter;
import org.linux.rdma.infinileap_h;

import java.util.Arrays;

import static org.linux.rdma.infinileap_h.*;

public enum QueuePairState {
    RESET(IBV_QPS_RESET()),
    INIT(IBV_QPS_INIT()),
    RTR(IBV_QPS_RTR()),
    RTS(IBV_QPS_RTS()),
    SQD(IBV_QPS_SQD()),
    SQE(IBV_QPS_SQE()),
    ERR(IBV_QPS_ERR()),
    UNKNOWN(IBV_QPS_UNKNOWN());

    private static final QueuePairState[] VALUES;

    static {
        int arrayLength = Arrays.stream(values()).mapToInt(element -> element.value).max().orElseThrow() + 1;

        VALUES = new QueuePairState[arrayLength];

        for (QueuePairState element : QueuePairState.values()) {
            VALUES[element.value] = element;
        }
    }

    private final int value;

    QueuePairState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static final EnumConverter<QueuePairState> CONVERTER = new EnumConverter<>() {

        @Override
        public int toInt(QueuePairState enumeration) {
            return enumeration.value;
        }

        @Override
        public QueuePairState toEnum(int integer) {
            if (integer < RESET.value || integer > UNKNOWN.value) {
                throw new IllegalArgumentException(String.format("Unknown state provided %d", integer));
            }

            return VALUES[integer];
        }
    };
}
