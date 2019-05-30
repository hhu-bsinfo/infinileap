package de.hhu.bsinfo.neutrino.struct;

import de.hhu.bsinfo.neutrino.data.EnumConverter;
import de.hhu.bsinfo.neutrino.data.NativeBitMask;
import de.hhu.bsinfo.neutrino.data.NativeBoolean;
import de.hhu.bsinfo.neutrino.data.NativeByte;
import de.hhu.bsinfo.neutrino.data.NativeEnum;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.data.NativeShort;
import de.hhu.bsinfo.neutrino.data.NativeString;
import de.hhu.bsinfo.neutrino.util.AnonymousFactory;
import de.hhu.bsinfo.neutrino.util.Flag;
import de.hhu.bsinfo.neutrino.util.MemoryUtil;
import de.hhu.bsinfo.neutrino.util.ReferenceFactory;
import de.hhu.bsinfo.neutrino.util.StructUtil;
import de.hhu.bsinfo.neutrino.util.ValueFactory;
import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Struct implements NativeObject {

    private static final Cleaner CLEANER = Cleaner.create();

    private final StructInformation info;
    private final ByteBuffer byteBuffer;
    private final long handle;
    private final int baseOffset;
    private final String nameSpace;

    protected Struct() {
        info = StructUtil.getInfo(getClass());
        byteBuffer = ByteBuffer.allocateDirect(info.getSize());
        byteBuffer.order(ByteOrder.nativeOrder());
        handle = MemoryUtil.getAddress(byteBuffer);
        baseOffset = 0;
        nameSpace = null;
    }

    protected Struct(long handle) {
        info = StructUtil.getInfo(getClass());
        byteBuffer = MemoryUtil.wrap(handle, info.getSize());
        byteBuffer.order(ByteOrder.nativeOrder());
        baseOffset = 0;
        nameSpace = null;
        this.handle = handle;
        CLEANER.register(this, new StructCleaner(handle));
    }

    protected Struct(ByteBuffer buffer, int offset) {
        info = StructUtil.getInfo(getClass());
        byteBuffer = buffer;
        handle = MemoryUtil.getAddress(byteBuffer);
        baseOffset = offset;
        nameSpace = null;
    }

    protected Struct(ByteBuffer buffer, String nameSpace) {
        info = StructUtil.getInfo(getClass());
        byteBuffer = buffer;
        handle = MemoryUtil.getAddress(byteBuffer);
        baseOffset = 0;
        if (!nameSpace.isEmpty() && nameSpace.charAt(nameSpace.length() - 1) == '.') {
            this.nameSpace = nameSpace;
        } else {
            this.nameSpace = nameSpace + '.';
        }
    }

    protected final ByteBuffer getByteBuffer() {
        return byteBuffer;
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

    protected final NativeShort shortField(String identifier) {
        return new NativeShort(byteBuffer, offsetOf(identifier));
    }

    protected final NativeInteger integerField(String identifier) {
        return new NativeInteger(byteBuffer, offsetOf(identifier));
    }

    protected final NativeLong longField(String identifier) {
        return new NativeLong(byteBuffer, offsetOf(identifier));
    }

    protected final NativeBoolean booleanField(String identifier) {
        return new NativeBoolean(byteBuffer, offsetOf(identifier));
    }

    protected final <T extends Enum<T> & Flag> NativeBitMask<T> bitField(String identifier) {
        return new NativeBitMask<>(byteBuffer, offsetOf(identifier));
    }

    protected final NativeString stringField(String identifier, int length) {
        return new NativeString(byteBuffer, offsetOf(identifier), length);
    }

    protected final <T extends Enum<T>> NativeEnum<T> enumField(String identifier, EnumConverter<T> converter) {
        return new NativeEnum<>(byteBuffer, offsetOf(identifier), converter);
    }

    protected final <T extends NativeObject> T valueField(String identifier, ValueFactory<T> factory) {
        return factory.newInstance(byteBuffer, offsetOf(identifier));
    }

    protected final <T extends NativeObject> T referenceField(String identifier, ReferenceFactory<T> factory) {
        long referenceHandle = byteBuffer.getLong(offsetOf(identifier));

        return referenceHandle == 0 ? null : factory.newInstance(byteBuffer.getLong(offsetOf(identifier)));
    }

    protected final <T extends NativeObject> T anonymousField(AnonymousFactory<T> factory) {
        return factory.newInstance(byteBuffer);
    }

    private int offsetOf(String identifier) {
        return nameSpace == null ?
            baseOffset + info.getOffset(identifier) :
            baseOffset + info.getOffset(nameSpace + identifier);
    }

    private static final class StructCleaner implements Runnable {

        private final long handle;

        private StructCleaner(long handle) {
            this.handle = handle;
        }

        @Override
        public void run() {
            MemoryUtil.free(handle);
        }
    }
}
