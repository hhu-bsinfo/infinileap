#include <de_hhu_bsinfo_neutrino_data_MemoryUtil.h>

JNIEXPORT jobject JNICALL Java_de_hhu_bsinfo_neutrino_data_MemoryUtil_wrap (JNIEnv *env, jclass clazz, jlong handle, jint size) {
    return env->NewDirectByteBuffer(reinterpret_cast<void*>(handle), size);
}

JNIEXPORT jlong JNICALL Java_de_hhu_bsinfo_neutrino_data_MemoryUtil_getAddress (JNIEnv *env, jclass clazz, jobject byteBuffer) {
    return reinterpret_cast<jlong>(env->GetDirectBufferAddress(byteBuffer));
}
