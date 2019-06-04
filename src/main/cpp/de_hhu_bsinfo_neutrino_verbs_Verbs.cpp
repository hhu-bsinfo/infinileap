#include <de_hhu_bsinfo_neutrino_verbs_Verbs.h>
#include <infiniband/verbs.h>
#include <stddef.h>
#include <vector>
#include <neutrino/NativeCall.hpp>

JNIEXPORT jint JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_getNumDevices (JNIEnv *env, jclass clazz) {
    int numDevices = 0;

    ibv_device **devices = ibv_get_device_list(&numDevices);
    if (devices != nullptr) {
        ibv_free_device_list(devices);
    }

    return numDevices;
}

JNIEXPORT jstring JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_getDeviceName (JNIEnv *env, jclass clazz, jlong contextHandle) {
    auto context = NativeCall::castHandle<ibv_context>(contextHandle);

    const char *ret = ibv_get_device_name(context->device);
    if(ret == nullptr) {
        return env->NewStringUTF("");
    } else {
        return env->NewStringUTF(ret);
    }
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_openDevice (JNIEnv *env, jclass clazz, jint index, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    int numDevices = 0;

    ibv_device **devices = ibv_get_device_list(&numDevices);
    if (devices == nullptr) {
        NativeCall::setResult(result, 1, nullptr);
        return;
    }

    if (index >= numDevices) {
        ibv_free_device_list(devices);
        NativeCall::setResult(result, 1, nullptr);
        return;
    }

    NativeCall::setResult(result, 0, ibv_open_device(devices[index]));

    ibv_free_device_list(devices);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_closeDevice (JNIEnv *env, jclass clazz, jlong contextHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto context = NativeCall::castHandle<ibv_context>(contextHandle);

    NativeCall::setResult(result, ibv_close_device(context), nullptr);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_queryDevice (JNIEnv *env, jclass clazz, jlong contextHandle, jlong deviceHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto context = NativeCall::castHandle<ibv_context>(contextHandle);
    auto device = NativeCall::castHandle<ibv_device_attr>(deviceHandle);

    NativeCall::setResult(result, ibv_query_device(context, device), nullptr);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_queryPort (JNIEnv *env, jclass clazz, jlong contextHandle, jlong portHandle, jint portNumber, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto context = NativeCall::castHandle<ibv_context>(contextHandle);
    auto port = NativeCall::castHandle<ibv_port_attr>(portHandle);

    NativeCall::setResult(result, ibv_query_port(context, portNumber, port), nullptr);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_getAsyncEvent (JNIEnv *env, jclass clazz, jlong contextHandle, jlong asyncEventHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto context = NativeCall::castHandle<ibv_context>(contextHandle);
    auto asyncEvent = NativeCall::castHandle<ibv_async_event>(asyncEventHandle);

    NativeCall::setResult(result, ibv_get_async_event(context, asyncEvent), nullptr);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_acknowledgeAsyncEvent (JNIEnv *env, jclass clazz, jlong asyncEventHandle) {
    auto asyncEvent = NativeCall::castHandle<ibv_async_event>(asyncEventHandle);

    ibv_ack_async_event(asyncEvent);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_allocateProtectionDomain (JNIEnv *env, jclass clazz, jlong contextHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto context = NativeCall::castHandle<ibv_context>(contextHandle);

    auto protectionDomain = ibv_alloc_pd(context);

    NativeCall::setResult(result, protectionDomain == nullptr ? errno : 0, protectionDomain);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_deallocateProtectionDomain (JNIEnv *env, jclass clazz, jlong protectionDomainHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto protectionDomain = NativeCall::castHandle<ibv_pd>(protectionDomainHandle);

    NativeCall::setResult(result, ibv_dealloc_pd(protectionDomain), 0);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_allocateThreadDomain (JNIEnv *env, jclass clazz, jlong contextHandle, jlong attributesHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto context = NativeCall::castHandle<ibv_context>(contextHandle);
    auto attributes = NativeCall::castHandle<ibv_td_init_attr>(attributesHandle);

    auto threadDomain = ibv_alloc_td(context, attributes);

    NativeCall::setResult(result, threadDomain == nullptr ? errno : 0, threadDomain);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_deallocateThreadDomain (JNIEnv *env, jclass clazz, jlong threadDomainHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto threadDomain = NativeCall::castHandle<ibv_td>(threadDomainHandle);

    NativeCall::setResult(result, ibv_dealloc_td(threadDomain), 0);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_allocateParentDomain (JNIEnv *env, jclass clazz, jlong contextHandle, jlong attributesHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto context = NativeCall::castHandle<ibv_context>(contextHandle);
    auto attributes = NativeCall::castHandle<ibv_parent_domain_init_attr>(attributesHandle);

    auto parentDomain = ibv_alloc_parent_domain(context, attributes);

    NativeCall::setResult(result, parentDomain == nullptr ? errno : 0, parentDomain);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_allocateDeviceMemory (JNIEnv *env, jclass clazz, jlong contextHandle, jlong attributesHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto context = NativeCall::castHandle<ibv_context>(contextHandle);
    auto attributes = NativeCall::castHandle<ibv_alloc_dm_attr>(attributesHandle);

    auto deviceMemory = ibv_alloc_dm(context, attributes);

    NativeCall::setResult(result, deviceMemory == nullptr ? errno : 0, deviceMemory);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_registerDeviceMemoryAsMemoryRegion (JNIEnv *env, jclass clazz, jlong protectionDomainHandle, jlong deviceMemoryHandle, jlong offset, jlong length, jint accessFlags, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto protectionDomain = NativeCall::castHandle<ibv_pd>(protectionDomainHandle);
    auto deviceMemory = NativeCall::castHandle<ibv_dm>(deviceMemoryHandle);

    auto memoryRegion = ibv_reg_dm_mr(protectionDomain, deviceMemory, offset, length, accessFlags);

    NativeCall::setResult(result, memoryRegion == nullptr ? errno : 0, memoryRegion);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_copyToDeviceMemory (JNIEnv *env, jclass clazz, jlong deviceMemoryHandle, jlong offset, jlong sourceAddress, jlong length, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto deviceMemory = NativeCall::castHandle<ibv_dm>(deviceMemoryHandle);

    NativeCall::setResult(result, ibv_memcpy_to_dm(deviceMemory, offset, reinterpret_cast<const void *>(sourceAddress), length), 0);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_copyFromDeviceMemory
    (JNIEnv *env, jclass clazz, jlong targetAddress, jlong deviceMemoryHandle, jlong offset, jlong length, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto deviceMemory = NativeCall::castHandle<ibv_dm>(deviceMemoryHandle);

    NativeCall::setResult(result, ibv_memcpy_from_dm(reinterpret_cast<void *>(targetAddress), deviceMemory, offset, length), 0);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_freeDeviceMemory (JNIEnv *env, jclass clazz, jlong deviceMemoryHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto deviceMemory = NativeCall::castHandle<ibv_dm>(deviceMemoryHandle);

    NativeCall::setResult(result, ibv_free_dm(deviceMemory), 0);
}


JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_registerMemoryRegion (JNIEnv *env, jclass clazz, jlong protectionDomainHandle, jlong address, jlong size, jint accessFlags, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto protectionDomain = NativeCall::castHandle<ibv_pd>(protectionDomainHandle);
    auto bufferAddress = NativeCall::castHandle<void>(address);

    auto memoryRegion = ibv_reg_mr(protectionDomain, bufferAddress, size, accessFlags);

    NativeCall::setResult(result, memoryRegion == nullptr ? errno : 0, memoryRegion);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_allocateNullMemoryRegion (JNIEnv *env, jclass clazz, jlong protectionDomainHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto protectionDomain = NativeCall::castHandle<ibv_pd>(protectionDomainHandle);

    auto memoryRegion = ibv_alloc_null_mr(protectionDomain);

    NativeCall::setResult(result, memoryRegion == nullptr ? errno : 0, memoryRegion);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_deregisterMemoryRegion (JNIEnv *env, jclass clazz, jlong memoryRegionHandle, jlong resultHandle) {
    auto memoryRegion = NativeCall::castHandle<ibv_mr>(memoryRegionHandle);
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);

    NativeCall::setResult(result, ibv_dereg_mr(memoryRegion), 0);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_allocateMemoryWindow (JNIEnv *env, jclass clazz, jlong protectionDomainHandle, jint type, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto protectionDomain = NativeCall::castHandle<ibv_pd>(protectionDomainHandle);

    auto memoryWindow = ibv_alloc_mw(protectionDomain, static_cast<ibv_mw_type>(type));

    NativeCall::setResult(result, memoryWindow == nullptr ? errno : 0, memoryWindow);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_bindMemoryWindow (JNIEnv *env, jclass clazz, jlong memoryWindowHandle, jlong queuePairHandle, jlong attributesHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto memoryWindow = NativeCall::castHandle<ibv_mw>(memoryWindowHandle);
    auto queuePair = NativeCall::castHandle<ibv_qp>(queuePairHandle);
    auto attributes = NativeCall::castHandle<ibv_mw_bind>(attributesHandle);

    NativeCall::setResult(result, ibv_bind_mw(queuePair, memoryWindow, attributes), 0);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_deallocateMemoryWindow (JNIEnv *env, jclass clazz, jlong memoryWindowHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto memoryWindow = NativeCall::castHandle<ibv_mw>(memoryWindowHandle);

    NativeCall::setResult(result, ibv_dealloc_mw(memoryWindow), 0);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_createAddressHandle (JNIEnv *env, jclass clazz, jlong protectionDomainHandle, jlong attributesHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto protectionDomain = NativeCall::castHandle<ibv_pd>(protectionDomainHandle);
    auto attributes = NativeCall::castHandle<ibv_ah_attr>(attributesHandle);

    auto addressHandle = ibv_create_ah(protectionDomain, attributes);

    NativeCall::setResult(result, addressHandle == nullptr ? errno : 0, addressHandle);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_destroyAddressHandle (JNIEnv *env, jclass clazz, jlong addressHandleHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto addressHandle = NativeCall::castHandle<ibv_ah>(addressHandleHandle);

    NativeCall::setResult(result, ibv_destroy_ah(addressHandle), 0);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_createCompletionChannel (JNIEnv *env, jclass clazz, jlong contextHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto context = NativeCall::castHandle<ibv_context>(contextHandle);

    auto completionChannel = ibv_create_comp_channel(context);

    NativeCall::setResult(result, completionChannel == nullptr ? errno : 0, completionChannel);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_getCompletionEvent (JNIEnv *env, jclass clazz, jlong completionChannelHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto completionChannel = NativeCall::castHandle<ibv_comp_channel>(completionChannelHandle);

    ibv_cq *completionQueueRef = nullptr;
    void *contextRef = nullptr;

    auto ret = ibv_get_cq_event(completionChannel, &completionQueueRef, &contextRef);

    NativeCall::setResult(result, ret, completionQueueRef);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_destroyCompletionChannel (JNIEnv *env, jclass clazz, jlong completionChannelHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto channel = NativeCall::castHandle<ibv_comp_channel>(completionChannelHandle);

    NativeCall::setResult(result, ibv_destroy_comp_channel(channel), 0);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_createCompletionQueue (JNIEnv *env, jclass clazz, jlong contextHandle, jint maxElements, jlong userContextHandle, jlong completionChannelHandle, jint completionVector, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto context = NativeCall::castHandle<ibv_context>(contextHandle);
    auto userContext = NativeCall::castHandle<void>(userContextHandle);
    auto completionChannel = NativeCall::castHandle<ibv_comp_channel>(completionChannelHandle);

    auto completionQueue = ibv_create_cq(context, maxElements, userContext, completionChannel, completionVector);

    NativeCall::setResult(result, completionQueue == nullptr ? errno : 0, completionQueue);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_pollCompletionQueue (JNIEnv *env, jclass clazz, jlong completionQueueHandle, jint numEntries, jlong arrayHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto array = NativeCall::castHandle<ibv_wc>(arrayHandle);
    auto completionQueue = NativeCall::castHandle<ibv_cq>(completionQueueHandle);

    auto workRequestCount = ibv_poll_cq(completionQueue, numEntries, array);

    NativeCall::setResult(result, workRequestCount < 0 ? errno : 0, workRequestCount);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_requestNotification (JNIEnv *env, jclass clazz, jlong completionQueueHandle, jint solicitedOnly, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto completionQueue = NativeCall::castHandle<ibv_cq>(completionQueueHandle);

    NativeCall::setResult(result, ibv_req_notify_cq(completionQueue, solicitedOnly), nullptr);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_acknowledgeCompletionEvents (JNIEnv *env, jclass clazz, jlong completionQueueHandle, jint ackCount) {
    auto completionQueue = NativeCall::castHandle<ibv_cq>(completionQueueHandle);

    ibv_ack_cq_events(completionQueue, ackCount);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_destroyCompletionQueue (JNIEnv *env, jclass clazz, jlong completionQueueHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto completionQueue = NativeCall::castHandle<ibv_cq>(completionQueueHandle);

    NativeCall::setResult(result, ibv_destroy_cq(completionQueue), 0);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_postSendWorkRequest (JNIEnv *env, jclass clazz, jlong queuePairHandle, jlong workRequestHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto workRequest = NativeCall::castHandle<ibv_send_wr>(workRequestHandle);
    auto queuePair = NativeCall::castHandle<ibv_qp>(queuePairHandle);

    ibv_send_wr* badWorkRequest;
    NativeCall::setResult(result, ibv_post_send(queuePair, workRequest, &badWorkRequest), badWorkRequest);
}


JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_postReceiveWorkRequest (JNIEnv *env, jclass clazz, jlong queuePairHandle, jlong workRequestHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto workRequest = NativeCall::castHandle<ibv_recv_wr>(workRequestHandle);
    auto queuePair = NativeCall::castHandle<ibv_qp>(queuePairHandle);

    ibv_recv_wr* badWorkRequest;
    NativeCall::setResult(result, ibv_post_recv(queuePair, workRequest, &badWorkRequest), badWorkRequest);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_createSharedReceiveQueue (JNIEnv *env, jclass clazz, jlong protectionDomainHandle, jlong attributesHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto attributes = NativeCall::castHandle<ibv_srq_init_attr>(attributesHandle);
    auto protectionDomain = NativeCall::castHandle<ibv_pd>(protectionDomainHandle);

    auto sharedReceiveQueue = ibv_create_srq(protectionDomain, attributes);

    NativeCall::setResult(result, sharedReceiveQueue == nullptr ? errno : 0, sharedReceiveQueue);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_modifySharedReceiveQueue (JNIEnv *env, jclass clazz, jlong sharedReceiveQueueHandle, jlong attributesHandle, jint attributesMask, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto attributes = NativeCall::castHandle<ibv_srq_attr>(attributesHandle);
    auto sharedReceiveQueue = NativeCall::castHandle<ibv_srq>(sharedReceiveQueueHandle);

    NativeCall::setResult(result, ibv_modify_srq(sharedReceiveQueue, attributes, attributesMask), 0);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_querySharedReceiveQueue (JNIEnv *env, jclass clazz, jlong sharedReceiveQueueHandle, jlong attributesHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto attributes = NativeCall::castHandle<ibv_srq_attr>(attributesHandle);
    auto sharedReceiveQueue = NativeCall::castHandle<ibv_srq>(sharedReceiveQueueHandle);

    NativeCall::setResult(result, ibv_query_srq(sharedReceiveQueue, attributes), 0);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_destroySharedReceiveQueue (JNIEnv *env, jclass clazz, jlong sharedReceiveQueueHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto sharedReceiveQueue = NativeCall::castHandle<ibv_srq>(sharedReceiveQueueHandle);

    NativeCall::setResult(result, ibv_destroy_srq(sharedReceiveQueue), 0);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_createQueuePair (JNIEnv *env, jclass clazz, jlong protectionDomainHandle, jlong attributesHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto attributes = NativeCall::castHandle<ibv_qp_init_attr>(attributesHandle);
    auto protectionDomain = NativeCall::castHandle<ibv_pd>(protectionDomainHandle);

    auto queuePair = ibv_create_qp(protectionDomain, attributes);

    NativeCall::setResult(result, queuePair == nullptr ? errno : 0, queuePair);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_modifyQueuePair (JNIEnv *env, jclass clazz, jlong queuePairHandle, jlong attributesHandle, jint attributesMask, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto attributes = NativeCall::castHandle<ibv_qp_attr>(attributesHandle);
    auto queuePair = NativeCall::castHandle<ibv_qp>(queuePairHandle);

    NativeCall::setResult(result, ibv_modify_qp(queuePair, attributes, attributesMask), 0);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_queryQueuePair (JNIEnv *env, jclass clazz, jlong queuePairHandle, jlong attributesHandle, jint attributesMask, jlong initialAttributesHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto attributes = NativeCall::castHandle<ibv_qp_attr>(attributesHandle);
    auto initialAttributes = NativeCall::castHandle<ibv_qp_init_attr>(initialAttributesHandle);
    auto queuePair = NativeCall::castHandle<ibv_qp>(queuePairHandle);

    NativeCall::setResult(result, ibv_query_qp(queuePair, attributes, attributesMask, initialAttributes), 0);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_destroyQueuePair (JNIEnv *env, jclass clazz, jlong queuePairHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto queuePair = NativeCall::castHandle<ibv_qp>(queuePairHandle);

    NativeCall::setResult(result, ibv_destroy_qp(queuePair), 0);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_queryExtendedDevice (JNIEnv *env, jclass clazz, jlong contextHandle, jlong extendedDeviceHandle, jlong queryExtendedDeviceInputHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto context = NativeCall::castHandle<ibv_context>(contextHandle);
    auto device = NativeCall::castHandle<ibv_device_attr_ex>(extendedDeviceHandle);
    auto queryInput = NativeCall::castHandle<ibv_query_device_ex_input>(queryExtendedDeviceInputHandle);

    NativeCall::setResult(result, ibv_query_device_ex(context, queryInput, device), 0);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_createExtendedCompletionQueue (JNIEnv *env, jclass clazz, jlong contextHandle, jlong attributesHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto context = NativeCall::castHandle<ibv_context>(contextHandle);
    auto attributes = NativeCall::castHandle<ibv_cq_init_attr_ex>(attributesHandle);

    auto extendedCompletionQueue = ibv_create_cq_ex(context, attributes);

    NativeCall::setResult(result, extendedCompletionQueue == nullptr ? errno : 0, extendedCompletionQueue);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_extendedCompletionQueueToCompletionQueue (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);

    auto completionQueue = ibv_cq_ex_to_cq(extendedCompletionQueue);

    NativeCall::setResult(result, completionQueue == nullptr ? errno : 0, completionQueue);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_startPoll (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle, jlong attributesHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);
    auto attributes = NativeCall::castHandle<ibv_poll_cq_attr>(attributesHandle);

    auto ret = ibv_start_poll(extendedCompletionQueue, attributes);

    // ENOENT means, that the completion queue is empty. This should not be treated as an error.
    NativeCall::setResult(result, (ret == 0 || ret == ENOENT) ? 0 : ret, ret);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_nextPoll (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);

    auto ret = ibv_next_poll(extendedCompletionQueue);

    // ENOENT means, that the completion queue is empty. This should not be treated as an error.
    NativeCall::setResult(result, (ret == 0 || ret == ENOENT) ? 0 : ret, ret);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_endPoll (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle) {
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);

    ibv_end_poll(extendedCompletionQueue);
}

JNIEXPORT jint JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_readOpCode (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle) {
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);

    return ibv_wc_read_opcode(extendedCompletionQueue);
}

JNIEXPORT jint JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_readVendorError (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle) {
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);

    return ibv_wc_read_vendor_err(extendedCompletionQueue);
}

JNIEXPORT jint JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_readByteCount (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle) {
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);

    return ibv_wc_read_byte_len(extendedCompletionQueue);
}

JNIEXPORT jint JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_readImmediateData (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle) {
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);

    return ibv_wc_read_imm_data(extendedCompletionQueue);
}

JNIEXPORT jint JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_readInvalidatedRemoteKey (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle) {
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);

    return ibv_wc_read_invalidated_rkey(extendedCompletionQueue);
}

JNIEXPORT jint JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_readQueuePairNumber (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle) {
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);

    return ibv_wc_read_qp_num(extendedCompletionQueue);
}

JNIEXPORT jint JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_readSourceQueuePair (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle) {
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);

    return ibv_wc_read_src_qp(extendedCompletionQueue);
}

JNIEXPORT jint JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_readWorkCompletionFlags (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle) {
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);

    return ibv_wc_read_wc_flags(extendedCompletionQueue);
}

JNIEXPORT jint JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_readSourceLocalId (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle) {
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);

    return ibv_wc_read_slid(extendedCompletionQueue);
}

JNIEXPORT jbyte JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_readServiceLevel (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle) {
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);

    return ibv_wc_read_sl(extendedCompletionQueue);
}

JNIEXPORT jbyte JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_readPathBits (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle) {
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);

    return ibv_wc_read_dlid_path_bits(extendedCompletionQueue);
}

JNIEXPORT jlong JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_readCompletionTimestamp (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle) {
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);

    return ibv_wc_read_completion_ts(extendedCompletionQueue);
}

JNIEXPORT jlong JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_readCompletionWallClockNanoseconds (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle) {
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);

    return ibv_wc_read_completion_wallclock_ns(extendedCompletionQueue);
}

JNIEXPORT jshort JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_readCVLan (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle) {
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);

    return ibv_wc_read_cvlan(extendedCompletionQueue);
}

JNIEXPORT jint JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_readFlowTag (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle) {
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);

    return ibv_wc_read_flow_tag(extendedCompletionQueue);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_readTagMatchingInfo (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle, jlong tagMatchingInfoHandle) {
    auto tagMatchingInfo = NativeCall::castHandle<ibv_wc_tm_info>(tagMatchingInfoHandle);
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);

    ibv_wc_read_tm_info(extendedCompletionQueue, tagMatchingInfo);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_benchmarkDummyMethod1 (JNIEnv *env, jclass clazz, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);

    NativeCall::setResult(result, 0, nullptr);
}

JNIEXPORT jlong JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_benchmarkDummyMethod2 (JNIEnv *env, jclass clazz) {
    return 299792458L;
}