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

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_createCompletionQueue (JNIEnv *env, jclass clazz, jlong contextHandle, jint maxElements, jlong userContextHandle, jlong completionChannelHandle, jint completionVector, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto context = NativeCall::castHandle<ibv_context>(contextHandle);
    auto userContext = NativeCall::castHandle<void>(userContextHandle);
    auto completionChannel = NativeCall::castHandle<ibv_comp_channel>(completionChannelHandle);

    auto completionQueue = ibv_create_cq(context, maxElements, userContext, completionChannel, completionVector);

    NativeCall::setResult(result, completionQueue == nullptr ? errno : 0, completionQueue);
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_verbs_Verbs_destroyCompletionQueue (JNIEnv *env, jclass clazz, jlong completionQueueHandle, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto completionQueue = NativeCall::castHandle<ibv_cq>(completionQueueHandle);

    NativeCall::setResult(result, ibv_destroy_cq(completionQueue), nullptr);
}