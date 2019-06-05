package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.NativeBoolean;
import de.hhu.bsinfo.neutrino.data.NativeByte;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.data.NativeShort;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LinkNative("ibv_ah")
public class AddressHandle extends Struct {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressHandle.class);

    private final Context context = referenceField("context", Context::new);
    private final ProtectionDomain protectionDomain = referenceField("pd", ProtectionDomain::new);

    AddressHandle(final long handle) {
        super(handle);
    }

    public boolean destroy() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.destroyAddressHandle(getHandle(), result.getHandle());
        boolean isError = result.isError();
        if(isError) {
            LOGGER.error("Destroying address handle failed with error [{}]", result.getStatus());
        }

        result.releaseInstance();

        return !isError;
    }

    public Context getContext() {
        return context;
    }

    public ProtectionDomain getProtectionDomain() {
        return protectionDomain;
    }

    @LinkNative("ibv_ah_attr")
    public static final class Attributes extends Struct {

        private final NativeShort destination = shortField("dlid");
        private final NativeByte serviceLevel = byteField("sl");
        private final NativeByte sourcePathBits = byteField("src_path_bits");
        private final NativeByte staticRate = byteField("static_rate");
        private final NativeBoolean isGlobal = booleanField("is_global");
        private final NativeByte portNumber = byteField("port_num");

        public final GlobalRoute globalRoute = valueField("grh", GlobalRoute::new);

        Attributes(LocalBuffer byteBuffer, int offset) {
            super(byteBuffer, offset);
        }

        public Attributes() {}

        public Attributes(final Consumer<Attributes> configurator) {
            configurator.accept(this);
        }

        public short getDestination() {
            return destination.get();
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

        public void setDestination(final short value) {
            destination.set(value);
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
                "\n\tdestination=" + destination +
                ",\n\tserviceLevel=" + serviceLevel +
                ",\n\tsourcePathBits=" + sourcePathBits +
                ",\n\tstaticRate=" + staticRate +
                ",\n\tisGlobal=" + isGlobal +
                ",\n\tportNumber=" + portNumber +
                ",\n\tglobalRoute=" + globalRoute +
                "\n}";
        }
    }



    @LinkNative("ibv_global_route")
    public static final class GlobalRoute extends Struct {

        private final NativeLong destination = longField("dgid");
        private final NativeInteger flowLabel = integerField("flow_label");
        private final NativeByte index = byteField("sgid_index");
        private final NativeByte hopLimit = byteField("hop_limit");
        private final NativeByte trafficClass = byteField("traffic_class");

        GlobalRoute(LocalBuffer byteBuffer, int offset) {
            super(byteBuffer, offset);
        }

        public GlobalRoute() {}

        public GlobalRoute(final Consumer<GlobalRoute> configurator) {
            configurator.accept(this);
        }

        public long getDestination() {
            return destination.get();
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

        public void setDestination(final long value) {
            destination.set(value);
        }

        public void setFlowLabel(final int value) {
            flowLabel.set(value);
        }

        public void setIndex(final byte value) {
            index.set(value);
        }

        public void setHopLimit(final byte value) {
            hopLimit.set(value);
        }

        public void setTrafficClass(final byte value) {
            trafficClass.set(value);
        }

        @Override
        public String toString() {
            return "{" +
                "\n\tdestination=" + destination +
                ",\n\tflowLabel=" + flowLabel +
                ",\n\tindex=" + index +
                ",\n\thopLimit=" + hopLimit +
                ",\n\ttrafficClass=" + trafficClass +
                "\n}";
        }
    }
}
