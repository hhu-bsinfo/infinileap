package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.util.Poolable;
import de.hhu.bsinfo.neutrino.util.RingBufferPool;
import java.util.HashMap;
import java.util.Map;

public final class Verbs {

    private static final int DEFAULT_POOL_SIZE = 1024;

    @SuppressWarnings("FieldNamingConvention")
    private static final Map<Class<? extends Poolable>, ThreadLocal<RingBufferPool<Poolable>>> poolMap = new HashMap<>();

    static {
        System.loadLibrary("neutrino");

        poolMap.put(Result.class, ThreadLocal.withInitial(() -> new RingBufferPool<>(DEFAULT_POOL_SIZE, Result::new)));
    }

    private Verbs() {
    }

    public static Poolable getPoolableInstance(Class<? extends Poolable> clazz) {
        return poolMap.get(clazz).get().getInstance();
    }

    public static void returnPoolableInstance(Poolable instance) {
        poolMap.get(instance.getClass()).get().returnInstance(instance);
    }

    static native int getNumDevices();
    static native String getDeviceName(long contextHandle);

    static native void openDevice(int index, long resultHandle);
    static native void closeDevice(long contextHandle, long resultHandle);
    static native void queryDevice(long contextHandle, long deviceHandle, long resultHandle);
    static native void queryPort(long contextHandle, long portHandle, int portNumber, long resultHandle);
    static native void allocateProtectionDomain(long contextHandle, long resultHandle);
    static native void deallocateProtectionDomain(long protectionDomainHandle, long resultHandle);
    static native void registerMemoryRegion(long protectionDomainHandle, long address, long size, int accessFlags, long resultHandle);
    static native void deregisterMemoryRegion(long memoryRegionHandle, long resultHandle);
    static native void createCompletionQueue(long contextHandle, int maxElements, long userContextHandle, long completionChannelHandle, int completionVector, long resultHandle);
    static native void destroyCompletionQueue(long completionQueueHandle, long resultHandle);
    static native void postSendWorkRequest(long queuePairHandle, long sendWorkRequestHandle, long resultHandle);
    static native void postReceiveWorkRequest(long queuePairHandle, long receiveWorkRequestHandle, long resultHandle);
    static native void createSharedReceiveQueue(long protectionDomainHandle, long attributesHandle, long resultHandle);
    static native void createQueuePair(long protectionDomainHandle, long attributesHandle, long resultHandle);
    static native void modifyQueuePair(long queuePairHandle, long attributesHandle, int attributesMask, long resultHandle);
    static native void destroyQueuePair(long queuePairHandle, long resultHandle);
    static native void pollCompletionQueue(long completionQueueHandle, int numEntries, long arrayHandle, long resultHandle);

    public static native void benchmarkDummyMethod(long resultHandle);
}
