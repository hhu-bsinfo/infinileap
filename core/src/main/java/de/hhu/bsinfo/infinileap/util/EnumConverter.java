package de.hhu.bsinfo.infinileap.util;

public interface EnumConverter<T extends Enum<T>> {
    int toInt(final T enumeration);
    T toEnum(final int integer);
}
