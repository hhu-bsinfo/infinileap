#include <de_hhu_bsinfo_neutrino_util_FileDescriptor.h>
#include <unistd.h>

JNIEXPORT jint JNICALL Java_de_hhu_bsinfo_neutrino_util_FileDescriptor_close0 (JNIEnv *env, jclass clazz, jint fd) {
    return close(fd);
}