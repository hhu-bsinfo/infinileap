package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.struct.field.NativeObject;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.util.*;
import de.hhu.bsinfo.neutrino.verbs.DeviceMemory.AllocationAttributes;
import de.hhu.bsinfo.neutrino.verbs.ExtendedDeviceAttributes.QueryExtendedDeviceInput;
import de.hhu.bsinfo.neutrino.verbs.ProtectionDomain.InitialAttributes;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

public class Context implements NativeObject, AutoCloseable {

    private static final int DEFAULT_DEVICE_NUMBER = 0;
    private static final int DEFAULT_PORT_NUMBER = 1;

    static {
        NativeLibrary.load("neutrino");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Context.class);

    private final long handle;

    Context(long handle) {
        this.handle = handle;
    }

    @Override
    public long getHandle() {
        return handle;
    }

    @Override
    public int getNativeSize() {
        return -1;
    }

    public static int getDeviceCount() {
        return Verbs.getNumDevices();
    }

    public static Context openDevice() throws IOException {
        return openDevice(DEFAULT_DEVICE_NUMBER);
    }

    public static Context openDevice(int index) throws IOException {
        var result = Result.localInstance();

        Verbs.openDevice(index, result.getHandle());
        if (result.isError()) {
            throw new IOException(Message.format("Opening device {} failed with error [{}]: {}", index, result.getStatus(), result.getStatusMessage()));
        }

        var context = result.get(Context::new);
        NativeObjectRegistry.registerObject(context);

        return context;
    }

    @Override
    public void close() throws IOException {
        var result = Result.localInstance();

        Verbs.closeDevice(getHandle(), result.getHandle());
        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        NativeObjectRegistry.deregisterObject(this);
    }

    public String getDeviceName() {
        return Verbs.getDeviceName(getHandle());
    }

    public DeviceAttributes queryDevice() throws IOException {
        var result = Result.localInstance();
        var device = new DeviceAttributes();

        Verbs.queryDevice(getHandle(), device.getHandle(), result.getHandle());
        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        return device;
    }

    public PortAttributes queryPort() throws IOException {
        return queryPort(DEFAULT_PORT_NUMBER);
    }

    public PortAttributes queryPort(int portNumber) throws IOException {
        var result = Result.localInstance();
        var port = new PortAttributes();

        Verbs.queryPort(getHandle(), port.getHandle(), portNumber, result.getHandle());
        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        return port;
    }

