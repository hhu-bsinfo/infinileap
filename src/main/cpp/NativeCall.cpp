#include "neutrino/NativeCall.hpp"

void NativeCall::setResult(NativeCall::Result *result, int status, void *handle) {
    result->status = status;
    result->handle = reinterpret_cast<long>(handle);
}
