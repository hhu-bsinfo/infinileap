package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.EnumConverter;
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
}