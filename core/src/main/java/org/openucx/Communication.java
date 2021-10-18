package org.openucx;

import jdk.incubator.foreign.*;

import java.lang.invoke.MethodHandle;

import static jdk.incubator.foreign.CLinker.*;

public class Communication {

    static {
        System.loadLibrary("ucp");
    }
    
    private static final ValueLayout REQUEST_HANDLE = ValueLayout.JAVA_LONG;

    // -------- UCP GET ---------- //

    private static final FunctionDescriptor ucp_get_nbx$FUNC = FunctionDescriptor.of(REQUEST_HANDLE,
            ValueLayout.ADDRESS,
            ValueLayout.ADDRESS,
            ValueLayout.JAVA_LONG,
            ValueLayout.JAVA_LONG,
            ValueLayout.ADDRESS,
            ValueLayout.ADDRESS
    );

    private static final MethodHandle ucp_get_nbx$MH = RuntimeHelper.downcallHandle(
            "ucp_get_nbx", ucp_get_nbx$FUNC, false
    );

    public static long ucp_get_nbx ( Addressable ep,  Addressable buffer,  long count,  long remote_addr,  Addressable rkey,  Addressable param) {
        try {
            return (long) ucp_get_nbx$MH.invokeExact(ep.address(), buffer.address(), count, remote_addr, rkey.address(), param.address());
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    // -------- UCP PUT ---------- //

    private static final FunctionDescriptor ucp_put_nbx$FUNC = FunctionDescriptor.of(REQUEST_HANDLE,
            ValueLayout.ADDRESS,
            ValueLayout.ADDRESS,
            ValueLayout.JAVA_LONG,
            ValueLayout.JAVA_LONG,
            ValueLayout.ADDRESS,
            ValueLayout.ADDRESS
    );

    static final MethodHandle ucp_put_nbx$MH = RuntimeHelper.downcallHandle(
            "ucp_put_nbx", ucp_put_nbx$FUNC, false
    );

    public static long ucp_put_nbx (Addressable ep, Addressable buffer, long count, long remote_addr, Addressable rkey, Addressable param) {
        try {
            return (long) ucp_put_nbx$MH.invokeExact(ep.address(), buffer.address(), count, remote_addr, rkey.address(), param.address());
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    // -------- UCP TAG SEND ---------- //

    private static final FunctionDescriptor ucp_tag_send_nbx$FUNC = FunctionDescriptor.of(REQUEST_HANDLE,
            ValueLayout.ADDRESS,
            ValueLayout.ADDRESS,
            ValueLayout.JAVA_LONG,
            ValueLayout.JAVA_LONG,
            ValueLayout.ADDRESS
    );

    private static final MethodHandle ucp_tag_send_nbx$MH = RuntimeHelper.downcallHandle(
            "ucp_tag_send_nbx", ucp_tag_send_nbx$FUNC, false
    );

    public static long ucp_tag_send_nbx ( Addressable ep,  Addressable buffer,  long count,  long tag,  Addressable param) {
        try {
            return (long) ucp_tag_send_nbx$MH.invokeExact(ep.address(), buffer.address(), count, tag, param.address());
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    // -------- UCP TAG RECEIVE ---------- //

    private static final FunctionDescriptor ucp_tag_recv_nbx$FUNC = FunctionDescriptor.of(REQUEST_HANDLE,
            ValueLayout.ADDRESS,
            ValueLayout.ADDRESS,
            ValueLayout.JAVA_LONG,
            ValueLayout.JAVA_LONG,
            ValueLayout.JAVA_LONG,
            ValueLayout.ADDRESS
    );

    private static final MethodHandle ucp_tag_recv_nbx$MH = RuntimeHelper.downcallHandle(
            "ucp_tag_recv_nbx", ucp_tag_recv_nbx$FUNC, false
    );

    public static long ucp_tag_recv_nbx ( Addressable worker,  Addressable buffer,  long count,  long tag,  long tag_mask,  Addressable param) {
        try {
            return (long) ucp_tag_recv_nbx$MH.invokeExact(worker.address(), buffer.address(), count, tag, tag_mask, param.address());
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    // -------- UCP STREAM RECEIVE ---------- //

    private static final FunctionDescriptor ucp_stream_send_nbx$FUNC = FunctionDescriptor.of(REQUEST_HANDLE,
            ValueLayout.ADDRESS,
            ValueLayout.ADDRESS,
            ValueLayout.JAVA_LONG,
            ValueLayout.ADDRESS
    );

    private static final MethodHandle ucp_stream_send_nbx$MH = RuntimeHelper.downcallHandle(
            "ucp_stream_send_nbx", ucp_stream_send_nbx$FUNC, false
    );

    public static long ucp_stream_send_nbx ( Addressable ep,  Addressable buffer,  long count,  Addressable param) {
        try {
            return (long) ucp_stream_send_nbx$MH.invokeExact(ep.address(), buffer.address(), count, param.address());
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    // -------- UCP STREAM RECEIVE ---------- //

    private static final FunctionDescriptor ucp_stream_recv_nbx$FUNC = FunctionDescriptor.of(REQUEST_HANDLE,
            ValueLayout.ADDRESS,
            ValueLayout.ADDRESS,
            ValueLayout.JAVA_LONG,
            ValueLayout.ADDRESS,
            ValueLayout.ADDRESS
    );

    private static final MethodHandle ucp_stream_recv_nbx$MH = RuntimeHelper.downcallHandle(
            "ucp_stream_recv_nbx", ucp_stream_recv_nbx$FUNC, false
    );

    public static long ucp_stream_recv_nbx ( Addressable ep,  Addressable buffer,  long count,  Addressable length,  Addressable param) {
        try {
            return (long) ucp_stream_recv_nbx$MH.invokeExact(ep.address(), buffer.address(), count, length.address(), param.address());
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    // -------- UCP ATOMIC OP ---------- //

    private static final FunctionDescriptor ucp_atomic_op_nbx$FUNC = FunctionDescriptor.of(REQUEST_HANDLE,
            ValueLayout.ADDRESS,
            ValueLayout.JAVA_INT,
            ValueLayout.ADDRESS,
            ValueLayout.JAVA_LONG,
            ValueLayout.JAVA_LONG,
            ValueLayout.ADDRESS,
            ValueLayout.ADDRESS
    );

    private static final MethodHandle ucp_atomic_op_nbx$MH = RuntimeHelper.downcallHandle(
            "ucp_atomic_op_nbx", ucp_atomic_op_nbx$FUNC, false
    );

    public static long ucp_atomic_op_nbx ( Addressable ep,  int opcode,  Addressable buffer,  long count,  long remote_addr,  Addressable rkey,  Addressable param) {
        try {
            return (long) ucp_atomic_op_nbx$MH.invokeExact(ep.address(), opcode, buffer.address(), count, remote_addr, rkey.address(), param.address());
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    // ----------- UCP AM SEND ------------- //

    private static final FunctionDescriptor ucp_am_send_nbx$FUNC = FunctionDescriptor.of(REQUEST_HANDLE,
            ValueLayout.ADDRESS,
            ValueLayout.JAVA_INT,
            ValueLayout.ADDRESS,
            ValueLayout.JAVA_LONG,
            ValueLayout.ADDRESS,
            ValueLayout.JAVA_LONG,
            ValueLayout.ADDRESS
    );

    private static final MethodHandle ucp_am_send_nbx$MH = RuntimeHelper.downcallHandle(
            "ucp_am_send_nbx", ucp_am_send_nbx$FUNC, false
    );

    public static long ucp_am_send_nbx ( Addressable ep,  int id,  Addressable header,  long header_length,  Addressable buffer,  long count,  Addressable param) {
        try {
            return (long) ucp_am_send_nbx$MH.invokeExact(ep.address(), id, header.address(), header_length, buffer.address(), count, param.address());
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    // -------- UCP WORKER FLUSH ---------- //

    private static final FunctionDescriptor ucp_worker_flush_nbx$FUNC = FunctionDescriptor.of(REQUEST_HANDLE,
            ValueLayout.ADDRESS,
            ValueLayout.ADDRESS
    );

    private static final MethodHandle ucp_worker_flush_nbx$MH = RuntimeHelper.downcallHandle(
            "ucp_worker_flush_nbx", ucp_worker_flush_nbx$FUNC, false
    );

    public static long ucp_worker_flush_nbx ( Addressable worker,  Addressable param) {
        try {
            return (long) ucp_worker_flush_nbx$MH.invokeExact(worker.address(), param.address());
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    // -------- UCP WORKER FLUSH ---------- //

    private static final FunctionDescriptor ucp_ep_flush_nbx$FUNC = FunctionDescriptor.of(REQUEST_HANDLE,
            ValueLayout.ADDRESS,
            ValueLayout.ADDRESS
    );

    private static final MethodHandle ucp_ep_flush_nbx$MH = RuntimeHelper.downcallHandle(
            "ucp_ep_flush_nbx", ucp_ep_flush_nbx$FUNC, false
    );

    public static long ucp_ep_flush_nbx ( Addressable ep,  Addressable param) {
        try {
            return (long) ucp_ep_flush_nbx$MH.invokeExact(ep.address(), param.address());
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    // -------- UCP REQUEST CANCEL ---------- //

    private static final FunctionDescriptor ucp_request_cancel$FUNC = FunctionDescriptor.ofVoid(
            ValueLayout.ADDRESS,
            REQUEST_HANDLE
    );

    private static final MethodHandle ucp_request_cancel$MH = RuntimeHelper.downcallHandle(
            "ucp_request_cancel", ucp_request_cancel$FUNC, false
    );

    public static void ucp_request_cancel ( Addressable worker,  long request) {
        try {
            ucp_request_cancel$MH.invokeExact(worker.address(), request);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    // -------- UCP REQUEST FREE ---------- //

    private static final FunctionDescriptor ucp_request_free$FUNC = FunctionDescriptor.ofVoid(
            REQUEST_HANDLE
    );

    private static final MethodHandle ucp_request_free$MH = RuntimeHelper.downcallHandle(
            "ucp_request_free", ucp_request_free$FUNC, false
    );

    public static void ucp_request_free ( long request) {
        try {
            ucp_request_free$MH.invokeExact(request);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    // -------- UCP REQUEST CHECK STATUS ---------- //

    private static final FunctionDescriptor ucp_request_check_status$FUNC = FunctionDescriptor.of(ValueLayout.JAVA_CHAR,
            REQUEST_HANDLE
    );

    private static final MethodHandle ucp_request_check_status$MH = RuntimeHelper.downcallHandle(
            "ucp_request_check_status", ucp_request_check_status$FUNC, false
    );

    public static byte ucp_request_check_status ( long request) {
        try {
            return (byte) ucp_request_check_status$MH.invokeExact(request);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
}
