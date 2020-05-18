package de.hhu.bsinfo.neutrino.struct.field;

public interface EnumConverter<T extends Enum<T>> {
    int toInt(final T enumeration);
    T toEnum(final int integer);
}
