package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.util.Poolable;
import de.hhu.bsinfo.neutrino.util.RingBufferPool;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class Verbs {

    private static final int DEFAULT_POOL_SIZE = 1024;

    @SuppressWarnings("FieldNamingConvention")
    private static final Map<Class<? extends Poolable>, ThreadLocal<RingBufferPool<Poolable>>> poolMap = new HashMap<>();

    static {
        System.loadLibrary("neutrino");

        poolMap.put(Result.class, ThreadLocal.withInitial(() -> new RingBufferPool<>(DEFAULT_POOL_SIZE, Result::new)));
        poolMap.put(AsyncEvent.class, ThreadLocal.withInitial(() -> new RingBufferPool<>(DEFAULT_POOL_SIZE, AsyncEvent::new)));
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

    // Standard API
    static native void openDevice(int index, long resultHandle);
    static native void closeDevice(long contextHandle, long resultHandle);
    static native void queryDevice(long contextHandle, long deviceHandle, long resultHandle);
    static native void queryPort(long contextHandle, long portHandle, int portNumber, long resultHandle);
    static native void getAsyncEvent(long contextHandle, long asyncEventHandle, long resultHandle);
    static native void acknowledgeAsyncEvent(long asyncEventHandle);
    static native void allocateProtectionDomain(long contextHandle, long resultHandle);
    static native void deallocateProtectionDomain(long protectionDomainHandle, long resultHandle);
    static native void registerMemoryRegion(long protectionDomainHandle, long address, long size, int accessFlags, long resultHandle);
    static native void deregisterMemoryRegion(long memoryRegionHandle, long resultHandle);
    static native void createCompletionChannel(long contextHandle, long resultHandle);
    static native void getCompletionEvent(long completionChannelHandle, long resultHandle);
    static native void destroyCompletionChannel(long completionChannelHandle, long resultHandle);
    static native void createCompletionQueue(long contextHandle, int maxElements, long userContextHandle, long completionChannelHandle, int completionVector, long resultHandle);
    static native void pollCompletionQueue(long completionQueueHandle, int numEntries, long arrayHandle, long resultHandle);
    static native void requestNotification(long completionQueueHandle, int solicitedOnly, long resultHandle);
    static native void acknowledgeCompletionEvents(long completionQueueHandle, int ackCount);
    static native void destroyCompletionQueue(long completionQueueHandle, long resultHandle);
    static native void postSendWorkRequest(long queuePairHandle, long sendWorkRequestHandle, long resultHandle);
    static native void postReceiveWorkRequest(long queuePairHandle, long receiveWorkRequestHandle, long resultHandle);
    static native void createSharedReceiveQueue(long protectionDomainHandle, long attributesHandle, long resultHandle);
    static native void createQueuePair(long protectionDomainHandle, long attributesHandle, long resultHandle);
    static native void modifyQueuePair(long queuePairHandle, long attributesHandle, int attributesMask, long resultHandle);
    static native void queryQueuePair(long queuePairHandle, long attributesHandle, int attributesMask, long initialAttributesHandle, long resultHandle);
    static native void destroyQueuePair(long queuePairHandle, long resultHandle);

    // Extended API
    static native void createExtendedCompletionQueue(long contextHandle, long initialAttributesHandle, long resultHandle);
    static native void extendedCompletionQueueToCompletionQueue(long extendedCompletionQueueHandle, long resultHandle);
    static native void startPoll(long extendedCompletionQueueHandle, long pollCompletionQueueAttributesHandle, long resultHandle);
    static native void nextPoll(long extendedCompletionQueueHandle, long resultHandle);
    static native void endPoll(long extendedCompletionQueueHandle);
    static native int readOpCode(long extendedCompletionQueueHandle);
    static native int readVendorError(long extendedCompletionQueueHandle);
    static native int readByteCount(long extendedCompletionQueueHandle);
    static native int readImmediateData(long extendedCompletionQueueHandle);
    static native int readInvalidatedRemoteKey(long extendedCompletionQueueHandle);
    static native int readQueuePairNumber(long extendedCompletionQueueHandle);
    static native int readSourceQueuePair(long extendedCompletionQueueHandle);
    static native int readWorkCompletionFlags(long extendedCompletionQueueHandle);
    static native int readSourceLocalId(long extendedCompletionQueueHandle);
    static native byte readServiceLevel(long extendedCompletionQueueHandle);
    static native byte readPathBits(long extendedCompletionQueueHandle);
    static native long readCompletionTimestamp(long extendedCompletionQueueHandle);
    static native long readCompletionWallClockNanoseconds(long extendedCompletionQueueHandle);
    static native short readCVLan(long extendedCompletionQueueHandle);
    static native int readFlowTag(long extendedCompletionQueueHandle);
    static native void readTagMatchingInfo(long extendedCompletionQueueHandle, long tagMatchingInfoHandle);

    // Dummy methods for benchmarking JNI overhead
    public static native void benchmarkDummyMethod1(long resultHandle);
    public static native long benchmarkDummyMethod2();
}
