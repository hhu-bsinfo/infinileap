package de.hhu.bsinfo.infinileap.example.util;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkDetails;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkInstruction;
import de.hhu.bsinfo.infinileap.primitive.NativeInteger;
import de.hhu.bsinfo.infinileap.primitive.NativeLong;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.openucx.Communication.ucp_request_check_status;
import static org.openucx.Communication.ucp_request_free;

public class Requests {

    public enum State {
        ERROR, PENDING, COMPLETE
    }

    public static void poll(Worker worker, AtomicBoolean value) {
        while (!value.get()) {
            worker.progress();
        }

        value.set(false);
    }

    public static void await(Worker worker, AtomicBoolean value) throws InterruptedException {
        while (!value.get()) {
            if (worker.progress() == WorkerProgress.IDLE) {
                worker.await();
            }

            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }

        value.set(false);
    }

    public static void poll(Worker worker, CommunicationBarrier barrier) {
        while (!barrier.isReleased()) {
            worker.progress();
        }

        barrier.reset();
    }

    public static void await(Worker worker, CommunicationBarrier barrier) throws InterruptedException {
        while (!barrier.isReleased()) {
            if (worker.progress() == WorkerProgress.IDLE) {
                worker.await();
            }

            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }

        barrier.reset();
    }

    public static void poll(Worker worker, AtomicReference<?> value) {
        while (value.get() == null) {
            worker.progress();
        }
    }

