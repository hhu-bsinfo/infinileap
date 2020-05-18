package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.DeviceBuffer;
import de.hhu.bsinfo.neutrino.buffer.RegisteredBuffer;
import de.hhu.bsinfo.neutrino.struct.field.NativeInteger;
import de.hhu.bsinfo.neutrino.struct.field.NativeLong;
import de.hhu.bsinfo.neutrino.struct.LinkNative;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.*;
import de.hhu.bsinfo.neutrino.verbs.DeviceMemory.AllocationAttributes;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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

    public ProtectionDomain allocateParentDomain(@Nullable ThreadDomain threadDomain) throws IOException {
        return getContext().allocateParentDomain(new InitialAttributes.Builder(this)
                .withThreadDomain(threadDomain)
                .build());
    }

    private MemoryRegion registerDeviceMemory(DeviceMemory deviceMemory, long offset, long length, AccessFlag... flags) throws IOException {
        var result = Result.localInstance();

        Verbs.registerDeviceMemoryAsMemoryRegion(getHandle(), deviceMemory.getHandle(), offset, length, BitMask.intOf(flags), result.getHandle());
        if(result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        var memoryRegion = result.get(MemoryRegion::new);
        NativeObjectRegistry.registerObject(memoryRegion);

        return memoryRegion;
    }

    public RegisteredBuffer allocateMemory(long capacity, AccessFlag... flags) throws IOException {
        return registerMemory(UnsafeProvider.getUnsafe().allocateMemory(capacity), capacity, flags);
    }

    public MemoryRegion registerMemoryRegion(long handle, long capacity, AccessFlag... flags) throws IOException {
        var result = Result.localInstance();

        Verbs.registerMemoryRegion(getHandle(), handle, capacity, BitMask.intOf(flags), result.getHandle());
        if(result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        var memoryRegion = result.get(MemoryRegion::new);
        NativeObjectRegistry.registerObject(memoryRegion);

        return memoryRegion;
    }

    private RegisteredBuffer registerMemory(long handle, long capacity, AccessFlag... flags) throws IOException {
        var memoryRegion = registerMemoryRegion(handle, capacity, flags);
        return new RegisteredBuffer(memoryRegion);
    }

    public RegisteredBuffer allocateNullMemory() throws IOException {
        var result = Result.localInstance();

        Verbs.allocateNullMemoryRegion(getHandle(), result.getHandle());
        if(result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        var memoryRegion = result.get(MemoryRegion::new);
        NativeObjectRegistry.registerObject(memoryRegion);

        return memoryRegion == null ? null : new RegisteredBuffer(memoryRegion, 0, memoryRegion.getLength());
    }

    public DeviceBuffer allocateDeviceMemory(AllocationAttributes attributes, AccessFlag... flags) throws IOException {
        var deviceMemory = getContext().allocateDeviceMemory(attributes);
        var memoryRegion = registerDeviceMemory(deviceMemory, 0, attributes.getLength(), flags);
        return new DeviceBuffer(deviceMemory, memoryRegion, attributes.getLength());
    }

    public MemoryWindow allocateMemoryWindow(MemoryWindow.Type type) throws IOException {
        var result = Result.localInstance();

        Verbs.allocateMemoryWindow(getHandle(), type.getValue(), result.getHandle());
        if(result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        var memoryWindow = result.get(MemoryWindow::new);
        NativeObjectRegistry.registerObject(memoryWindow);

        return memoryWindow;
    }

    public AddressHandle createAddressHandle(AddressHandle.Attributes attributes) throws IOException {
        var result = Result.localInstance();

        Verbs.createAddressHandle(getHandle(), attributes.getHandle(), result.getHandle());
        if(result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        var addressHandle = result.get(AddressHandle::new);
        NativeObjectRegistry.registerObject(addressHandle);

        return addressHandle;
    }

    public SharedReceiveQueue createSharedReceiveQueue(SharedReceiveQueue.InitialAttributes initialAttributes) throws IOException {
        var result = Result.localInstance();

        Verbs.createSharedReceiveQueue(getHandle(), initialAttributes.getHandle(), result.getHandle());
        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        var sharedReceiveQueue = result.get(SharedReceiveQueue::new);
        NativeObjectRegistry.registerObject(sharedReceiveQueue);

        return sharedReceiveQueue;
    }

    public QueuePair createQueuePair(QueuePair.InitialAttributes initialAttributes) throws IOException {
        var result = Result.localInstance();

        Verbs.createQueuePair(getHandle(), initialAttributes.getHandle(), result.getHandle());
        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        var queuePair = result.get(QueuePair::new);
        NativeObjectRegistry.registerObject(queuePair);

        return queuePair;
    }

    @Override
    public void close() throws IOException {
        var result = Result.localInstance();

        Verbs.deallocateProtectionDomain(getHandle(), result.getHandle());
        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        NativeObjectRegistry.deregisterObject(this);
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
