package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeBitMask;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.Flag;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LinkNative("ibv_xrcd")
public class ExtendedConnectionDomain extends Struct implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedConnectionDomain.class);

    private final Context context = referenceField("context", Context::new);

    public ExtendedConnectionDomain(final long handle) {
        super(handle);
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void close() throws Exception {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.closeExtendedConnectionDomain(getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Destroying completion queue failed with error [{}]", result.getStatus());
        }

        result.releaseInstance();
    }

    public enum AttributeFlag implements Flag {
        FD(1), OFLAGS(1 << 1), RESERVED(1 << 2);

        private final int value;

        AttributeFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    public enum OperationFlag implements Flag {
        O_CREAT(Verbs.getOperationFlagCreate()),
        O_EXCL(Verbs.getOperationFlagExclusive());

        private final int value;

        OperationFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    @LinkNative("ibv_xrcd_init_attr")
    public static final class InitalAttributes extends Struct {

        private final NativeBitMask<AttributeFlag> attributesMask = bitField("comp_mask");
        private final NativeInteger fileDescriptor = integerField("fd");
        private final NativeBitMask<OperationFlag> operationFlags = bitField("oflags");

        public InitalAttributes() {}

        public InitalAttributes(final Consumer<InitalAttributes> configurator) {
            configurator.accept(this);
        }

        public int getAttributesMask() {
            return attributesMask.get();
        }

        public int getFileDescriptor() {
            return fileDescriptor.get();
        }

        public int getOperationFlags() {
            return operationFlags.get();
        }

        public void setAttributesMask(final AttributeFlag... flags) {
            attributesMask.set(flags);
        }

        public void setFileDescriptor(final int value) {
            fileDescriptor.set(value);
        }

        public void setOperationFlags(final OperationFlag... flags) {
            operationFlags.set(flags);
        }

        @Override
        public String toString() {
            return "InitalAttributes {" +
                "\n\tattributesMask=" + attributesMask +
                ",\n\tfileDescriptor=" + fileDescriptor +
                ",\n\toperationFlags=" + operationFlags +
                "\n}";
        }
    }
}
