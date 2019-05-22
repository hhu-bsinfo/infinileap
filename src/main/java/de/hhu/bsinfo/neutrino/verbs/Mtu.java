package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.EnumConverter;
import java.util.Arrays;

public enum Mtu {
    IBV_MTU_256(1), IBV_MTU_512(2), IBV_MTU_1024(3), IBV_MTU_2048(4), IBV_MTU_4096(5);

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
            if (integer < IBV_MTU_256.value || integer > IBV_MTU_4096.value) {
                throw new IllegalArgumentException(String.format("Unkown operation code provided %d", integer));
            }

            return VALUES[integer];
        }
    };
}