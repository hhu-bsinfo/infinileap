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

    private Context(long handle) {
        this.handle = handle;
    }

    @Override
    public long getHandle() {
        return handle;
    }

    @Nullable
    public static Context openDevice(int index) {
        var result = (Result) Verbs.getNativeObjectPool(Result.class).newInstance();

        Verbs.openDevice(index, result.getHandle());
        if (result.isError()) {
            LOGGER.error("Could not open device with index {}", index);
            return null;
        }

        result.free();
        return result.get(Context::new);
    }

    public boolean close() {
        var result = (Result) Verbs.getNativeObjectPool(Result.class).newInstance();

        Verbs.closeDevice(handle, result.getHandle());
        if(result.isError()) {
            LOGGER.error("Could not close device");
            return false;
        }

        result.free();
        return true;
    }

    public String getDeviceName() {
        return Verbs.getDeviceName(handle);
    }

    @Nullable
    public Device queryDevice() {
        var result = (Result) Verbs.getNativeObjectPool(Result.class).newInstance();
        var device = new Device();

        Verbs.queryDevice(handle, device.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Could not query device");
            return null;
        }

        result.free();
        return device;
    }

    @Nullable
    public Port queryPort(int portNumber) {
        var result = (Result) Verbs.getNativeObjectPool(Result.class).newInstance();
        var port = new Port();

        Verbs.queryPort(handle, port.getHandle(), portNumber, result.getHandle());
        if (result.isError()) {
            LOGGER.error("Could not query port");
            return null;
        }

        result.free();
        return port;
    }

    @Nullable
    public ProtectionDomain allocateProtectionDomain() {
        var result = (Result) Verbs.getNativeObjectPool(Result.class).newInstance();

        Verbs.allocateProtectionDomain(handle, result.getHandle());
        if(result.isError()) {
            LOGGER.error("Could not allocate protection domain");
            return null;
        }

        result.free();
        return result.get(ProtectionDomain::new);
    }

    @Nullable
    public CompletionQueue createCompletionQueue(int numElements) {
        var result = (Result) Verbs.getNativeObjectPool(Result.class).newInstance();

        Verbs.createCompletionQueue(handle, numElements, nullptr, nullptr, 0, result.getHandle());
        if (result.isError()) {
            LOGGER.error("Could not create completion queue [error={}]", result.getStatus());
            return null;
        }

        result.free();
        return result.get(CompletionQueue::new);
    }
}