    DeviceMemory allocateDeviceMemory(AllocationAttributes attributes) throws IOException {
        var result = Result.localInstance();

        Verbs.allocateDeviceMemory(getHandle(), attributes.getHandle(), result.getHandle());
        if(result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        var deviceMemory = result.get(DeviceMemory::new);
        NativeObjectRegistry.registerObject(deviceMemory);

        return deviceMemory;
    }

    public ProtectionDomain allocateProtectionDomain() throws IOException {
        var result = Result.localInstance();

        Verbs.allocateProtectionDomain(getHandle(), result.getHandle());
        if(result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        var protectionDomain = result.get(ProtectionDomain::new);
        NativeObjectRegistry.registerObject(protectionDomain);

        return protectionDomain;
    }

    public ThreadDomain allocateThreadDomain(ThreadDomain.InitialAttributes attributes) throws IOException {
        var result = Result.localInstance();

        Verbs.allocateThreadDomain(getHandle(), attributes.getHandle(), result.getHandle());
        if(result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        var threadDomain = result.get(ThreadDomain::new);
        NativeObjectRegistry.registerObject(threadDomain);

        return threadDomain;
    }

    ProtectionDomain allocateParentDomain(InitialAttributes attributes) throws IOException {
        var result = Result.localInstance();

        Verbs.allocateParentDomain(getHandle(), attributes.getHandle(), result.getHandle());
        if(result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        var protectionDomain = result.get(ProtectionDomain::new);
        NativeObjectRegistry.registerObject(protectionDomain);

        return protectionDomain;
    }

    public CompletionChannel createCompletionChannel() throws IOException {
        var result = Result.localInstance();

        Verbs.createCompletionChannel(getHandle(), result.getHandle());
        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        var completionChannel = result.get(CompletionChannel::new);
        NativeObjectRegistry.registerObject(completionChannel);

        return completionChannel;
    }

    public CompletionQueue createCompletionQueue(int numElements) throws IOException {
        return createCompletionQueue(numElements, null);
    }

    public CompletionQueue createCompletionQueue(int numElements, @Nullable CompletionChannel channel) throws IOException {
        var result = Result.localInstance();

        Verbs.createCompletionQueue(
                getHandle(),
                numElements,
                NativeObject.NULL,
                channel == null ? NativeObject.NULL : channel.getHandle(),
                0,
                result.getHandle()
        );

        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        var completionQueue = result.get(CompletionQueue::new);
        NativeObjectRegistry.registerObject(completionQueue);

        return completionQueue;
    }

    public AsyncEvent getAsyncEvent() throws IOException {
        var result = Result.localInstance();
        var event = (AsyncEvent) Verbs.getPoolableInstance(AsyncEvent.class);

        var then = System.currentTimeMillis();
        Verbs.getAsyncEvent(getHandle(), event.getHandle(), result.getHandle());
        if(result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        if (System.currentTimeMillis() - then > Duration.ofSeconds(1).toMillis()) {
            LOGGER.warn("Waited {} seconds for async event {}", (System.currentTimeMillis() - then) / 1000, event.getEventType());
        }

        return event;
    }

    public ExtendedDeviceAttributes queryExtendedDevice(QueryExtendedDeviceInput queryInput) throws IOException {
        var result = Result.localInstance();
        var device = new ExtendedDeviceAttributes();

        Verbs.queryExtendedDevice(getHandle(), device.getHandle(), queryInput.getHandle(), result.getHandle());
        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }



        return device;
    }

    public ExtendedConnectionDomain openExtendedConnectionDomain(ExtendedConnectionDomain.InitialAttributes attributes) throws IOException {
        var result = Result.localInstance();

        Verbs.openExtendedConnectionDomain(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        var extendedConnectionDomain = result.get(ExtendedConnectionDomain::new);
        NativeObjectRegistry.registerObject(extendedConnectionDomain);

        return extendedConnectionDomain;
    }

    public SharedReceiveQueue createExtendedSharedReceiveQueue(SharedReceiveQueue.ExtendedInitialAttributes attributes) throws IOException {
        var result = Result.localInstance();

        Verbs.createExtendedSharedReceiveQueue(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        var sharedReceiveQueue = result.get(SharedReceiveQueue::new);
        NativeObjectRegistry.registerObject(sharedReceiveQueue);

        return sharedReceiveQueue;
    }

    public ExtendedCompletionQueue createExtendedCompletionQueue(ExtendedCompletionQueue.InitialAttributes attributes) throws IOException {
        var result = Result.localInstance();

        Verbs.createExtendedCompletionQueue(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        var extendedCompletionQueue = result.get(ExtendedCompletionQueue::new);
        NativeObjectRegistry.registerObject(extendedCompletionQueue);

        return extendedCompletionQueue;
    }

    public WorkQueue createWorkQueue(WorkQueue.InitialAttributes attributes) throws IOException {
        var result = Result.localInstance();

        Verbs.createWorkQueue(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        var workQueue = result.get(WorkQueue::new);
        NativeObjectRegistry.registerObject(workQueue);

        return workQueue;
    }

    public ReceiveWorkQueueIndirectionTable createReceiveWorkQueueIndirectionTable(ReceiveWorkQueueIndirectionTable.InitialAttributes attributes) throws IOException {
        var result = Result.localInstance();

        Verbs.createReceiveWorkQueueIndirectionTable(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        var indirectionTable = result.get(ReceiveWorkQueueIndirectionTable::new);
        NativeObjectRegistry.registerObject(indirectionTable);

        return indirectionTable;
    }

    public QueuePair createExtendedQueuePair(ExtendedQueuePair.InitialAttributes attributes) throws IOException {
        var result = Result.localInstance();

        Verbs.createExtendedQueuePair(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        var queuePair = result.get(QueuePair::new);
        NativeObjectRegistry.registerObject(queuePair);

        return queuePair;
    }

    public QueuePair openQueuePair(QueuePair.OpenAttributes attributes) throws IOException {
        var result = Result.localInstance();

        Verbs.openQueuePair(getHandle(), attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        var queuePair = result.get(QueuePair::new);
        NativeObjectRegistry.registerObject(queuePair);

        return queuePair;
    }
}
