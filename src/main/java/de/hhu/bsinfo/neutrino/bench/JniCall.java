package de.hhu.bsinfo.neutrino.bench;

import org.agrona.concurrent.AtomicBuffer;

import java.nio.ByteBuffer;

public class JniCall {

    public static native byte checkSumByteArrayGet(byte[] array);

    public static native byte checkSumByteArrayGetCritical(byte[] array);

    public static native byte checkSumByteArrayJavaCritical(byte[] array);

    public static native byte checkSumDirectByteBuffer(ByteBuffer byteBuffer);

    public static native byte checkSumHeapByteBuffer(ByteBuffer byteBuffer);

    private static native byte checkSumLocalBuffer(long handle, long capacity);
    public static byte checkSumLocalBuffer(AtomicBuffer buffer) {
        return checkSumLocalBuffer(buffer.addressOffset(), buffer.capacity());
    }


    public static native ComplexNumber addComplex(ComplexNumber a, ComplexNumber b);

    private static native NativeComplexNumber addNativeComplex(long a, long b);

    private static native void addNativeComplex(long a, long b, long result);

    public static NativeComplexNumber addNativeComplex(NativeComplexNumber a, NativeComplexNumber b) {
        return addNativeComplex(a.getHandle(), b.getHandle());
    }

    public static void addNativeComplex(NativeComplexNumber a, NativeComplexNumber b, NativeComplexNumber result) {
        addNativeComplex(a.getHandle(), b.getHandle(), result.getHandle());
    }
}
