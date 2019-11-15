package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.DeviceBuffer;
import de.hhu.bsinfo.neutrino.buffer.RegisteredBuffer;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.BitMask;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.MemoryUtil;
import de.hhu.bsinfo.neutrino.util.NativeObjectRegistry;
import de.hhu.bsinfo.neutrino.verbs.DeviceMemory.AllocationAttributes;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

@LinkNative("ibv_pd")
public class ProtectionDomain extends Struct implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtectionDomain.class);

    private final Context context = referenceField("context");

    ProtectionDomain(long handle) {
        super(handle);
    }

    public Context getContext() {
        return context;
    }

    @Nullable
    public ProtectionDomain allocateParentDomain(@Nullable ThreadDomain threadDomain) {
        return getContext().allocateParentDomain(new InitialAttributes.Builder(this)
                .withThreadDomain(threadDomain)
                .build());
    }

    @Nullable
    private MemoryRegion registerDeviceMemory(DeviceMemory deviceMemory, long offset, long length, AccessFlag... flags) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.registerDeviceMemoryAsMemoryRegion(getHandle(), deviceMemory.getHandle(), offset, length, BitMask.intOf(flags), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Registering device memory as memory region failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var memoryRegion = result.getAndRelease(MemoryRegion::new);
        NativeObjectRegistry.registerObject(memoryRegion);

        return memoryRegion;
    }

    @Nullable
    public RegisteredBuffer allocateMemory(long capacity, AccessFlag... flags) {
        return registerMemory(MemoryUtil.allocateMemory(capacity), capacity, flags);
    }

    public MemoryRegion registerMemoryRegion(long handle, long capacity, AccessFlag... flags) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.registerMemoryRegion(getHandle(), handle, capacity, BitMask.intOf(flags), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Registering memory region failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var memoryRegion = result.getAndRelease(MemoryRegion::new);
        NativeObjectRegistry.registerObject(memoryRegion);

        return memoryRegion;
    }

    @Nullable
    private RegisteredBuffer registerMemory(long handle, long capacity, AccessFlag... flags) {
        var memoryRegion = registerMemoryRegion(handle, capacity, flags);
        return memoryRegion == null ? null : new RegisteredBuffer(memoryRegion);
    }

    @Nullable
    public RegisteredBuffer allocateNullMemory() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.allocateNullMemoryRegion(getHandle(), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Allocating null memory region failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var memoryRegion = result.getAndRelease(MemoryRegion::new);
        NativeObjectRegistry.registerObject(memoryRegion);

        return memoryRegion == null ? null : new RegisteredBuffer(memoryRegion, 0, memoryRegion.getLength());
    }

    @Nullable
    public DeviceBuffer allocateDeviceMemory(AllocationAttributes attributes, AccessFlag... flags) {
        var deviceMemory = getContext().allocateDeviceMemory(attributes);

        if(deviceMemory == null) {
            return null;
        }

        var memoryRegion = registerDeviceMemory(deviceMemory, 0, attributes.getLength(), flags);

        if(memoryRegion == null) {
            return null;
        }

        return new DeviceBuffer(deviceMemory, memoryRegion, attributes.getLength());
    }

    @Nullable
    public MemoryWindow allocateMemoryWindow(MemoryWindow.Type type) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.allocateMemoryWindow(getHandle(), type.getValue(), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Allocating memory window failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var memoryWindow = result.getAndRelease(MemoryWindow::new);
        NativeObjectRegistry.registerObject(memoryWindow);

        return memoryWindow;
    }

    @Nullable
    public AddressHandle createAddressHandle(AddressHandle.Attributes attributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.createAddressHandle(getHandle(), attributes.getHandle(), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Creating address handle failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var addressHandle = result.getAndRelease(AddressHandle::new);
        NativeObjectRegistry.registerObject(addressHandle);

        return addressHandle;
    }

    @Nullable
    public SharedReceiveQueue createSharedReceiveQueue(SharedReceiveQueue.InitialAttributes initialAttributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.createSharedReceiveQueue(getHandle(), initialAttributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating shared receive queue failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var sharedReceiveQueue = result.getAndRelease(SharedReceiveQueue::new);
        NativeObjectRegistry.registerObject(sharedReceiveQueue);

        return sharedReceiveQueue;
    }

    @Nullable
    public QueuePair createQueuePair(QueuePair.InitialAttributes initialAttributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.createQueuePair(getHandle(), initialAttributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating queue pair failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        var queuePair = result.getAndRelease(QueuePair::new);
        NativeObjectRegistry.registerObject(queuePair);

        return queuePair;
    }

    @Override
    public void close() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.deallocateProtectionDomain(getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Closing protection domain failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        } else {
            NativeObjectRegistry.deregisterObject(this);
        }

        result.releaseInstance();
    }

    @LinkNative("ibv_parent_domain_init_attr")
    public static final class InitialAttributes extends Struct {

        private final NativeLong protectionDomain = longField("pd");
        private final NativeLong threadDomain = longField("td");
        private final NativeInteger compatibilityMask = integerField("comp_mask");

        InitialAttributes() {}

        public ProtectionDomain getProtectionDomain() {
            return NativeObjectRegistry.getObject(protectionDomain.get());
        }

        public ThreadDomain getThreadDomain() {
            return NativeObjectRegistry.getObject(threadDomain.get());
        }

        public int getCompatibilityMask() {
            return compatibilityMask.get();
        }

        void setProtectionDomain(final ProtectionDomain protectionDomain) {
            this.protectionDomain.set(protectionDomain.getHandle());
        }

        void setThreadDomain(final ThreadDomain threadDomain) {
            this.threadDomain.set(threadDomain.getHandle());
        }

        void setCompatibilityMask(final int value) {
            compatibilityMask.set(value);
        }

        public static final class Builder {

            private ProtectionDomain protectionDomain;
            private ThreadDomain threadDomain;
            private int compatibilityMask;

            public Builder(ProtectionDomain protectionDomain) {
                this.protectionDomain = protectionDomain;
            }

            public Builder withThreadDomain(ThreadDomain threadDomain) {
                this.threadDomain = threadDomain;
                return this;
            }

            public Builder withCompatibilityMask(int compatibilityMask) {
                this.compatibilityMask = compatibilityMask;
                return this;
            }

            public InitialAttributes build() {
                var ret = new InitialAttributes();

                if(protectionDomain != null) ret.setProtectionDomain(protectionDomain);
                if(threadDomain != null) ret.setThreadDomain(threadDomain);
                ret.setCompatibilityMask(compatibilityMask);

                return ret;
            }
        }
    }
}
