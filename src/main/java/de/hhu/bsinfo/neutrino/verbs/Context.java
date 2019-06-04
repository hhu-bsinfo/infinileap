package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.verbs.DeviceMemory.AllocationAttributes;
import de.hhu.bsinfo.neutrino.verbs.ExtendedDeviceAttributes.QueryExtendedDeviceInput;
import de.hhu.bsinfo.neutrino.verbs.ProtectionDomain.ParentDomainInitialAttributes;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Context implements NativeObject, AutoCloseable {

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

    @Override
    public long getNativeSize() {
        return -1;
    }

    @Nullable
    public static Context openDevice(int index) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.openDevice(index, result.getHandle());
        if (result.isError()) {
            LOGGER.error("Opening deviceAttributes {} failed with error [{}]", index, result.getStatus());
        }

        return result.getAndRelease(Context::new);
    }

    @Override
    public void close() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.closeDevice(getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Closing deviceAttributes failed with error [{}]", result.getStatus());
        }

        result.releaseInstance();
    }

    public String getDeviceName() {
        return Verbs.getDeviceName(getHandle());
    }

    @Nullable
    public DeviceAttributes queryDevice() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);
        var device = new DeviceAttributes();

        Verbs.queryDevice(getHandle(), device.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Querying deviceAttributes failed with error [{}]", result.getStatus());
            device = null;
        }

        result.releaseInstance();

        return device;
    }

    @Nullable
    public Port queryPort(int portNumber) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);
        var port = new Port();

        Verbs.queryPort(getHandle(), port.getHandle(), portNumber, result.getHandle());
        if (result.isError()) {
            LOGGER.error("Querying port failed with error [{}]", result.getStatus());
            port = null;
        }

        result.releaseInstance();

        return port;
    }

    @Nullable
    DeviceMemory allocateDeviceMemory(AllocationAttributes attributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.allocateDeviceMemory(getHandle(), attributes.getHandle(), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Allocating device memory failed with error [{}]", result.getStatus());
        }

        return result.getAndRelease(DeviceMemory::new);
    }

    @Nullable
    public ProtectionDomain allocateProtectionDomain() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.allocateProtectionDomain(getHandle(), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Allocating protection domain failed with error [{}]", result.getStatus());
        }

        return result.getAndRelease(ProtectionDomain::new);
    }

    @Nullable
    public ThreadDomain allocateThreadDomain(ThreadDomain.InitialAttributes attributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.allocateThreadDomain(getHandle(), attributes.getHandle(), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Allocating thread domain failed with error [{}]", result.getStatus());
        }

        return result.getAndRelease(ThreadDomain::new);
    }

    @Nullable
    public ProtectionDomain allocateParentDomain(ParentDomainInitialAttributes attributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.allocateParentDomain(getHandle(), attributes.getHandle(), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Allocating parent domain failed with error [{}]", result.getStatus());
        }

        return result.getAndRelease(ProtectionDomain::new);
    }

    @Nullable
    public CompletionChannel createCompletionChannel() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.createCompletionChannel(getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating completion channel failed with error [{}]", result.getStatus());
        }

        return result.getAndRelease(CompletionChannel::new);
    }

    @Nullable
    public CompletionQueue createCompletionQueue(int numElements, @Nullable CompletionChannel channel) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.createCompletionQueue(getHandle(), numElements, nullptr, channel == null ? nullptr : channel.getHandle(), 0, result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating completion queue failed with error [{}]", result.getStatus());
        }

        return result.getAndRelease(CompletionQueue::new);
    }

    @Nullable
    public AsyncEvent getAsyncEvent() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);
        var event = (AsyncEvent) Verbs.getPoolableInstance(AsyncEvent.class);

        Verbs.getAsyncEvent(getHandle(), event.getHandle(), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Polling async event failed with error [{}]", result.getStatus());

            result.releaseInstance();
            event.releaseInstance();

            return null;
        }

        result.releaseInstance();

        return event;
    }

    @Nullable
    public ExtendedDeviceAttributes queryExtendedDevice(QueryExtendedDeviceInput queryInput) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);
        var device = new ExtendedDeviceAttributes();

        Verbs.queryExtendedDevice(getHandle(), device.getHandle(), queryInput.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Querying extended deviceAttributes failed with error [{}]", result.getStatus());
            device = null;
        }

        result.releaseInstance();

        return device;
    }

    @Nullable
    public SharedReceiveQueue createExtendedSharedReceiveQueue(SharedReceiveQueue.ExtendedInitialAttributes attributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.createExtendedSharedReceiveQueue(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating extended shared receive queue failed with error [{}]", result.getStatus());
        }

        return result.getAndRelease(SharedReceiveQueue::new);
    }

    @Nullable
    public ExtendedCompletionQueue createExtendedCompletionQueue(ExtendedCompletionQueue.InitialAttributes attributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.createExtendedCompletionQueue(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating extended completion queue failed with error [{}]", result.getStatus());
        }

        return result.getAndRelease(ExtendedCompletionQueue::new);
    }
}
