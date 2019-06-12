package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.NativeBitMask;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.Flag;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

@LinkNative("ibv_rwq_ind_table")
public class ReceiveWorkQueueIndirectionTable extends Struct implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveWorkQueueIndirectionTable.class);

    private final Context context = referenceField("context", Context::new);
    private final NativeInteger tableHandle = integerField("ind_tbl_handle");
    private final NativeInteger tableNumber = integerField("ind_tbl_num");
    private final NativeInteger compatibilityMask = integerField("comp_mask");

    ReceiveWorkQueueIndirectionTable(final long handle) {
        super(handle);
    }

    ReceiveWorkQueueIndirectionTable(final LocalBuffer buffer, final int offset) {
        super(buffer, offset);
    }

    @Override
    public void close() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.destroyReceiveWorkQueueIndirectionTable(getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Destroying receive work queue indirection table failed with error [{}]", result.getStatus());
        }

        result.releaseInstance();
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

    public enum InitialAttributeFlag implements Flag {
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
        private final NativeBitMask<InitialAttributeFlag> compatibilityMask = bitField("comp_mask");

        private final LocalBuffer buffer;

        public InitialAttributes(int logarithmicTableSize) {
            this.logarithmicTableSize.set(logarithmicTableSize);

            buffer = LocalBuffer.allocate((long) (Math.pow(2, logarithmicTableSize) * Long.BYTES));

            tableHandle.set(buffer.getHandle());
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

        public void setWorkQueue(final long index, final WorkQueue workQueue) {
            long size = (long) Math.pow(2, logarithmicTableSize.get());

            if(index > size) {
                throw new IndexOutOfBoundsException("Index " + index + " is greater than array size (" + size + ")");
            }

            buffer.putLong(index * Long.BYTES, workQueue.getHandle());
        }

        public void setCompatibilityMask(final InitialAttributeFlag... flags) {
            compatibilityMask.set(flags);
        }
    }
}
