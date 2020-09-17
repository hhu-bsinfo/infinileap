package de.hhu.bsinfo.infinileap.verbs;

import de.hhu.bsinfo.infinileap.util.EnumConverter;
import org.linux.rdma.infinileap_h;

import java.util.Arrays;

import static org.linux.rdma.infinileap_h.*;

public enum QueuePairType {
    RC(IBV_QPT_RC()),
    UC(IBV_QPT_UC()),
    UD(IBV_QPT_UD()),
    RAW_PACKET(IBV_QPT_RAW_PACKET()),
    XRC_SEND(IBV_QPT_XRC_SEND()),
    XRC_RECV(IBV_QPT_XRC_RECV()),
    DRIVER(IBV_QPT_DRIVER());

    private static final QueuePairType[] VALUES;

    static {
        int arrayLength = Arrays.stream(values()).mapToInt(element -> element.value).max().orElseThrow() + 1;

        VALUES = new QueuePairType[arrayLength];

        for (QueuePairType element : QueuePairType.values()) {
            VALUES[element.value] = element;
        }
    }

    private final int value;

    QueuePairType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static final EnumConverter<QueuePairType> CONVERTER = new EnumConverter<>() {

        @Override
        public int toInt(QueuePairType enumeration) {
            return enumeration.value;
        }

        @Override
        public QueuePairType toEnum(int integer) {
            if (integer < RC.value || integer > UD.value && integer < RAW_PACKET.value
                    || integer > XRC_RECV.value && integer < DRIVER.value
                    || integer > DRIVER.value) {
                throw new IllegalArgumentException(String.format("Unknown operation code provided %d", integer));
            }

            return VALUES[integer];
        }
    };
}
