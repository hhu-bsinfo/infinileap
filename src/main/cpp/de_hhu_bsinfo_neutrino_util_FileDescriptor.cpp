#include <de_hhu_bsinfo_neutrino_util_FileDescriptor.h>
#include <unistd.h>
#include <fcntl.h>

JNIEXPORT jint JNICALL Java_de_hhu_bsinfo_neutrino_util_FileDescriptor_close0 (JNIEnv *env, jclass clazz, jint fd) {
    return close(fd);
}

JNIEXPORT jint JNICALL Java_de_hhu_bsinfo_neutrino_util_FileDescriptor_setMode0 (JNIEnv *env, jclass clazz, jint fd, jint mode) {
    auto flags =  fcntl(fd, F_GETFL);
    return fcntl(fd, F_SETFL, flags | mode);
}