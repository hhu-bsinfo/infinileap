package de.hhu.bsinfo.infinileap.binding;

public class Identifier {

    private final int value;

    private Identifier(int value) {
        this.value = value;
    }

    int value() {
        return value;
    }

    public static Identifier of(int value) {
        return new Identifier(value);
    }
}
