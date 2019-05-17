package de.hhu.bsinfo.neutrino.data;

public interface EnumConverter<T extends Enum<T>> {
    int toInt(final T enumeration);
    T toEnum(final int integer);
}
