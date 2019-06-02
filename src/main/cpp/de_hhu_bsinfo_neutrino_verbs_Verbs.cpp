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
    auto context = NativeCall::castHandle<ibv_context>(contextHandle);
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);

    NativeCall::setResult(result, ibv_close_device(context), nullptr);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_queryDevice (JNIEnv *env, jclass clazz, jlong contextHandle, jlong deviceHandle, jlong resultHandle) {
    auto context = NativeCall::castHandle<ibv_context>(contextHandle);
    auto device = NativeCall::castHandle<ibv_device_attr>(deviceHandle);
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);

    NativeCall::setResult(result, ibv_query_device(context, device), nullptr);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_queryPort (JNIEnv *env, jclass clazz, jlong contextHandle, jlong portHandle, jint portNumber, jlong resultHandle) {
    auto context = NativeCall::castHandle<ibv_context>(contextHandle);
    auto port = NativeCall::castHandle<ibv_port_attr>(portHandle);
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);

    NativeCall::setResult(result, ibv_query_port(context, portNumber, port), nullptr);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_getAsyncEvent (JNIEnv *env, jclass clazz, jlong contextHandle, jlong asyncEventHandle, jlong resultHandle) {
    auto context = NativeCall::castHandle<ibv_context>(contextHandle);
    auto asyncEvent = NativeCall::castHandle<ibv_async_event>(asyncEventHandle);
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);

    NativeCall::setResult(result, ibv_get_async_event(context, asyncEvent), nullptr);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_acknowledgeAsyncEvent (JNIEnv *env, jclass clazz, jlong asyncEventHandle) {
    auto asyncEvent = NativeCall::castHandle<ibv_async_event>(asyncEventHandle);

    ibv_ack_async_event(asyncEvent);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_allocateProtectionDomain (JNIEnv *env, jclass clazz, jlong contextHandle, jlong resultHandle) {
    auto context = NativeCall::castHandle<ibv_context>(contextHandle);
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);

    auto protectionDomain = ibv_alloc_pd(context);

    NativeCall::setResult(result, protectionDomain == nullptr ? 1 : 0, protectionDomain);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_deallocateProtectionDomain (JNIEnv *env, jclass clazz, jlong protectionDomainHandle, jlong resultHandle) {
    auto protectionDomain = NativeCall::castHandle<ibv_pd>(protectionDomainHandle);
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);

    NativeCall::setResult(result, ibv_dealloc_pd(protectionDomain), nullptr);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_registerMemoryRegion (JNIEnv *env, jclass clazz, jlong protectionDomainHandle, jlong address, jlong size, jint accessFlags, jlong resultHandle) {
    auto protectionDomain = NativeCall::castHandle<ibv_pd>(protectionDomainHandle);
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto bufferAddress = NativeCall::castHandle<void>(address);

    auto memoryRegion = ibv_reg_mr(protectionDomain, bufferAddress, size, accessFlags);

    NativeCall::setResult(result, memoryRegion == nullptr ? errno : 0, memoryRegion);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_deregisterMemoryRegion (JNIEnv *env, jclass clazz, jlong memoryRegionHandle, jlong resultHandle) {
    auto memoryRegion = NativeCall::castHandle<ibv_mr>(memoryRegionHandle);
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);

    NativeCall::setResult(result, ibv_dereg_mr(memoryRegion), nullptr);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_createAddressHandle (JNIEnv *env, jclass clazz, jlong protectionDomainHandle, jlong addressHandleAttributesHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto protectionDomain = NativeCall::castHandle<ibv_pd>(protectionDomainHandle);
    auto addressHandleAttributes = NativeCall::castHandle<ibv_ah_attr>(addressHandleAttributesHandle);

    auto addressHandle = ibv_create_ah(protectionDomain, addressHandleAttributes);

    NativeCall::setResult(result, addressHandle == nullptr ? errno : 0, addressHandle);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_destroyAddressHandle (JNIEnv *env, jclass clazz, jlong addressHandleHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto addressHandle = NativeCall::castHandle<ibv_ah>(addressHandleHandle);

    NativeCall::setResult(result, ibv_destroy_ah(addressHandle), nullptr);
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

    NativeCall::setResult(result, ibv_destroy_comp_channel(channel), nullptr);
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

    NativeCall::setResult(result, workRequestCount < 0 ? 1 : 0, workRequestCount);
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

    NativeCall::setResult(result, ibv_destroy_cq(completionQueue), nullptr);
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

    NativeCall::setResult(result, sharedReceiveQueue == nullptr ? 1 : 0, sharedReceiveQueue);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_createQueuePair (JNIEnv *env, jclass clazz, jlong protectionDomainHandle, jlong attributesHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto attributes = NativeCall::castHandle<ibv_qp_init_attr>(attributesHandle);
    auto protectionDomain = NativeCall::castHandle<ibv_pd>(protectionDomainHandle);

    auto queuePair = ibv_create_qp(protectionDomain, attributes);

    NativeCall::setResult(result, queuePair == nullptr ? 1 : 0, queuePair);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_modifyQueuePair (JNIEnv *env, jclass clazz, jlong queuePairHandle, jlong attributesHandle, jint attributesMask, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto attributes = NativeCall::castHandle<ibv_qp_attr>(attributesHandle);
    auto queuePair = NativeCall::castHandle<ibv_qp>(queuePairHandle);

    NativeCall::setResult(result, ibv_modify_qp(queuePair, attributes, attributesMask), nullptr);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_queryQueuePair (JNIEnv *env, jclass clazz, jlong queuePairHandle, jlong attributesHandle, jint attributesMask, jlong initialAttributesHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto attributes = NativeCall::castHandle<ibv_qp_attr>(attributesHandle);
    auto initialAttributes = NativeCall::castHandle<ibv_qp_init_attr>(initialAttributesHandle);
    auto queuePair = NativeCall::castHandle<ibv_qp>(queuePairHandle);

    NativeCall::setResult(result, ibv_query_qp(queuePair, attributes, attributesMask, initialAttributes), nullptr);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_destroyQueuePair (JNIEnv *env, jclass clazz, jlong queuePairHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto queuePair = NativeCall::castHandle<ibv_qp>(queuePairHandle);

    NativeCall::setResult(result, ibv_destroy_qp(queuePair), nullptr);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_createExtendedCompletionQueue (JNIEnv *env, jclass clazz, jlong contextHandle, jlong initialAttributesHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto context = NativeCall::castHandle<ibv_context>(contextHandle);
    auto initialAttributes = NativeCall::castHandle<ibv_cq_init_attr_ex>(initialAttributesHandle);

    auto extendedCompletionQueue = ibv_create_cq_ex(context, initialAttributes);

    NativeCall::setResult(result, extendedCompletionQueue == nullptr ? errno : 0, extendedCompletionQueue);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_extendedCompletionQueueToCompletionQueue (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);

    auto completionQueue = ibv_cq_ex_to_cq(extendedCompletionQueue);

    NativeCall::setResult(result, completionQueue == nullptr ? errno : 0, completionQueue);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_startPoll (JNIEnv *env, jclass clazz, jlong extendedCompletionQueueHandle, jlong pollCompletionQueueAttributesHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto extendedCompletionQueue = NativeCall::castHandle<ibv_cq_ex>(extendedCompletionQueueHandle);
    auto pollCompletionQueueAttributes = NativeCall::castHandle<ibv_poll_cq_attr>(pollCompletionQueueAttributesHandle);

    auto ret = ibv_start_poll(extendedCompletionQueue, pollCompletionQueueAttributes);

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