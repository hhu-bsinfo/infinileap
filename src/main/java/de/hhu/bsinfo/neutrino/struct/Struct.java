package de.hhu.bsinfo.neutrino.struct;

import de.hhu.bsinfo.neutrino.struct.field.*;
import de.hhu.bsinfo.neutrino.util.*;
import de.hhu.bsinfo.neutrino.util.factory.AnonymousFactory;
import de.hhu.bsinfo.neutrino.util.factory.ReferenceFactory;
import de.hhu.bsinfo.neutrino.util.factory.ValueFactory;
import de.hhu.bsinfo.neutrino.util.flag.ByteFlag;
import de.hhu.bsinfo.neutrino.util.flag.IntegerFlag;
import de.hhu.bsinfo.neutrino.util.flag.LongFlag;
import de.hhu.bsinfo.neutrino.util.flag.ShortFlag;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.AtomicBuffer;

public class Struct implements NativeObject {

    /**
     * This struct's native information (size and field offsets).
     */
    private final StructInformation info;

    /**
     * This struct's backing buffer.
     */
    private final AtomicBuffer buffer;

    /**
     * This struct's offset within the backing buffer.
     */
    private final int baseOffset;

    /**
     * This struct's namespace.
     */
    private final String nameSpace;

    /**
     * This struct's native size in bytes.
     */
    private final int size;

    /**
     * The current field offset used for dynamically created structs.
     */
    private int currentFieldOffset = 0;

    protected Struct() {
        info = StructUtil.getInfo(getClass());
        buffer = MemoryUtil.allocateAligned(info.getSize(), MemoryAlignment.CACHE);
        baseOffset = 0;
        nameSpace = null;
        size = info.getSize();
    }

    protected Struct(long handle) {
        info = StructUtil.getInfo(getClass());
        buffer = MemoryUtil.wrap(handle, info.getSize());
        baseOffset = 0;
        nameSpace = null;
        size = info.getSize();
    }

    protected Struct(AtomicBuffer buffer, int offset) {
        info = StructUtil.getInfo(getClass());
        this.buffer = buffer;
        baseOffset = offset;
        nameSpace = null;
        size = info.getSize();
    }

    protected Struct(AtomicBuffer buffer, String nameSpace) {
        info = StructUtil.getInfo(getClass());
        this.buffer = buffer;
        baseOffset = 0;
        size = info.getSize();
        if (!nameSpace.isEmpty() && nameSpace.charAt(nameSpace.length() - 1) == '.') {
            this.nameSpace = nameSpace;
        } else {
            this.nameSpace = nameSpace + '.';
        }
    }

    public void clear() {
        buffer.setMemory(0, buffer.capacity(), (byte) 0);
    }

    @Override
    public long getHandle() {
        return buffer.addressOffset();
    }

    @Override
    public int getNativeSize() {
        return size;
    }

    protected final NativeByte byteField(String identifier) {
        return new NativeByte(buffer, offsetOf(identifier));
    }

    protected final NativeByte byteField(int offset) {
        return new NativeByte(buffer, offset);
    }

    protected final NativeByte byteField() {
        var field = new NativeByte(buffer, currentFieldOffset);
        currentFieldOffset += field.getSize();
        return field;
    }

    protected final NativeShort shortField(String identifier) {
        return new NativeShort(buffer, offsetOf(identifier));
    }

    protected final NativeShort shortField(int offset) {
        return new NativeShort(buffer, offset);
    }

    protected final NativeShort shortField() {
        var field = new NativeShort(buffer, currentFieldOffset);
        currentFieldOffset += field.getSize();
        return field;
    }

    protected final NativeInteger integerField(String identifier) {
        return new NativeInteger(buffer, offsetOf(identifier));
    }

    protected final NativeInteger integerField(int offset) {
        return new NativeInteger(buffer, offset);
    }

    protected final NativeInteger integerField() {
        var field = new NativeInteger(buffer, currentFieldOffset);
        currentFieldOffset += field.getSize();
        return field;
    }

    protected final NativeLong longField(String identifier) {
        return new NativeLong(buffer, offsetOf(identifier));
    }

    protected final NativeLong longField(int offset) {
        return new NativeLong(buffer, offset);
    }

    protected final NativeLong longField() {
        var field = new NativeLong(buffer, currentFieldOffset);
        currentFieldOffset += field.getSize();
        return field;
    }

    protected final NativeDouble doubleField(String identifier) {
        return new NativeDouble(buffer, offsetOf(identifier));
    }

    protected final NativeDouble doubleField(int offset) {
        return new NativeDouble(buffer, offset);
    }

    protected final NativeDouble doubleField() {
        var field = new NativeDouble(buffer, currentFieldOffset);
        currentFieldOffset += field.getSize();
        return field;
    }

    protected final NativeBoolean booleanField(String identifier) {
        return new NativeBoolean(buffer, offsetOf(identifier));
    }

    protected final NativeBoolean booleanField(int offset) {
        return new NativeBoolean(buffer, offset);
    }

    protected final NativeBoolean booleanField() {
        var field = new NativeBoolean(buffer, currentFieldOffset);
        currentFieldOffset += field.getSize();
        return field;
    }

