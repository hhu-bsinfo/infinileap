package de.hhu.bsinfo.neutrino.struct;

import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.util.MemoryUtil;
import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Struct implements NativeObject {

    private static final Cleaner CLEANER = Cleaner.create();

    private final ByteBuffer byteBuffer;
    private final long handle;

    protected Struct(int size) {
        byteBuffer = ByteBuffer.allocateDirect(size);
        byteBuffer.order(ByteOrder.nativeOrder());
        handle = MemoryUtil.getAddress(byteBuffer);
    }

    protected Struct(long handle, int size) {
        byteBuffer = MemoryUtil.wrap(handle, size);
        byteBuffer.order(ByteOrder.nativeOrder());
        this.handle = handle;
        CLEANER.register(this, new StructCleaner(handle));
    }

    protected final ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public long getHandle() {
        return handle;
    }

    private static class StructCleaner implements Runnable {

        private long handle;

        private StructCleaner(long handle) {
            this.handle = handle;
        }

        public void run() {
            MemoryUtil.free(handle);
        }
    }
}
