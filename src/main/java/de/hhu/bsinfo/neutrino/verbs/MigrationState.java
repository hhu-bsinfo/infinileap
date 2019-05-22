package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.EnumConverter;
import java.util.Arrays;

public enum MigrationState {
    MIGRATED(0), REARM(1), ARMED(2);

    private static final MigrationState[] VALUES;

    static {
        int arrayLength = Arrays.stream(values()).mapToInt(element -> element.value).max().orElseThrow() + 1;

        VALUES = new MigrationState[arrayLength];

        for (MigrationState element : MigrationState.values()) {
            VALUES[element.value] = element;
        }
    }

    private final int value;

    MigrationState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static final EnumConverter<MigrationState> CONVERTER = new EnumConverter<>() {

        @Override
        public int toInt(MigrationState enumeration) {
            return enumeration.value;
        }

        @Override
        public MigrationState toEnum(int integer) {
            if (integer < MIGRATED.value || integer > ARMED.value) {
                throw new IllegalArgumentException(String.format("Unkown migration state provided %d", integer));
            }

            return VALUES[integer];
        }
    };
}