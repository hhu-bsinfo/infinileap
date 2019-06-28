package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LinkNative("ibv_dm")
public class DeviceMemory extends Struct implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceMemory.class);

    private final Context context = referenceField("context");
    private final NativeInteger compatibilityMask = integerField("comp_mask");

    DeviceMemory(final long handle) {
        super(handle);
    }

    public Context getContext() {
        return context;
    }

    public int getCompatibilityMask() {
        return compatibilityMask.get();
    }

    public boolean copyFromDeviceMemory(long sourceOffset, LocalBuffer targetBuffer, long targetOffset, long length) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.copyFromDeviceMemory(targetBuffer.getHandle() + targetOffset, getHandle(), sourceOffset, length, result.getHandle());
        boolean isError = result.isError();
        if(isError) {
            LOGGER.error("Copying from device memory to local buffer failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        result.releaseInstance();

        return !isError;
    }

    public boolean copyToDeviceMemory(LocalBuffer sourceBuffer, long sourceOffset, long targetOffset, long length) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.copyToDeviceMemory(getHandle(), targetOffset, sourceBuffer.getHandle() + sourceOffset, length, result.getHandle());
        boolean isError = result.isError();
        if(isError) {
            LOGGER.error("Copying from local buffer to device memory failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        result.releaseInstance();

        return !isError;
    }

    @Override
    public void close() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.freeDeviceMemory(getHandle(), result.getHandle());
        boolean isError = result.isError();
        if(isError) {
            LOGGER.error("Freeing device memory failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        result.releaseInstance();
    }

    @LinkNative("ibv_alloc_dm_attr")
    public static final class AllocationAttributes extends Struct {

        private final NativeLong length = longField("length");
        private final NativeInteger logarithmicAlignmentRequirement = integerField("log_align_req");
        private final NativeInteger compatibilityMask = integerField("comp_mask");

        AllocationAttributes() {}

        public long getLength() {
            return length.get();
        }

        public int getLogarithmicAlignmentRequirement() {
            return logarithmicAlignmentRequirement.get();
        }

        public int getCompatibilityMask() {
            return compatibilityMask.get();
        }

        public void setLength(final long value) {
            length.set(value);
        }

        public void setLogarithmicAlignmentRequirement(final int value) {
            logarithmicAlignmentRequirement.set(value);
        }

        public void setCompatibilityMask(final int value) {
            compatibilityMask.set(value);
        }

        @Override
        public String toString() {
            return "AllocationAttributes {" +
                    "\n\tlength=" + length +
                    ",\n\tlogarithmicAlignmentRequirement=" + logarithmicAlignmentRequirement +
                    ",\n\tcompatibilityMask=" + compatibilityMask +
                    "\n}";
        }

        public static final class Builder {

            private long length;
            private int logarithmicAlignmentRequirement;
            private int compatibilityMask = 0;

            public Builder(final long length, final int logarithmicAlignmentRequirement) {
                this.length = length;
                this.logarithmicAlignmentRequirement = logarithmicAlignmentRequirement;
            }

            public Builder withCompatibilityMask(final int compatibilityMask) {
                this.compatibilityMask = compatibilityMask;
                return this;
            }

            public AllocationAttributes build() {
                var ret = new AllocationAttributes();

                ret.setLength(length);
                ret.setLogarithmicAlignmentRequirement(logarithmicAlignmentRequirement);
                ret.setCompatibilityMask(compatibilityMask);

                return ret;
            }
        }
    }
}
