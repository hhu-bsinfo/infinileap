package de.hhu.bsinfo.neutrino.struct;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.*;
import de.hhu.bsinfo.neutrino.util.*;

import java.lang.ref.Cleaner;

public class Struct implements NativeObject {

    private final StructInformation info;
    private final LocalBuffer byteBuffer;
    private final long handle;
    private final long baseOffset;
    private final String nameSpace;

    private long currentFieldOffset = 0;

    protected Struct() {
        info = StructUtil.getInfo(getClass());
        byteBuffer = LocalBuffer.allocate(info.getSize());
        handle = byteBuffer.getHandle();
        baseOffset = 0;
        nameSpace = null;
    }

    protected Struct(long handle) {
        info = StructUtil.getInfo(getClass());
        byteBuffer = LocalBuffer.wrap(handle, info.getSize());
        baseOffset = 0;
        nameSpace = null;
        this.handle = handle;
    }

    protected Struct(LocalBuffer buffer, long offset) {
        info = StructUtil.getInfo(getClass());
        byteBuffer = buffer;
        handle = buffer.getHandle();
        baseOffset = offset;
        nameSpace = null;
    }

    protected Struct(LocalBuffer buffer, String nameSpace) {
        info = StructUtil.getInfo(getClass());
        byteBuffer = buffer;
        handle = buffer.getHandle();
        baseOffset = 0;
        if (!nameSpace.isEmpty() && nameSpace.charAt(nameSpace.length() - 1) == '.') {
            this.nameSpace = nameSpace;
        } else {
            this.nameSpace = nameSpace + '.';
        }
    }

    public void clear() {
        byteBuffer.clear();
    }

    @Override
    public long getHandle() {
        return handle;
    }

    @Override
    public long getNativeSize() {
        return info.size.get();
    }

    protected final NativeByte byteField(String identifier) {
        return new NativeByte(byteBuffer, offsetOf(identifier));
    }

    protected final NativeByte byteField(long offset) {
        return new NativeByte(byteBuffer, offset);
    }

    protected final NativeByte byteField() {
        var field = new NativeByte(byteBuffer, currentFieldOffset);
        currentFieldOffset += field.getSize();
        return field;
    }

    protected final NativeShort shortField(String identifier) {
        return new NativeShort(byteBuffer, offsetOf(identifier));
    }

    protected final NativeShort shortField(long offset) {
        return new NativeShort(byteBuffer, offset);
    }

    protected final NativeShort shortField() {
        var field = new NativeShort(byteBuffer, currentFieldOffset);
        currentFieldOffset += field.getSize();
        return field;
    }

    protected final NativeInteger integerField(String identifier) {
        return new NativeInteger(byteBuffer, offsetOf(identifier));
    }

    protected final NativeInteger integerField(long offset) {
        return new NativeInteger(byteBuffer, offset);
    }

    protected final NativeInteger integerField(NativeDataType predecessor) {
        var field = new NativeInteger(byteBuffer, currentFieldOffset);
        currentFieldOffset += field.getSize();
        return field;
    }

    protected final NativeLong longField(String identifier) {
        return new NativeLong(byteBuffer, offsetOf(identifier));
    }

    protected final NativeLong longField(long offset) {
        return new NativeLong(byteBuffer, offset);
    }

    protected final NativeLong longField() {
        var field = new NativeLong(byteBuffer, currentFieldOffset);
        currentFieldOffset += field.getSize();
        return field;
    }

    protected final NativeBoolean booleanField(String identifier) {
        return new NativeBoolean(byteBuffer, offsetOf(identifier));
    }

    protected final NativeBoolean booleanField(long offset) {
        return new NativeBoolean(byteBuffer, offset);
    }

    protected final NativeBoolean booleanField() {
        var field = new NativeBoolean(byteBuffer, currentFieldOffset);
        currentFieldOffset += field.getSize();
        return field;
    }

    protected final <T extends Enum<T> & Flag> NativeBitMask<T> bitField(String identifier) {
        return new NativeBitMask<>(byteBuffer, offsetOf(identifier));
    }

    protected final <T extends Enum<T> & Flag> NativeBitMask<T> bitField(long offset) {
        return new NativeBitMask<>(byteBuffer, offset);
    }

    protected final <T extends Enum<T> & Flag> NativeBitMask<T> bitField() {
        var field = new NativeBitMask<T>(byteBuffer, currentFieldOffset);
        currentFieldOffset += field.getSize();
        return field;
    }

    protected final NativeString stringField(String identifier, int length) {
        return new NativeString(byteBuffer, offsetOf(identifier), length);
    }

    protected final NativeString stringField(long offset, int length) {
        return new NativeString(byteBuffer, offset, length);
    }

    protected final NativeString stringField(int length) {
        var field = new NativeString(byteBuffer, currentFieldOffset, length);
        currentFieldOffset += field.getSize();
        return field;
    }

    protected final <T extends Enum<T>> NativeEnum<T> enumField(String identifier, EnumConverter<T> converter) {
        return new NativeEnum<>(byteBuffer, offsetOf(identifier), converter);
    }

    protected final <T extends Enum<T>> NativeEnum<T> enumField(long offset, EnumConverter<T> converter) {
        return new NativeEnum<>(byteBuffer, offset, converter);
    }

    protected final <T extends Enum<T>> NativeEnum<T> enumField(EnumConverter<T> converter) {
        var field = new NativeEnum<T>(byteBuffer, currentFieldOffset, converter);
        currentFieldOffset += field.getSize();
        return field;
    }

    protected final <T extends NativeObject> T valueField(String identifier, ValueFactory<T> factory) {
        return factory.newInstance(byteBuffer, offsetOf(identifier));
    }

    protected final <T extends NativeObject> T valueField(long offset, ValueFactory<T> factory) {
        return factory.newInstance(byteBuffer, offset);
    }

    protected final <T extends NativeObject> T valueField(ValueFactory<T> factory) {
        var field = factory.newInstance(byteBuffer, currentFieldOffset);
        currentFieldOffset += field.getNativeSize();
        return field;
    }

    protected final <T extends NativeObject> T referenceField(String identifier, ReferenceFactory<T> factory) {
        long referenceHandle = byteBuffer.getLong(offsetOf(identifier));
        return referenceHandle == 0 ? null : factory.newInstance(referenceHandle);
    }

    protected final <T extends NativeObject> T referenceField(long offset, ReferenceFactory<T> factory) {
        long referenceHandle = byteBuffer.getLong(offset);
        return referenceHandle == 0 ? null : factory.newInstance(referenceHandle);
    }

    protected final <T extends NativeObject> T anonymousField(AnonymousFactory<T> factory) {
        return factory.newInstance(byteBuffer);
    }

    private long offsetOf(String identifier) {
        return nameSpace == null ?
            baseOffset + info.getOffset(identifier) :
            baseOffset + info.getOffset(nameSpace + identifier);
    }
}
