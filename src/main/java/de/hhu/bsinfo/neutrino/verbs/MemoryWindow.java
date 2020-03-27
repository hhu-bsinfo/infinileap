package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.EnumConverter;
import de.hhu.bsinfo.neutrino.data.NativeIntegerBitMask;
import de.hhu.bsinfo.neutrino.data.NativeEnum;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.NativeObjectRegistry;
import de.hhu.bsinfo.neutrino.verbs.SendWorkRequest.SendFlag;
import java.util.Arrays;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LinkNative("ibv_mw")
public class MemoryWindow extends Struct implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryWindow.class);

    private final Context context = referenceField("context");
    private final ProtectionDomain protectionDomain = referenceField("pd");
    private final NativeInteger remoteKey = integerField("rkey");
    private final NativeEnum<Type> type = enumField("type", Type.CONVERTER);

    MemoryWindow(final long handle) {
        super(handle);
    }

    public Context getContext() {
        return context;
    }

    public ProtectionDomain getProtectionDomain() {
        return protectionDomain;
    }

    public int getRemoteKey() {
        return remoteKey.get();
    }

    public Type getType() {
        return type.get();
    }

    public boolean bind(QueuePair queuePair, BindAttributes attributes) {
        var result = Result.localInstance();

        Verbs.bindMemoryWindow(getHandle(), queuePair.getHandle(), attributes.getHandle(), result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            LOGGER.error("Binding memory window failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }



        return !isError;
    }

    @Override
    public void close() {
        var result = Result.localInstance();

        Verbs.deallocateMemoryWindow(getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Deallocating memory window failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        } else {
            NativeObjectRegistry.deregisterObject(this);
        }


    }

    @Override
    public String toString() {
        return "MemoryWindow {" +
            ",\n\tremoteKey=" + remoteKey +
            ",\n\ttype=" + type +
            "\n}";
    }

    public enum Type {
        TYPE_1(1), TYPE_2(2);

        private static final Type[] VALUES;

        static {
            int arrayLength = Arrays.stream(values()).mapToInt(element -> element.value).max().orElseThrow() + 1;

            VALUES = new Type[arrayLength];

            for (Type element : Type.values()) {
                VALUES[element.value] = element;
            }
        }

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static final EnumConverter<Type> CONVERTER = new EnumConverter<>() {

            @Override
            public int toInt(Type enumeration) {
                return enumeration.value;
            }

            @Override
            public Type toEnum(int integer) {
                if (integer < TYPE_1.value || integer > TYPE_2.value) {
                    throw new IllegalArgumentException(String.format("Unknown operation code provided %d", integer));
                }

                return VALUES[integer];
            }
        };
    }

    @LinkNative("ibv_mw_bind")
    public static final class BindAttributes extends Struct {

        private final NativeLong workRequestId = longField("wr_id");
        private final NativeIntegerBitMask<SendFlag> sendFlags = integerBitField("send_flags");

        public final BindInformation bindInfo = valueField("bind_info", BindInformation::new);

        public BindAttributes() {}

        public BindAttributes(final Consumer<BindAttributes> configurator) {
            configurator.accept(this);
        }

        public long getWorkRequestId() {
            return workRequestId.get();
        }

        public int getSendFlags() {
            return sendFlags.get();
        }

        void setWorkRequestId(long value) {
            workRequestId.set(value);
        }

        void setSendFlags(SendFlag... flags) {
            sendFlags.set(flags);
        }

        @Override
        public String toString() {
            return "BindAttributes {" +
                "\n\tworkRequestId=" + workRequestId +
                ",\n\tsendFlags=" + sendFlags +
                ",\n\tbindInfo=" + bindInfo +
                "\n}";
        }

        public static final class Builder {

            private int workRequestId;
            private SendFlag[] sendFlags;

            // Bind Information
            private MemoryRegion memoryRegion;
            private long address;
            private long length;
            private AccessFlag[] accessFlags;

            public Builder(final MemoryRegion memoryRegion, final long address, final long length, final AccessFlag... accessFlags) {
                this.memoryRegion = memoryRegion;
                this.address = address;
                this.length = length;
                this.accessFlags = accessFlags;
            }

            public Builder withWorkRequestId(final int workRequestId) {
                this.workRequestId = workRequestId;
                return this;
            }

            public Builder withSendFlags(final SendFlag... flags) {
                sendFlags = flags;
                return this;
            }

            public BindAttributes build() {
                var ret = new BindAttributes();

                ret.setWorkRequestId(workRequestId);
                if(sendFlags != null) ret.setSendFlags(sendFlags);

                ret.bindInfo.setLength(length);
                ret.bindInfo.setAddress(address);
                if(accessFlags != null) ret.bindInfo.setAccessFlags(accessFlags);
                if(memoryRegion != null) ret.bindInfo.setMemoryRegion(memoryRegion);

                return ret;
            }
        }
    }

    @LinkNative("ibv_mw_bind_info")
    public static final class BindInformation extends Struct {

        private final NativeLong memoryRegion = longField("mr");
        private final NativeLong address = longField("addr");
        private final NativeLong length = longField("length");
        private final NativeIntegerBitMask<AccessFlag> accessFlags = integerBitField("mw_access_flags");

        BindInformation(final LocalBuffer buffer, final long offset) {
            super(buffer, offset);
        }

        public MemoryRegion getMemoryRegion() {
            return NativeObjectRegistry.getObject(memoryRegion.get());
        }

        public long getAddress() {
            return address.get();
        }

        public long getLength() {
            return length.get();
        }

        public int getAccessFlags() {
            return accessFlags.get();
        }

        void setMemoryRegion(final MemoryRegion region) {
            memoryRegion.set(region.getHandle());
        }

        void setAddress(final long value) {
            address.set(value);
        }

        void setLength(final long value) {
            length.set(value);
        }

        void setAccessFlags(final AccessFlag... flags) {
            accessFlags.set(flags);
        }

        @Override
        public String toString() {
            return "BindInformation {" +
                "\n\tmemoryRegion=" + memoryRegion +
                ",\n\taddress=" + address +
                ",\n\tlength=" + length +
                ",\n\taccessFlags=" + accessFlags +
                "\n}";
        }
    }
}
