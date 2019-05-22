#include <jni.h>

#ifndef RDMA_NATIVECALL_HPP
#define RDMA_NATIVECALL_HPP

class NativeCall {

public:

    struct Result {
        int status;
        long value;
    } __attribute__ ((packed));

    NativeCall() = delete;

    template<typename T> static T* castHandle(jlong handle) {
        return reinterpret_cast<T*>(handle);
    }

    static void setResult(Result* result, int status, void* handle);

    static void setResult(Result* result, int status, int value);
};

#endif //RDMA_NATIVECALL_HPP
