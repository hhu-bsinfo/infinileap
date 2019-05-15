package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.struct.Result;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Context {

    static {
        System.loadLibrary("neutrino");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Context.class);

    private final long handle;

    private Context(long handle) {
        this.handle = handle;
    }

    @Nullable
    public static Context openDevice(int index) {
        var result = new Result();
        Verbs.openDevice(index, result.getHandle());
        if (result.isError()) {
            LOGGER.error("Could not open device with index {}", index);
            return null;
        }

        return new Context(result.getResultHandle());
    }

    public boolean close() {
        var result = new Result();
        Verbs.closeDevice(handle, result.getHandle());

        if(result.isError()) {
            LOGGER.error("Could not close device");
            return false;
        }

        return true;
    }

    public String getDeviceName() {
        return Verbs.getDeviceName(handle);
    }

    @Nullable
    public Device queryDevice() {
        var result = new Result();
        var device = new Device();
        Verbs.queryDevice(handle, device.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Could not query device");
            return null;
        }

        return device;
    }

    @Nullable
    public Port queryPort(int portNumber) {
        var result = new Result();
        var port = new Port();
        Verbs.queryPort(handle, port.getHandle(), portNumber, result.getHandle());
        if (result.isError()) {
            LOGGER.error("Could not query port");
            return null;
        }

        return port;
    }

    @Nullable
    public ProtectionDomain allocateProtectionDomain() {
        var result = new Result();
        Verbs.allocateProtectionDomain(handle, result.getHandle());
        if(result.isError()) {
            LOGGER.error("Could not allocate protection domain");
            return null;
        }

        return new ProtectionDomain(result.getResultHandle());
    }
}
