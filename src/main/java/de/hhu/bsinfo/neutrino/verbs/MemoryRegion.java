package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LinkNative("ibv_mr")
public class MemoryRegion extends Struct implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryRegion.class);

    private final Context context = referenceField("context");
    private final ProtectionDomain protectionDomain = referenceField("pd");
    private final NativeLong address = longField("addr");
    private final NativeLong length = longField("length");
    private final NativeInteger localKey = integerField("lkey");
    private final NativeInteger remoteKey = integerField("rkey");

    MemoryRegion(long handle) {
        super(handle);
    }

    public Context getContext() {
        return context;
    }

    public ProtectionDomain getProtectionDomain() {
        return protectionDomain;
    }

    public long getAddress() {
        return address.get();
    }

    public long getLength() {
        return length.get();
    }

    public int getLocalKey() {
        return localKey.get();
    }

    public int getRemoteKey() {
        return remoteKey.get();
    }

    @Override
    public void close() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.deregisterMemoryRegion(getHandle(), result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            LOGGER.error("Deregistering memory region failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        result.releaseInstance();
    }

    @Override
    public String toString() {
        return "MemoryRegion {\n" +
            "\taddress=" + address +
            ",\n\tlength=" + length +
            ",\n\tlocalKey=" + localKey +
            ",\n\tremoteKey=" + remoteKey +
            "\n}";
    }
}
