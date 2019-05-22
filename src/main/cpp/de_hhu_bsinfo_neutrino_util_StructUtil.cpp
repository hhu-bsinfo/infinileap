#include <de_hhu_bsinfo_neutrino_util_StructUtil.h>
#include <infiniband/verbs.h>
#include <neutrino/ReflectionUtility.hpp>
#include <neutrino/NativeCall.hpp>


JNIEXPORT void JNICALL Java_de_hhu_bsinfo_neutrino_util_StructUtil_getStructInformation (JNIEnv *env, jclass clazz, jstring identifier, jlong resultHandle) {
    auto result = NativeCall::castHandle<NativeCall::Result>(resultHandle);
    auto structName = env->GetStringUTFChars(identifier, nullptr);

    auto structInfo = ReflectionUtility::getStructInfo(structName);

    env->ReleaseStringUTFChars(identifier, structName);

    result->status = structInfo == nullptr ? 1 : 0;
    result->value = reinterpret_cast<long>(structInfo);
}


