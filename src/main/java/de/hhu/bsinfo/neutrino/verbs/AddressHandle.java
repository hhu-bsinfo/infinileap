package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.struct.field.NativeBoolean;
import de.hhu.bsinfo.neutrino.struct.field.NativeByte;
import de.hhu.bsinfo.neutrino.struct.field.NativeInteger;
import de.hhu.bsinfo.neutrino.struct.field.NativeLong;
import de.hhu.bsinfo.neutrino.struct.field.NativeShort;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.struct.LinkNative;
import de.hhu.bsinfo.neutrino.util.SystemUtil;
import org.agrona.concurrent.AtomicBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@LinkNative("ibv_ah")
public class AddressHandle extends Struct {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressHandle.class);

    private final Context context = referenceField("context");
    private final ProtectionDomain protectionDomain = referenceField("pd");

    AddressHandle(final long handle) {
        super(handle);
    }

    public void destroy() throws IOException {
        var result = Result.localInstance();

        Verbs.destroyAddressHandle(getHandle(), result.getHandle());
        if(result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }
    }

    public Context getContext() {
        return context;
    }

    public ProtectionDomain getProtectionDomain() {
        return protectionDomain;
    }

    @LinkNative("ibv_ah_attr")
    public static final class Attributes extends Struct {

        private final NativeShort remoteLocalId = shortField("dlid");
        private final NativeByte serviceLevel = byteField("sl");
        private final NativeByte sourcePathBits = byteField("src_path_bits");
        private final NativeByte staticRate = byteField("static_rate");
        private final NativeBoolean isGlobal = booleanField("is_global");
        private final NativeByte portNumber = byteField("port_num");

        public final GlobalRoute globalRoute = valueField("grh", GlobalRoute::new);

        Attributes() {}

        Attributes(AtomicBuffer buffer, int offset) {
            super(buffer, offset);
        }

        public short getRemoteLocalId() {
            return remoteLocalId.get();
        }

        public byte getServiceLevel() {
            return serviceLevel.get();
        }

        public byte getSourcePathBits() {
            return sourcePathBits.get();
        }

        public byte getStaticRate() {
            return staticRate.get();
        }

        public boolean getIsGlobal() {
            return isGlobal.get();
        }

        public byte getPortNumber() {
            return portNumber.get();
        }

        public void setRemoteLocalId(final short value) {
            remoteLocalId.set(value);
        }

        public void setServiceLevel(final byte value) {
            serviceLevel.set(value);
        }

        public void setSourcePathBits(final byte value) {
            sourcePathBits.set(value);
        }

        public void setStaticRate(final byte value) {
            staticRate.set(value);
        }

        public void setIsGlobal(final boolean value) {
            isGlobal.set(value);
        }

        public void setPortNumber(final byte value) {
            portNumber.set(value);
        }

        @Override
        public String toString() {
            return "{" +
                "\n\tdestination=" + remoteLocalId +
                ",\n\tserviceLevel=" + serviceLevel +
                ",\n\tsourcePathBits=" + sourcePathBits +
                ",\n\tstaticRate=" + staticRate +
                ",\n\tisGlobal=" + isGlobal +
                ",\n\tportNumber=" + portNumber +
                ",\n\tglobalRoute=" + globalRoute +
                "\n}";
        }

        public static final class Builder {

            private short remoteLocalId;
            private byte serviceLevel;
            private byte sourcePathBits;
            private byte staticRate;
            private boolean isGlobal;
            private byte portNumber;

            // Global Route
            private long remoteGlobalId;
            private int flowLabel;
            private byte index;
            private byte hopLimit;
            private byte trafficClass;

            public Builder(short remoteLocalId, byte remotePortNumber) {
                this.remoteLocalId = remoteLocalId;
                this.portNumber = remotePortNumber;

                // Default values
                serviceLevel = 1;
                sourcePathBits = 0;
                staticRate = 0;
                isGlobal = false;
            }

            public Builder withRemoteLocalId(final short remoteLocalId) {
                this.remoteLocalId = remoteLocalId;
                return this;
            }

            public Builder withServiceLevel(final byte serviceLevel) {
                this.serviceLevel = serviceLevel;
                return this;
            }

            public Builder withSourcePathBits(final byte sourcePathBits) {
                this.sourcePathBits = sourcePathBits;
                return this;
            }

            public Builder withStaticRate(final byte staticRate) {
                this.staticRate = staticRate;
                return this;
            }

            public Builder withIsGlobal(final boolean isGlobal) {
                this.isGlobal = isGlobal;
                return this;
            }

            public Builder withRemotePortNumber(final byte portNumber) {
                this.portNumber = portNumber;
                return this;
            }

            public Builder withRemoteGlobalId(final long remoteGlobalId) {
                this.remoteGlobalId = remoteGlobalId;
                return this;
            }

            public Builder withFlowLabel(final int flowLabel) {
                this.flowLabel = flowLabel;
                return this;
            }

            public Builder withIndex(final byte index) {
                this.index = index;
                return this;
            }

            public Builder withHopLimit(final byte hopLimit) {
                this.hopLimit = hopLimit;
                return this;
            }

            public Builder withTrafficClass(final byte trafficClass) {
                this.trafficClass = trafficClass;
                return this;
            }

            public Attributes build() {
                Attributes ret = new Attributes();

                ret.setRemoteLocalId(remoteLocalId);
                ret.setServiceLevel(serviceLevel);
                ret.setSourcePathBits(sourcePathBits);
                ret.setStaticRate(staticRate);
                ret.setIsGlobal(isGlobal);
                ret.setPortNumber(portNumber);

                ret.globalRoute.setRemoteGlobalId(remoteGlobalId);
                ret.globalRoute.setFlowLabel(flowLabel);
                ret.globalRoute.setIndex(index);
                ret.globalRoute.setHopLimit(hopLimit);
                ret.globalRoute.setTrafficClass(trafficClass);

                return ret;
            }
        }
    }



    @LinkNative("ibv_global_route")
    public static final class GlobalRoute extends Struct {

        private final NativeLong remoteGlobalId = longField("dgid");
        private final NativeInteger flowLabel = integerField("flow_label");
        private final NativeByte index = byteField("sgid_index");
        private final NativeByte hopLimit = byteField("hop_limit");
        private final NativeByte trafficClass = byteField("traffic_class");

        GlobalRoute(AtomicBuffer buffer, int offset) {
            super(buffer, offset);
        }

        public long getRemoteGlobalId() {
            return remoteGlobalId.get();
        }

        public int getFlowLabel() {
            return flowLabel.get();
        }

        public byte getIndex() {
            return index.get();
        }

        public byte getHopLimit() {
            return hopLimit.get();
        }

        public byte getTrafficClass() {
            return trafficClass.get();
        }

        void setRemoteGlobalId(final long value) {
            remoteGlobalId.set(value);
        }

        void setFlowLabel(final int value) {
            flowLabel.set(value);
        }

        void setIndex(final byte value) {
            index.set(value);
        }

        void setHopLimit(final byte value) {
            hopLimit.set(value);
        }

        void setTrafficClass(final byte value) {
            trafficClass.set(value);
        }

        @Override
        public String toString() {
            return "{" +
                "\n\tdestination=" + remoteGlobalId +
                ",\n\tflowLabel=" + flowLabel +
                ",\n\tindex=" + index +
                ",\n\thopLimit=" + hopLimit +
                ",\n\ttrafficClass=" + trafficClass +
                "\n}";
        }
    }
}
