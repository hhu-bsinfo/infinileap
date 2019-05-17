#include <de_hhu_bsinfo_neutrino_util_MemoryUtil.h>
#include <neutrino/NativeCall.hpp>

JNIEXPORT jobject JNICALL Java_de_hhu_bsinfo_neutrino_util_MemoryUtil_wrap (JNIEnv *env, jclass clazz, jlong handle, jint size) {
    return env->NewDirectByteBuffer(reinterpret_cast<void*>(handle), size);
}

JNIEXPORT jlong JNICALL Java_de_hhu_bsinfo_neutrino_util_MemoryUtil_getAddress (JNIEnv *env, jclass clazz, jobject byteBuffer) {
    return reinterpret_cast<jlong>(env->GetDirectBufferAddress(byteBuffer));
}

JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_util_MemoryUtil_free (JNIEnv *env, jclass clazz, jlong handle) {
    delete NativeCall::castHandle<long>(handle);
}