package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;

@LinkNative("rdma_addr")
public final class Address extends Struct {

    private final NativeLong sourceAddress = longField("src_addr");
    private final NativeLong sourceInternetAddress4 = longField("src_sin");
    private final NativeLong sourceInternetAddress6 = longField("src_sin6");
    private final NativeLong sourceStorage = longField("src_storage");
    private final NativeLong destinationAddress = longField("dst_addr");
    private final NativeLong destinationInternetAddress4 = longField("dst_sin");
    private final NativeLong destinationInternetAddress6 = longField("dst_sin6");
    private final NativeLong destinationStorage = longField("dst_storage");
    private final NativeLong infinibandAddress = longField("ibaddr");

    Address() {}

    Address(long handle) {
        super(handle);
    }

    Address(LocalBuffer buffer, long offset) {
        super(buffer, offset);
    }

    public long getSourceAddress() {
        return sourceAddress.get();
    }

    public long getSourceInternetAddress4() {
        return sourceInternetAddress4.get();
    }

    public long getSourceInternetAddress6() {
        return sourceInternetAddress6.get();
    }

    public long getSourceStorage() {
        return sourceStorage.get();
    }

    public long getDestinationAddress() {
        return destinationAddress.get();
    }

    public long getDestinationInternetAddress4() {
        return destinationInternetAddress4.get();
    }

    public long getDestinationInternetAddress6() {
        return destinationInternetAddress6.get();
    }

    public long getDestinationStorage() {
        return destinationStorage.get();
    }

    public long getInfinibandAddress() {
        return infinibandAddress.get();
    }

    public void setSourceAddress(final long value) {
        sourceAddress.set(value);
    }

    public void setSourceInternetAddress4(final long value) {
        sourceInternetAddress4.set(value);
    }

    public void setSourceInternetAddress6(final long value) {
        sourceInternetAddress6.set(value);
    }

    public void setSourceStorage(final long value) {
        sourceStorage.set(value);
    }

    public void setDestinationAddress(final long value) {
        destinationAddress.set(value);
    }

    public void setDestinationInternetAddress4(final long value) {
        destinationInternetAddress4.set(value);
    }

    public void setDestinationInternetAddress6(final long value) {
        destinationInternetAddress6.set(value);
    }

    public void setDestinationStorage(final long value) {
        destinationStorage.set(value);
    }

    public void setInfinibandAddress(final long value) {
        infinibandAddress.set(value);
    }
}