    protected final <T extends Enum<T> & ByteFlag> NativeByteBitMask<T> byteBitField(String identifier) {
        return new NativeByteBitMask<>(buffer, offsetOf(identifier));
    }

    protected final <T extends Enum<T> & ByteFlag> NativeByteBitMask<T> byteBitField(int offset) {
        return new NativeByteBitMask<>(buffer, offset);
    }

    protected final <T extends Enum<T> & ByteFlag> NativeByteBitMask<T> byteBitField() {
        var field = new NativeByteBitMask<T>(buffer, currentFieldOffset);
        currentFieldOffset += field.getSize();
        return field;
    }

    protected final <T extends Enum<T> & ShortFlag> NativeShortBitMask<T> shortBitField(String identifier) {
        return new NativeShortBitMask<>(buffer, offsetOf(identifier));
    }

    protected final <T extends Enum<T> & ShortFlag> NativeShortBitMask<T> shortBitField(int offset) {
        return new NativeShortBitMask<>(buffer, offset);
    }

    protected final <T extends Enum<T> & ShortFlag> NativeShortBitMask<T> shortBitField() {
        var field = new NativeShortBitMask<T>(buffer, currentFieldOffset);
        currentFieldOffset += field.getSize();
        return field;
    }

    protected final <T extends Enum<T> & IntegerFlag> NativeIntegerBitMask<T> integerBitField(String identifier) {
        return new NativeIntegerBitMask<>(buffer, offsetOf(identifier));
    }

    protected final <T extends Enum<T> & IntegerFlag> NativeIntegerBitMask<T> integerBitField(int offset) {
        return new NativeIntegerBitMask<>(buffer, offset);
    }

    protected final <T extends Enum<T> & IntegerFlag> NativeIntegerBitMask<T> integerBitField() {
        var field = new NativeIntegerBitMask<T>(buffer, currentFieldOffset);
        currentFieldOffset += field.getSize();
        return field;
    }

    protected final <T extends Enum<T> & LongFlag> NativeLongBitMask<T> longBitField(String identifier) {
        return new NativeLongBitMask<>(buffer, offsetOf(identifier));
    }

    protected final <T extends Enum<T> & LongFlag> NativeLongBitMask<T> longBitField(int offset) {
        return new NativeLongBitMask<>(buffer, offset);
    }

    protected final <T extends Enum<T> & LongFlag> NativeLongBitMask<T> longBitField() {
        var field = new NativeLongBitMask<T>(buffer, currentFieldOffset);
        currentFieldOffset += field.getSize();
        return field;
    }

    protected final NativeString stringField(String identifier, int length) {
        return new NativeString(buffer, offsetOf(identifier), length);
    }

    protected final NativeString stringField(int offset, int length) {
        return new NativeString(buffer, offset, length);
    }

    protected final NativeString stringField(int length) {
        var field = new NativeString(buffer, currentFieldOffset, length);
        currentFieldOffset += field.getSize();
        return field;
    }

    protected final <T extends Enum<T>> NativeEnum<T> enumField(String identifier, EnumConverter<T> converter) {
        return new NativeEnum<>(buffer, offsetOf(identifier), converter);
    }

    protected final <T extends Enum<T>> NativeEnum<T> enumField(int offset, EnumConverter<T> converter) {
        return new NativeEnum<>(buffer, offset, converter);
    }

    protected final <T extends Enum<T>> NativeEnum<T> enumField(EnumConverter<T> converter) {
        var field = new NativeEnum<T>(buffer, currentFieldOffset, converter);
        currentFieldOffset += field.getSize();
        return field;
    }

    protected final <T extends NativeObject> T valueField(String identifier, ValueFactory<T> factory) {
        return factory.newInstance(buffer, offsetOf(identifier));
    }

    protected final <T extends NativeObject> T valueField(int offset, ValueFactory<T> factory) {
        return factory.newInstance(buffer, offset);
    }

    protected final <T extends NativeObject> T valueField(ValueFactory<T> factory) {
        var field = factory.newInstance(buffer, currentFieldOffset);
        currentFieldOffset += field.getNativeSize();
        return field;
    }

    protected final <T extends NativeObject> T referenceField(String identifier, ReferenceFactory<T> factory) {
        var handle = buffer.getLong(offsetOf(identifier));
        if (handle == 0) {
            return null;
        }

        return factory.newInstance(handle);
    }

    protected final <T extends NativeObject> T referenceField(String identifier) {
        return referenceField(offsetOf(identifier));
    }

    protected final <T extends NativeObject> T referenceField(int offset) {
        return NativeObjectRegistry.getObject(buffer.getLong(offset));
    }

    protected final <T extends NativeObject> T anonymousField(AnonymousFactory<T> factory) {
        return factory.newInstance(buffer);
    }

    private int offsetOf(String identifier) {
        return nameSpace == null ?
            baseOffset + info.getOffset(identifier) :
            baseOffset + info.getOffset(nameSpace + identifier);
    }

    @SuppressWarnings("unchecked")
    public <T extends Struct> T wrap(long handle) {
        buffer.wrap(handle, size);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Struct> T wrap(DirectBuffer buffer, int offset) {
        buffer.wrap(buffer, offset, size);
        return (T) this;
    }
}
