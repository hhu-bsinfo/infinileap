package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.struct.field.EnumConverter;
import java.util.Arrays;

public enum Mtu {
    MTU_256(1), MTU_512(2), MTU_1024(3), MTU_2048(4), MTU_4096(5);

    private static final Mtu[] VALUES;

    static {
        int arrayLength = Arrays.stream(values()).mapToInt(element -> element.value).max().orElseThrow() + 1;

        VALUES = new Mtu[arrayLength];

        for (Mtu element : Mtu.values()) {
            VALUES[element.value] = element;
        }
    }

    private final int value;

    Mtu(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public int getMtuValue() {
        switch (this) {
            case MTU_256:
                return 256;
            case MTU_512:
                return 512;
            case MTU_1024:
                return 1024;
            case MTU_2048:
                return 2048;
            case MTU_4096:
                return 4096;
            default:
                return 4096;
        }
    }

    public static final EnumConverter<Mtu> CONVERTER = new EnumConverter<>() {

        @Override
        public int toInt(Mtu enumeration) {
            return enumeration.value;
        }

        @Override
        public Mtu toEnum(int integer) {
            if (integer < MTU_256.value || integer > MTU_4096.value) {
                throw new IllegalArgumentException(String.format("Unknown operation code provided %d", integer));
            }

            return VALUES[integer];
        }
    };

    public static Mtu fromValue(int mtu) {
        if (mtu <= MTU_256.value) {
            return MTU_256;
        } else if (mtu > MTU_256.value && mtu <= MTU_512.value) {
            return MTU_512;
        } else if (mtu > MTU_512.value && mtu <= MTU_1024.value) {
            return MTU_1024;
        } else if (mtu > MTU_1024.value && mtu <= MTU_2048.value) {
            return MTU_2048;
        } else {
            return MTU_4096;
        }
    }
}