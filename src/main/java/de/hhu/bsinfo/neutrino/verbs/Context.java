package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.util.NativeObjectRegistry;
import de.hhu.bsinfo.neutrino.verbs.DeviceMemory.AllocationAttributes;
import de.hhu.bsinfo.neutrino.verbs.ExtendedDeviceAttributes.QueryExtendedDeviceInput;
import de.hhu.bsinfo.neutrino.verbs.ProtectionDomain.InitialAttributes;
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

    public static int getDeviceCount() {
        return Verbs.getNumDevices();
    }

    @Nullable
    public static Context openDevice() {
        return openDevice(0);
    }

    @Nullable
    public static Context openDevice(int index) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.openDevice(index, result.getHandle());
        if (result.isError()) {
            LOGGER.error("Opening device {} failed with error [{}]: {}", index, result.getStatus(), result.getStatusMessage());
        }

        var context = result.getAndRelease(Context::new);
        NativeObjectRegistry.registerObject(context);

        return context;
    }

    @Override
    public void close() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.closeDevice(getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Closing device failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        } else {
            NativeObjectRegistry.deregisterObject(this);
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
            LOGGER.error("Querying device failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
            device = null;
        }

        result.releaseInstance();

        return device;
    }

    @Nullable
    public Port queryPort() {
        return queryPort(1);
    }

    @Nullable
    public PortAttributes queryPort(int portNumber) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);
        var port = new PortAttributes();

        Verbs.queryPort(getHandle(), port.getHandle(), portNumber, result.getHandle());
        if (result.isError()) {
            LOGGER.error("Querying port failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
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
            LOGGER.error("Allocating device memory failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var deviceMemory = result.getAndRelease(DeviceMemory::new);
        NativeObjectRegistry.registerObject(deviceMemory);

        return deviceMemory;
    }

    @Nullable
    public ProtectionDomain allocateProtectionDomain() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.allocateProtectionDomain(getHandle(), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Allocating protection domain failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var protectionDomain = result.getAndRelease(ProtectionDomain::new);
        NativeObjectRegistry.registerObject(protectionDomain);

        return protectionDomain;
    }

    @Nullable
    public ThreadDomain allocateThreadDomain(ThreadDomain.InitialAttributes attributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.allocateThreadDomain(getHandle(), attributes.getHandle(), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Allocating thread domain failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var threadDomain = result.getAndRelease(ThreadDomain::new);
        NativeObjectRegistry.registerObject(threadDomain);

        return threadDomain;
    }

    @Nullable ProtectionDomain allocateParentDomain(InitialAttributes attributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.allocateParentDomain(getHandle(), attributes.getHandle(), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Allocating parent domain failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var protectionDomain = result.getAndRelease(ProtectionDomain::new);
        NativeObjectRegistry.registerObject(protectionDomain);

        return protectionDomain;
    }

    @Nullable
    public CompletionChannel createCompletionChannel() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.createCompletionChannel(getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating completion channel failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var completionChannel = result.getAndRelease(CompletionChannel::new);
        NativeObjectRegistry.registerObject(completionChannel);

        return completionChannel;
    }

    @Nullable
    public CompletionQueue createCompletionQueue(int numElements) {
        return createCompletionQueue(numElements, null);
    }

    @Nullable
    public CompletionQueue createCompletionQueue(int numElements, @Nullable CompletionChannel channel) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.createCompletionQueue(getHandle(), numElements, nullptr, channel == null ? nullptr : channel.getHandle(), 0, result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating completion queue failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var completionQueue = result.getAndRelease(CompletionQueue::new);
        NativeObjectRegistry.registerObject(completionQueue);

        return completionQueue;
    }

    @Nullable
    public AsyncEvent getAsyncEvent() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);
        var event = (AsyncEvent) Verbs.getPoolableInstance(AsyncEvent.class);

        Verbs.getAsyncEvent(getHandle(), event.getHandle(), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Polling async event failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());

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
            LOGGER.error("Querying extended device failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
            device = null;
        }

        result.releaseInstance();

        return device;
    }

    @Nullable
    public ExtendedConnectionDomain openExtendedConnectionDomain(ExtendedConnectionDomain.InitialAttributes attributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.openExtendedConnectionDomain(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Opening extended connection domain failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var extendedConnectionDomain = result.getAndRelease(ExtendedConnectionDomain::new);
        NativeObjectRegistry.registerObject(extendedConnectionDomain);

        return extendedConnectionDomain;
    }

    @Nullable
    public SharedReceiveQueue createExtendedSharedReceiveQueue(SharedReceiveQueue.ExtendedInitialAttributes attributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.createExtendedSharedReceiveQueue(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating extended shared receive queue failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var sharedReceiveQueue = result.getAndRelease(SharedReceiveQueue::new);
        NativeObjectRegistry.registerObject(sharedReceiveQueue);

        return sharedReceiveQueue;
    }

    @Nullable
    public ExtendedCompletionQueue createExtendedCompletionQueue(ExtendedCompletionQueue.InitialAttributes attributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.createExtendedCompletionQueue(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating extended completion queue failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var extendedCompletionQueue = result.getAndRelease(ExtendedCompletionQueue::new);
        NativeObjectRegistry.registerObject(extendedCompletionQueue);

        return extendedCompletionQueue;
    }

    @Nullable
    public WorkQueue createWorkQueue(WorkQueue.InitialAttributes attributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.createWorkQueue(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating work queue failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var workQueue = result.getAndRelease(WorkQueue::new);
        NativeObjectRegistry.registerObject(workQueue);

        return workQueue;
    }

    @Nullable
    public ReceiveWorkQueueIndirectionTable createReceiveWorkQueueIndirectionTable(ReceiveWorkQueueIndirectionTable.InitialAttributes attributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.createReceiveWorkQueueIndirectionTable(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating receive work queue indirection table failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var indirectionTable = result.getAndRelease(ReceiveWorkQueueIndirectionTable::new);
        NativeObjectRegistry.registerObject(indirectionTable);

        return indirectionTable;
    }

    @Nullable
    public QueuePair createExtendedQueuePair(ExtendedQueuePair.InitialAttributes attributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.createExtendedQueuePair(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating extended queue pair failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var queuePair = result.getAndRelease(QueuePair::new);
        NativeObjectRegistry.registerObject(queuePair);

        return queuePair;
    }

    @Nullable
    public QueuePair openQueuePair(QueuePair.OpenAttributes attributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.openQueuePair(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Opening queue pair failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var queuePair = result.getAndRelease(QueuePair::new);
        NativeObjectRegistry.registerObject(queuePair);

        return queuePair;
    }
}
