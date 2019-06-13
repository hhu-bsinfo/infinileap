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

    // System helper functions
    static native int getOperationFlagCreate();
    static native int getOperationFlagExclusive();

    // Standard API
    static native int getNumDevices();
    static native String getDeviceName(long contextHandle);

    static native void openDevice(int index, long resultHandle);
    static native void closeDevice(long contextHandle, long resultHandle);

    static native void queryDevice(long contextHandle, long deviceHandle, long resultHandle);
    static native void queryPort(long contextHandle, long portHandle, int portNumber, long resultHandle);

    static native void getAsyncEvent(long contextHandle, long asyncEventHandle, long resultHandle);
    static native void acknowledgeAsyncEvent(long asyncEventHandle);

    static native void allocateProtectionDomain(long contextHandle, long resultHandle);
    static native void deallocateProtectionDomain(long protectionDomainHandle, long resultHandle);

    static native void allocateThreadDomain(long contextHandle, long attributesHandle, long resultHandle);
    static native void deallocateThreadDomain(long threadDomainHandle, long resultHandle);

    static native void allocateParentDomain(long contextHandle, long attributesHandle, long resultHandle);

    static native void allocateDeviceMemory(long contextHandle, long attributesHandle, long resultHandle);
    static native void registerDeviceMemoryAsMemoryRegion(long protectionDomainHandle, long deviceMemoryHandle, long offset, long length, int accessFlags, long resultHandle);
    static native void copyToDeviceMemory(long deviceMemoryHandle, long offset, long sourceAddress, long length, long resultHandle);
    static native void copyFromDeviceMemory(long targetAddress, long deviceMemoryHandle, long offset, long length, long resultHandle);
    static native void freeDeviceMemory(long deviceMemoryHandle, long resultHandle);

    static native void registerMemoryRegion(long protectionDomainHandle, long address, long size, int accessFlags, long resultHandle);
    static native void allocateNullMemoryRegion(long protectionDomainHandle, long resultHandle);
    static native void deregisterMemoryRegion(long memoryRegionHandle, long resultHandle);

    static native void allocateMemoryWindow(long protectionDomainHandle, int type, long resultHandle);
    static native void bindMemoryWindow(long memoryWindowHandle, long queuePairHandle, long attributesHandle, long resultHandle);
    static native void deallocateMemoryWindow(long memoryWindowHandle, long resultHandle);

    static native void createAddressHandle(long protectionDomainHandle, long attributesHandle, long resultHandle);
    static native void destroyAddressHandle(long addressHandleHandle, long resultHandle);

    static native void createCompletionChannel(long contextHandle, long resultHandle);
    static native void getCompletionEvent(long completionChannelHandle, long resultHandle);
    static native void destroyCompletionChannel(long completionChannelHandle, long resultHandle);

    static native void createCompletionQueue(long contextHandle, int maxElements, long userContextHandle, long completionChannelHandle, int completionVector, long resultHandle);
    static native void pollCompletionQueue(long completionQueueHandle, int numEntries, long arrayHandle, long resultHandle);
    static native void requestNotification(long completionQueueHandle, int solicitedOnly, long resultHandle);
    static native void acknowledgeCompletionEvents(long completionQueueHandle, int ackCount);
    static native void destroyCompletionQueue(long completionQueueHandle, long resultHandle);

    static native void createSharedReceiveQueue(long protectionDomainHandle, long attributesHandle, long resultHandle);
    static native void modifySharedReceiveQueue(long sharedReceiveQueueHandle, long attributesHandle, int attributesMask, long resultHandle);
    static native void querySharedReceiveQueue(long sharedReceiveQueueHandle, long attributesHandle, long resultHandle);
    static native void destroySharedReceiveQueue(long sharedReceiveQueueHandle, long resultHandle);

    static native void createQueuePair(long protectionDomainHandle, long attributesHandle, long resultHandle);
    static native void modifyQueuePair(long queuePairHandle, long attributesHandle, int attributesMask, long resultHandle);
    static native void queryQueuePair(long queuePairHandle, long attributesHandle, int attributesMask, long initialAttributesHandle, long resultHandle);
    static native void postSendWorkRequestQueuePair(long queuePairHandle, long sendWorkRequestHandle, long resultHandle);
    static native void postReceiveWorkRequestQueuePair(long queuePairHandle, long receiveWorkRequestHandle, long resultHandle);
    static native void destroyQueuePair(long queuePairHandle, long resultHandle);

    // Extended API
    static native void queryExtendedDevice(long contextHandle, long extendedDeviceHandle, long queryExtendedDeviceInputHandle, long resultHandle);

    static native void openExtendedConnectionDomain(long contextHandle, long attributesHandle, long resultHandle);
    static native void closeExtendedConnectionDomain(long extendedConnectionDomainHandle, long resultHandle);

    static native void createExtendedCompletionQueue(long contextHandle, long attributesHandle, long resultHandle);
    static native void extendedCompletionQueueToCompletionQueue(long extendedCompletionQueueHandle, long resultHandle);
    static native void startPoll(long extendedCompletionQueueHandle, long attributesHandle, long resultHandle);
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

    static native void createExtendedSharedReceiveQueue(long contextHandle, long attributesHandle, long resultHandle);

    static native void createWorkQueue(long contextHandle, long attributesHandle, long resultHandle);
    static native void modifyWorkQueue(long workQueueHandle, long attributesHandle, long resultHandle);
    static native void postReceiveWorkRequestWorkQueue(long workQueueHandle, long attributesHandle, long resultHandle);
    static native void destroyWorkQueue(long workQueueHandle, long resultHandle);
    static native void createReceiveWorkQueueIndirectionTable(long contextHandle, long attributesHandle, long resultHandle);
    static native void destroyReceiveWorkQueueIndirectionTable(long tableHandle, long resultHandle);

    static native void startWorkRequest(long extendedQueuePairHandle);
    static native void completeWorkRequest(long extendedQueuePairHandle);
    static native void abortWorkRequest(long extendedQueuePairHandle);
    static native void createExtendedQueuePair(long contextHandle, long attributesHandle, long resultHandle);
    static native void queuePairToExtendedQueuePair(long queuePairHandle, long resultHandle);
    static native void atomicCompareAndSwap(long extendedQueuePairHandle, int remoteKey, long remoteAddress, long compare, long swap);
    static native void atomicFetchAndAdd(long extendedQueuePairHandle, int remoteKey, long remoteAddress, long add);
    static native void bindMemoryWindow(long extendedQueuePairHandle, long memoryWindowHandle, int remoteKey, long informationHandle);
    static native void invalidateRemoteKey(long extendedQueuePairHandle, int remoteKey);
    static native void rdmaRead(long extendedQueuePairHandle, int remoteKey, long remoteAddress);
    static native void rdmaWrite(long extendedQueuePairHandle, int remoteKey, long remoteAddress);
    static native void rdmaWriteImm(long extendedQueuePairHandle, int remoteKey, long remoteAddress, int immData);
    static native void send(long extendedQueuePairHandle);
    static native void sendImm(long extendedQueuePairHandle, int immData);
    static native void sendInvalidateRemoteKey(long extendedQueuePairHandle, int remoteKey);
    static native void sendTcpSegmentOffload(long extendedQueuePairHandle, long hdrHandle, short hdrSize, short mss);
    static native void setUnreliableAddress(long extendedQueuePairHandle, long addressHandleHandle, int remoteQueuePairNumber, int remoteQkey);
    static native void setExtendedSharedReceiveQueueNumber(long extendedQueuePairHandle, int remoteSharedReceiveQueueNumber);
    static native void setInlineData(long extendedQueuePairHandle, long address, long length);
    static native void setInlineDataList(long extendedQueuePairHandle, long bufferCount, long bufferListHandle);
    static native void setScatterGatherElement(long extendedQueuePairHandle, int localKey, long address, int length);
    static native void setScatterGatherElementList(long extendedQueuePairHandle, int scatterGatherElementCount, long scatterGatherElementListHandle);

    // Dummy methods for benchmarking JNI overhead
    public static native void benchmarkDummyMethod1(long resultHandle);
    public static native long benchmarkDummyMethod2();
}
