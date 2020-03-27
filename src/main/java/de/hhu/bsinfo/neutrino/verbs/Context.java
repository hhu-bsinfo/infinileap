package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.util.NativeError;
import de.hhu.bsinfo.neutrino.util.NativeLibrary;
import de.hhu.bsinfo.neutrino.util.NativeObjectRegistry;
import de.hhu.bsinfo.neutrino.util.SystemUtil;
import de.hhu.bsinfo.neutrino.verbs.DeviceMemory.AllocationAttributes;
import de.hhu.bsinfo.neutrino.verbs.ExtendedDeviceAttributes.QueryExtendedDeviceInput;
import de.hhu.bsinfo.neutrino.verbs.ProtectionDomain.InitialAttributes;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class Context implements NativeObject, AutoCloseable {

    static {
        NativeLibrary.load("neutrino");
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
        var result = Result.localInstance();

        Verbs.openDevice(index, result.getHandle());
        if (result.isError()) {
            LOGGER.error("Opening device {} failed with error [{}]: {}", index, result.getStatus(), result.getStatusMessage());
        }

        var context = result.get(Context::new);
        NativeObjectRegistry.registerObject(context);

        return context;
    }

    @Override
    public void close() {
        var result = Result.localInstance();

        Verbs.closeDevice(getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Closing device failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        } else {
            NativeObjectRegistry.deregisterObject(this);
        }


    }

    public String getDeviceName() {
        return Verbs.getDeviceName(getHandle());
    }

    @Nullable
    public DeviceAttributes queryDevice() {
        var result = Result.localInstance();
        var device = new DeviceAttributes();

        Verbs.queryDevice(getHandle(), device.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Querying device failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
            device = null;
        }



        return device;
    }

    @Nullable
    public PortAttributes queryPort() {
        return queryPort(1);
    }

    @Nullable
    public PortAttributes queryPort(int portNumber) {
        var result = Result.localInstance();
        var port = new PortAttributes();

        Verbs.queryPort(getHandle(), port.getHandle(), portNumber, result.getHandle());
        if (result.isError()) {
            LOGGER.error("Querying port failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
            port = null;
        }



        return port;
    }

    @Nullable
    DeviceMemory allocateDeviceMemory(AllocationAttributes attributes) {
        var result = Result.localInstance();

        Verbs.allocateDeviceMemory(getHandle(), attributes.getHandle(), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Allocating device memory failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var deviceMemory = result.get(DeviceMemory::new);
        NativeObjectRegistry.registerObject(deviceMemory);

        return deviceMemory;
    }

    @Nullable
    public ProtectionDomain allocateProtectionDomain() {
        var result = Result.localInstance();

        Verbs.allocateProtectionDomain(getHandle(), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Allocating protection domain failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var protectionDomain = result.get(ProtectionDomain::new);
        NativeObjectRegistry.registerObject(protectionDomain);

        return protectionDomain;
    }

    @Nullable
    public ThreadDomain allocateThreadDomain(ThreadDomain.InitialAttributes attributes) {
        var result = Result.localInstance();

        Verbs.allocateThreadDomain(getHandle(), attributes.getHandle(), result.getHandle());
        if(result.isError()) {
            return null;
        }

        var threadDomain = result.get(ThreadDomain::new);
        NativeObjectRegistry.registerObject(threadDomain);

        return threadDomain;
    }

    @Nullable ProtectionDomain allocateParentDomain(InitialAttributes attributes) {
        var result = Result.localInstance();

        Verbs.allocateParentDomain(getHandle(), attributes.getHandle(), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Allocating parent domain failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var protectionDomain = result.get(ProtectionDomain::new);
        NativeObjectRegistry.registerObject(protectionDomain);

        return protectionDomain;
    }

    @Nullable
    public CompletionChannel createCompletionChannel() {
        var result = Result.localInstance();

        Verbs.createCompletionChannel(getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating completion channel failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
            throw new NativeError(SystemUtil.getErrorMessage());
        }

        var completionChannel = result.get(CompletionChannel::new);
        NativeObjectRegistry.registerObject(completionChannel);

        return completionChannel;
    }

    public CompletionQueue createCompletionQueue(int numElements) {
        return createCompletionQueue(numElements, null);
    }

    public CompletionQueue createCompletionQueue(int numElements, @Nullable CompletionChannel channel) {
        var result = Result.localInstance();

        Verbs.createCompletionQueue(getHandle(), numElements, nullptr, channel == null ? nullptr : channel.getHandle(), 0, result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating completion queue failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
            throw new NativeError(SystemUtil.getErrorMessage());
        }

        var completionQueue = result.get(CompletionQueue::new);
        NativeObjectRegistry.registerObject(completionQueue);

        return completionQueue;
    }

    @Nullable
    public AsyncEvent getAsyncEvent() {
        var result = Result.localInstance();
        var event = (AsyncEvent) Verbs.getPoolableInstance(AsyncEvent.class);

        var then = System.currentTimeMillis();
        Verbs.getAsyncEvent(getHandle(), event.getHandle(), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Polling async event failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());


            event.releaseInstance();

            return null;
        }

        if (System.currentTimeMillis() - then > Duration.ofSeconds(1).toMillis()) {
            LOGGER.warn("Waited {} seconds for async event {}", (System.currentTimeMillis() - then) / 1000, event.getEventType());
        }



        return event;
    }

    @Nullable
    public ExtendedDeviceAttributes queryExtendedDevice(QueryExtendedDeviceInput queryInput) {
        var result = Result.localInstance();
        var device = new ExtendedDeviceAttributes();

        Verbs.queryExtendedDevice(getHandle(), device.getHandle(), queryInput.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Querying extended device failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
            device = null;
        }



        return device;
    }

    @Nullable
    public ExtendedConnectionDomain openExtendedConnectionDomain(ExtendedConnectionDomain.InitialAttributes attributes) {
        var result = Result.localInstance();

        Verbs.openExtendedConnectionDomain(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Opening extended connection domain failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var extendedConnectionDomain = result.get(ExtendedConnectionDomain::new);
        NativeObjectRegistry.registerObject(extendedConnectionDomain);

        return extendedConnectionDomain;
    }

    @Nullable
    public SharedReceiveQueue createExtendedSharedReceiveQueue(SharedReceiveQueue.ExtendedInitialAttributes attributes) {
        var result = Result.localInstance();

        Verbs.createExtendedSharedReceiveQueue(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating extended shared receive queue failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var sharedReceiveQueue = result.get(SharedReceiveQueue::new);
        NativeObjectRegistry.registerObject(sharedReceiveQueue);

        return sharedReceiveQueue;
    }

    @Nullable
    public ExtendedCompletionQueue createExtendedCompletionQueue(ExtendedCompletionQueue.InitialAttributes attributes) {
        var result = Result.localInstance();

        Verbs.createExtendedCompletionQueue(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating extended completion queue failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var extendedCompletionQueue = result.get(ExtendedCompletionQueue::new);
        NativeObjectRegistry.registerObject(extendedCompletionQueue);

        return extendedCompletionQueue;
    }

    @Nullable
    public WorkQueue createWorkQueue(WorkQueue.InitialAttributes attributes) {
        var result = Result.localInstance();

        Verbs.createWorkQueue(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating work queue failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var workQueue = result.get(WorkQueue::new);
        NativeObjectRegistry.registerObject(workQueue);

        return workQueue;
    }

    @Nullable
    public ReceiveWorkQueueIndirectionTable createReceiveWorkQueueIndirectionTable(ReceiveWorkQueueIndirectionTable.InitialAttributes attributes) {
        var result = Result.localInstance();

        Verbs.createReceiveWorkQueueIndirectionTable(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating receive work queue indirection table failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var indirectionTable = result.get(ReceiveWorkQueueIndirectionTable::new);
        NativeObjectRegistry.registerObject(indirectionTable);

        return indirectionTable;
    }

    @Nullable
    public QueuePair createExtendedQueuePair(ExtendedQueuePair.InitialAttributes attributes) {
        var result = Result.localInstance();

        Verbs.createExtendedQueuePair(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating extended queue pair failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var queuePair = result.get(QueuePair::new);
        NativeObjectRegistry.registerObject(queuePair);

        return queuePair;
    }

    @Nullable
    public QueuePair openQueuePair(QueuePair.OpenAttributes attributes) {
        var result = Result.localInstance();

        Verbs.openQueuePair(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Opening queue pair failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var queuePair = result.get(QueuePair::new);
        NativeObjectRegistry.registerObject(queuePair);

        return queuePair;
    }
}
