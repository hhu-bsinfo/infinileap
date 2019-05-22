#include "neutrino/NativeCall.hpp"

void NativeCall::setResult(NativeCall::Result *result, int status, void *handle) {
    result->status = status;
    result->value = reinterpret_cast<long>(handle);
}
void NativeCall::setResult(NativeCall::Result *result, int status, int value) {
    result->status = status;
    result->value = value;
}
