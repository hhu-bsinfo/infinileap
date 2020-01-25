#include <de_hhu_bsinfo_neutrino_util_Epoll.h>
#include <sys/epoll.h>
#include <neutrino/NativeCall.hpp>

JNIEXPORT jint JNICALL Java_de_hhu_bsinfo_neutrino_util_Epoll_create (JNIEnv *env, jclass clazz, jint size) {
    return epoll_create(size);
}

JNIEXPORT jint JNICALL Java_de_hhu_bsinfo_neutrino_util_Epoll_control (JNIEnv *env, jclass clazz, jint epfd, jint op, jint fd, jlong events) {
    return epoll_ctl(epfd, op, fd, NativeCall::castHandle<epoll_event>(events));
}

JNIEXPORT jint JNICALL Java_de_hhu_bsinfo_neutrino_util_Epoll_wait (JNIEnv *env, jclass clazz, jint epfd, jlong events, jint maxEvents, jint timeout) {
    return epoll_wait(epfd, NativeCall::castHandle<epoll_event>(events), maxEvents, timeout);
}