#include <de_hhu_bsinfo_neutrino_util_SystemUtil.h>
#include <cstring>
#include <errno.h>

JNIEXPORT jstring JNICALL Java_de_hhu_bsinfo_neutrino_util_SystemUtil_getErrorMessage0 (JNIEnv *env, jclass clazz) {
    auto message = strerror(errno);
    return env->NewStringUTF(message);
}