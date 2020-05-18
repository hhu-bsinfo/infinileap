package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.struct.field.NativeIntegerBitMask;
import de.hhu.bsinfo.neutrino.struct.field.NativeInteger;
import de.hhu.bsinfo.neutrino.struct.field.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.MemoryAlignment;
import de.hhu.bsinfo.neutrino.util.MemoryUtil;
import de.hhu.bsinfo.neutrino.util.SystemUtil;
import de.hhu.bsinfo.neutrino.util.flag.IntegerFlag;
import de.hhu.bsinfo.neutrino.struct.LinkNative;
import de.hhu.bsinfo.neutrino.util.NativeObjectRegistry;
import org.agrona.concurrent.AtomicBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;

@LinkNative("ibv_rwq_ind_table")
public class ReceiveWorkQueueIndirectionTable extends Struct implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveWorkQueueIndirectionTable.class);

    private final Context context = referenceField("context");
    private final NativeInteger tableHandle = integerField("ind_tbl_handle");
    private final NativeInteger tableNumber = integerField("ind_tbl_num");
    private final NativeInteger compatibilityMask = integerField("comp_mask");

    ReceiveWorkQueueIndirectionTable(final long handle) {
        super(handle);
    }

    ReceiveWorkQueueIndirectionTable(AtomicBuffer buffer, int offset) {
        super(buffer, offset);
    }

    @Override
    public void close() throws IOException {
        var result = Result.localInstance();

        Verbs.destroyReceiveWorkQueueIndirectionTable(getHandle(), result.getHandle());
        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        NativeObjectRegistry.deregisterObject(this);
    }

    public Context getContext() {
        return context;
    }

    public int getTableHandle() {
        return tableHandle.get();
    }

    public int getTableNumber() {
        return tableNumber.get();
    }

    public int getCompatibilityMask() {
        return compatibilityMask.get();
    }

    @Override
    public String toString() {
        return "ReceiveWorkQueueIndirectionTable {" +
                ",\n\ttableHandle=" + tableHandle +
                ",\n\ttableNumber=" + tableNumber +
                ",\n\tcompatibilityMask=" + compatibilityMask +
                "\n}";
    }

    public enum InitialAttributeFlag implements IntegerFlag {
        RESERVED(1);

        private final int value;

        InitialAttributeFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    @LinkNative("ibv_rwq_ind_table_init_attr")
    public static final class InitialAttributes extends Struct {

        private final NativeInteger logarithmicTableSize = integerField("log_ind_tbl_size");
        private final NativeLong tableHandle = longField("ind_tbl");
        private final NativeIntegerBitMask<InitialAttributeFlag> compatibilityMask = integerBitField("comp_mask");

        private final AtomicBuffer buffer;

        public InitialAttributes(int logarithmicTableSize) {
            this.logarithmicTableSize.set(logarithmicTableSize);

            buffer = MemoryUtil.allocateAligned((int) (Math.pow(2, logarithmicTableSize) * Long.BYTES), MemoryAlignment.CACHE);

            tableHandle.set(buffer.addressOffset());
        }

        public InitialAttributes(int logarithmicTableSize, Consumer<InitialAttributes> configurator) {
            this(logarithmicTableSize);
            configurator.accept(this);
        }

        public int getLogarithmicTableSize() {
            return logarithmicTableSize.get();
        }

        public long getTableHandle() {
            return tableHandle.get();
        }

        public int getCompatibilityMask() {
            return compatibilityMask.get();
        }

        public void setWorkQueue(final int index, final WorkQueue workQueue) {
            long size = (long) Math.pow(2, logarithmicTableSize.get());

            if(index > size) {
                throw new IndexOutOfBoundsException("Index " + index + " is greater than array size (" + size + ")");
            }

            buffer.putLong(index * Long.BYTES, workQueue.getHandle());
        }

        public void setCompatibilityMask(final InitialAttributeFlag... flags) {
            compatibilityMask.set(flags);
        }

        @Override
        public String toString() {
            return "InitialAttributes {" +
                    "\n\tlogarithmicTableSize=" + logarithmicTableSize +
                    ",\n\ttableHandle=" + tableHandle +
                    ",\n\tcompatibilityMask=" + compatibilityMask +
                    ",\n\ttableBuffer=" + buffer +
                    "\n}";
        }
    }
}
