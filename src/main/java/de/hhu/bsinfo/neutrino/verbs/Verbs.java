package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.util.RingBufferPool;

public final class Verbs {

    static {
        System.loadLibrary("neutrino");
    }

    @SuppressWarnings("FieldNamingConvention")
    private static final ThreadLocal<RingBufferPool<Result>> resultPool = ThreadLocal.withInitial(
        () -> new RingBufferPool<>(1024, Result::new));

    private Verbs() {
    }

    public static RingBufferPool<Result> getResultPool() {
        return resultPool.get();
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
}