    public static void await(Worker worker, AtomicReference<?> value) throws InterruptedException {
        while (value.get() == null) {
            if (worker.progress() == WorkerProgress.IDLE) {
                worker.await();
            }

            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
    }

    public static void poll(Worker worker, long handle) {
        while (state(handle) != State.COMPLETE) {
            worker.progress();
        }

        release(handle);
    }

    public static void poll(Worker worker, long[] requests) {
        for (int i = 0; i < requests.length; i++) {
            poll(worker, requests[i]);
        }
    }

    public static void await(Worker worker, long handle) throws InterruptedException {
        while (state(handle) != State.COMPLETE) {
            if (worker.progress() == WorkerProgress.IDLE) {
                worker.await();
            }

            if (Thread.interrupted()) {
                worker.cancelRequest(handle);
                throw new InterruptedException();
            }
        }

        release(handle);
    }

    public static void sendOpCode(Worker worker, Endpoint endpoint, BenchmarkInstruction.OpCode opCode) throws InterruptedException {
        try (var instruction = new BenchmarkInstruction(opCode)) {
            Requests.await(
                    worker, endpoint.sendTagged(instruction, Constants.TAG_BENCHMARK_OPCODE)
            );
        }
    }

    public static BenchmarkInstruction.OpCode receiveOpCode(Worker worker) throws InterruptedException {
        try (var instruction = new BenchmarkInstruction()) {
            Requests.await(
                    worker, worker.receiveTagged(instruction, Constants.TAG_BENCHMARK_OPCODE)
            );

            return instruction.opCode();
        }
    }

    public static void sendDetails(Worker worker, Endpoint endpoint, BenchmarkDetails details) throws InterruptedException {
        Requests.await(
                worker, endpoint.sendTagged(details, Constants.TAG_BENCHMARK_DETAILS)
        );
    }

    public static BenchmarkDetails receiveDetails(Worker worker) throws InterruptedException {
        var details = new BenchmarkDetails();
        Requests.await(
                worker, worker.receiveTagged(details, Constants.TAG_BENCHMARK_DETAILS)
        );

        return details;
    }

    public static void sendDescriptor(Worker worker, Endpoint endpoint, MemoryDescriptor descriptor) throws InterruptedException {
        Requests.await(
                worker, endpoint.sendTagged(descriptor, Constants.TAG_BENCHMARK_DESCRIPTOR)
        );
    }

    public static MemoryDescriptor receiveDescriptor(Worker worker) throws InterruptedException {
        var descriptor = new MemoryDescriptor();

        Requests.await(
                worker, worker.receiveTagged(descriptor, Constants.TAG_BENCHMARK_DESCRIPTOR)
        );

        return descriptor;
    }

    public static long get(Endpoint endpoint, MemorySegment buffer,
                              MemoryAddress remoteAddress, RemoteKey remoteKey) {
        return endpoint.get(buffer, remoteAddress, remoteKey);
    }

    public static void blockingGet(Worker worker, Endpoint endpoint, MemorySegment buffer,
                                   MemoryAddress remoteAddress, RemoteKey remoteKey) {
        Requests.poll(
                worker, endpoint.get(buffer, remoteAddress, remoteKey)
        );
    }

    public static long put(Endpoint endpoint, MemorySegment buffer,
                           MemoryAddress remoteAddress, RemoteKey remoteKey) {
        return endpoint.put(buffer, remoteAddress, remoteKey);
    }

    public static void blockingPut(Worker worker, Endpoint endpoint, MemorySegment buffer,
                                   MemoryAddress remoteAddress, RemoteKey remoteKey) {
        Requests.poll(
                worker, endpoint.put(buffer, remoteAddress, remoteKey)
        );
    }

    public static long sendTagged(Endpoint endpoint, MemorySegment buffer) {
        return endpoint.sendTagged(buffer, Constants.TAG_BENCHMARK_MESSAGE);
    }

    public static void blockingSendTagged(Worker worker, Endpoint endpoint, MemorySegment buffer) {
        Requests.poll(
                worker, endpoint.sendTagged(buffer, Constants.TAG_BENCHMARK_MESSAGE)
        );
    }

    public static long receiveTagged(Worker worker, MemorySegment buffer) {
        return worker.receiveTagged(buffer, Constants.TAG_BENCHMARK_MESSAGE);
    }

    public static void blockingReceiveTagged(Worker worker, Endpoint endpoint, MemorySegment buffer) {
        Requests.poll(
                worker, worker.receiveTagged(buffer, Constants.TAG_BENCHMARK_MESSAGE)
        );
    }

    public static long atomic(AtomicOperation op, Endpoint endpoint, NativeInteger nativeInteger,
                                    MemoryAddress remoteAddress, RemoteKey remoteKey, RequestParameters params) {
        return endpoint.atomic(op, nativeInteger, remoteAddress, remoteKey, params);
    }

    public static void blockingAtomic(AtomicOperation op, Worker worker, Endpoint endpoint, NativeInteger nativeInteger,
                                      MemoryAddress remoteAddress, RemoteKey remoteKey, RequestParameters params) {
        Requests.poll(
                worker, endpoint.atomic(op, nativeInteger, remoteAddress, remoteKey, params)
        );
    }

    public static long atomic(AtomicOperation op, Endpoint endpoint, NativeLong nativeLong,
                                    MemoryAddress remoteAddress, RemoteKey remoteKey, RequestParameters params) {
        return endpoint.atomic(op, nativeLong, remoteAddress, remoteKey, params);
    }

    public static void blockingAtomic(AtomicOperation op, Worker worker, Endpoint endpoint, NativeLong nativeLong,
                                      MemoryAddress remoteAddress, RemoteKey remoteKey, RequestParameters params) {
        Requests.poll(
                worker, endpoint.atomic(op, nativeLong, remoteAddress, remoteKey, params)
        );
    }

    public static State state(long handle) {
        if (Status.isError(handle)) {
            return State.ERROR;
        }

        if (Status.is(handle, Status.OK)) {
            return State.COMPLETE;
        }

        if (Status.is(ucp_request_check_status(handle), Status.IN_PROGRESS)) {
            return State.PENDING;
        }

        return State.COMPLETE;
    }

    public static void release(long handle) {
        if (!Status.isStatus(handle)) {
            ucp_request_free(handle);
        }
    }
}
