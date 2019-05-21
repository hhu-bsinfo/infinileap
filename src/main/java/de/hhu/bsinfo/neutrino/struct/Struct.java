package de.hhu.bsinfo.neutrino.struct;

import de.hhu.bsinfo.neutrino.data.EnumConverter;
import de.hhu.bsinfo.neutrino.data.NativeByte;
import de.hhu.bsinfo.neutrino.data.NativeEnum;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.data.NativeShort;
import de.hhu.bsinfo.neutrino.data.NativeString;
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

    protected Struct(String name) {
        info = StructUtil.getInfo(name);
        byteBuffer = ByteBuffer.allocateDirect(info.getSize());
        byteBuffer.order(ByteOrder.nativeOrder());
        handle = MemoryUtil.getAddress(byteBuffer);
        baseOffset = 0;
    }

    protected Struct(String name, long handle) {
        info = StructUtil.getInfo(name);
        byteBuffer = MemoryUtil.wrap(handle, info.getSize());
        byteBuffer.order(ByteOrder.nativeOrder());
        baseOffset = 0;
        this.handle = handle;
        CLEANER.register(this, new StructCleaner(handle));
    }

    protected Struct(String name, ByteBuffer buffer, int offset) {
        info = StructUtil.getInfo(name);
        byteBuffer = buffer;
        handle = MemoryUtil.getAddress(byteBuffer);
        baseOffset = offset;
    }

    protected final ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    @Override
    public long getHandle() {
        return handle;
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

    protected final NativeString stringField(String identifier, int length) {
        return new NativeString(byteBuffer, offsetOf(identifier), length);
    }

    protected final <T extends Enum<T>> NativeEnum<T> enumField(String identifier, EnumConverter<T> converter) {
        return new NativeEnum<>(byteBuffer, offsetOf(identifier), converter);
    }

    protected final <T extends Struct> T valueField(String identifier, ValueFactory<T> factory) {
        return factory.newInstance(byteBuffer, offsetOf(identifier));
    }

    protected final <T extends Struct> T referenceField(String identifier, ReferenceFactory<T> factory) {
        return factory.newInstance(byteBuffer.getLong(offsetOf(identifier)));
    }

    private int offsetOf(String identifier) {
        return baseOffset + info.getOffset(identifier);
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
