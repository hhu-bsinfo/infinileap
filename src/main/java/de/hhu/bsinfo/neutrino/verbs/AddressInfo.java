package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.NativeEnum;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeIntegerBitMask;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.NativeError;
import de.hhu.bsinfo.neutrino.util.SystemUtil;
import de.hhu.bsinfo.neutrino.util.flag.IntegerFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

@LinkNative("rdma_addrinfo")
public final class AddressInfo extends Struct implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressInfo.class);

    private final NativeIntegerBitMask<Flag> flags = integerBitField("ai_flags");
    private final NativeInteger family = integerField("ai_family");
    private final NativeEnum<QueuePair.Type> queuePairType = enumField("ai_qp_type", QueuePair.Type.CONVERTER);
    private final NativeEnum<PortSpace> portSpace = enumField("ai_port_space", PortSpace.CONVERTER);
    private final NativeLong sourceLength = longField("ai_src_len");
    private final NativeLong destinationLength = longField("ai_dst_len");
    private final NativeLong sourceAddress = longField("ai_src_addr");
    private final NativeLong destinationAddress = longField("ai_dst_addr");
    private final NativeLong sourceCanonicalName = longField("ai_src_canonname");
    private final NativeLong destinationCanonicalName = longField("ai_dst_canonname");
    private final NativeLong routeLength = longField("ai_route_len");
    private final NativeLong route = longField("ai_route");
    private final NativeLong connectionLength = longField("ai_connect_len");
    private final NativeLong connection = longField("ai_connect");
    private final AddressInfo next = referenceField("ai_next", AddressInfo::new);

    AddressInfo() {}

    AddressInfo(long handle) {
        super(handle);
    }

    AddressInfo(LocalBuffer buffer, long offset) {
        super(buffer, offset);
    }

    public int getFlags() {
        return flags.get();
    }

    public int getFamily() {
        return family.get();
    }

    public QueuePair.Type getQueuePairType() {
        return queuePairType.get();
    }

    public PortSpace getPortSpace() {
        return portSpace.get();
    }

    public long getSourceLength() {
        return sourceLength.get();
    }

    public long getDestinationLength() {
        return destinationLength.get();
    }

    public long getSourceAddress() {
        return sourceAddress.get();
    }

    public long getDestinationAddress() {
        return destinationAddress.get();
    }

    public long getSourceCanonicalName() {
        return sourceCanonicalName.get();
    }

    public long getDestinationCanonicalName() {
        return destinationCanonicalName.get();
    }

    public long getRouteLength() {
        return routeLength.get();
    }

    public long getRoute() {
        return route.get();
    }

    public long getConnectionLength() {
        return connectionLength.get();
    }

    public long getConnection() {
        return connection.get();
    }

    public AddressInfo getNext() {
        return next;
    }

    public void setFlags(final Flag... value) {
        flags.set(value);
    }

    public void setFamily(final int value) {
        family.set(value);
    }

    public void setQueuePairType(final QueuePair.Type value) {
        queuePairType.set(value);
    }

    public void setPortSpace(final PortSpace value) {
        portSpace.set(value);
    }

    public void setSourceLength(final long value) {
        sourceLength.set(value);
    }

    public void setDestinationLength(final long value) {
        destinationLength.set(value);
    }

    public void setSourceAddress(final long value) {
        sourceAddress.set(value);
    }

    public void setDestinationAddress(final long value) {
        destinationAddress.set(value);
    }

    public void setSourceCanonicalName(final long value) {
        sourceCanonicalName.set(value);
    }

    public void setDestinationCanonicalName(final long value) {
        destinationCanonicalName.set(value);
    }

    public void setRouteLength(final long value) {
        routeLength.set(value);
    }

    public void setRoute(final long value) {
        route.set(value);
    }

    public void setConnectionLength(final long value) {
        connectionLength.set(value);
    }

    public void setConnection(final long value) {
        connection.set(value);
    }

    @Override
    public void close() {
        var result = Result.localInstance();

        CommunicationManager.freeAddressInfo0(getHandle(), result.getHandle());
        if (result.isError()) {
            throw new NativeError(SystemUtil.getErrorMessage());
        }
    }

    public enum Flag implements IntegerFlag {
        PASSIVE(0x00000001), NUMERICHOST(0x00000002), NOROUTE(0x00000004), FAMILY(0x00000008);

        private final int value;

        Flag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }
}