package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.struct.Result;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Context implements NativeObject {

    static {
        System.loadLibrary("neutrino");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Context.class);

    private final long handle;

    @SuppressWarnings("FieldNamingConvention")
    private static final long nullptr = 0L;

    Context(long handle) {
        this.handle = handle;
    }

    @Override
    public long getHandle() {
        return handle;
    }

    @Nullable
    public static Context openDevice(int index) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.openDevice(index, result.getHandle());
        if (result.isError()) {
            LOGGER.error("Could not open device with index {}", index);
            return null;
        }

        result.releaseInstance();
        return result.get(Context::new);
    }

    public boolean close() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.closeDevice(handle, result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            LOGGER.error("Could not close device [{}]", result.getStatus());
        }

        result.releaseInstance();

        return !isError;
    }

    public String getDeviceName() {
        return Verbs.getDeviceName(handle);
    }

    @Nullable
    public Device queryDevice() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);
        var device = new Device();

        Verbs.queryDevice(handle, device.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Could not query device [{}]", result.getStatus());
            device = null;
        }

        result.releaseInstance();
        return device;
    }

    @Nullable
    public Port queryPort(int portNumber) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);
        var port = new Port();

        Verbs.queryPort(handle, port.getHandle(), portNumber, result.getHandle());
        if (result.isError()) {
            LOGGER.error("Could not query port [{}]", result.getStatus());
            port = null;
        }

        result.releaseInstance();
        return port;
    }

    @Nullable
    public ProtectionDomain allocateProtectionDomain() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.allocateProtectionDomain(handle, result.getHandle());
        if(result.isError()) {
            LOGGER.error("Could not allocate protection domain");
        }

        return result.getAndRelease(ProtectionDomain::new);
    }

    @Nullable
    public CompletionQueue createCompletionQueue(int numElements) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.createCompletionQueue(handle, numElements, nullptr, nullptr, 0, result.getHandle());
        if (result.isError()) {
            LOGGER.error("Could not create completion queue [error={}]", result.getStatus());
        }

        return result.getAndRelease(CompletionQueue::new);
    }
}
