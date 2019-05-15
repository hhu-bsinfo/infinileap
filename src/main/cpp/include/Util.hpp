#include <jni.h>

#ifndef NEUTRINO_UTIL_HPP
#define NEUTRINO_UTIL_HPP

struct Result {
    int status;
    long handle;
} __attribute__ ((packed));

struct MemberInfo {
    char name[32];
    int offset;
} __attribute__ ((packed));

struct StructInfo {
    int structSize;
    int memberCount;
    MemberInfo *memberInfos;
} __attribute__ ((packed));

template<typename T> T* castHandle(jlong handle) {
    return reinterpret_cast<T*>(handle);
}

#endif //NEUTRINO_UTIL_HPP
