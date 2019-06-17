#include <de_hhu_bsinfo_neutrino_struct_Result.h>
#include <cstring>

JNIEXPORT jstring JNICALL Java_de_hhu_bsinfo_neutrino_struct_Result_getErrorMessage (JNIEnv *env, jclass clazz, jint errorNumber) {
    return env->NewStringUTF(strerror(errorNumber));
}