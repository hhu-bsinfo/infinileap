#include <de_hhu_bsinfo_neutrino_util_EventFileDescriptor.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <string.h>

#include <sys/eventfd.h>

JNIEXPORT jint JNICALL Java_de_hhu_bsinfo_neutrino_util_EventFileDescriptor_create0 (JNIEnv *env, jclass clazz, jint counter, jint flags) {
    return eventfd(counter, flags);
}

JNIEXPORT jlong JNICALL Java_de_hhu_bsinfo_neutrino_util_EventFileDescriptor_read0 (JNIEnv *env, jclass clazz, jint handle) {
    eventfd_t value;

    // TODO(krakowski):
    //  Check for errors and throw java exception if read failed
    eventfd_read(handle, &value);
    return value;
}

JNIEXPORT jint JNICALL Java_de_hhu_bsinfo_neutrino_util_EventFileDescriptor_increment0 (JNIEnv *env, jclass clazz, jint handle, jlong value) {
    return eventfd_write(handle, value);
}

JNIEXPORT jint JNICALL Java_de_hhu_bsinfo_neutrino_util_EventFileDescriptor_close0 (JNIEnv *env, jclass clazz, jint handle) {
    return close(handle);
}