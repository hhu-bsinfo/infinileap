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

public class Requests {

    private static final RequestParameters REQUEST_PARAMETERS_ATOMIC_32 = new RequestParameters()
            .setDataType(DataType.CONTIGUOUS_32_BIT);

    private static final RequestParameters REQUEST_PARAMETERS_ATOMIC_64 = new RequestParameters()
            .setDataType(DataType.CONTIGUOUS_64_BIT);

    public static void poll(Worker worker, AtomicBoolean value) {
        while (!value.get()) {
            worker.progress();
        }

        value.set(false);
    }

    public static void await(Worker worker, AtomicBoolean value) {
        while (!value.get()) {
            if (worker.progress() == WorkerProgress.IDLE) {
                worker.await();
            };
        }

        value.set(false);
    }

    public static void poll(Worker worker, CommunicationBarrier barrier) {
        while (!barrier.isReleased()) {
            worker.progress();
        }

        barrier.reset();
    }

    public static void await(Worker worker, CommunicationBarrier barrier) {
        while (!barrier.isReleased()) {
            if (worker.progress() == WorkerProgress.IDLE) {
                worker.await();
            };
        }

        barrier.reset();
    }

    public static void poll(Worker worker, AtomicReference<?> value) {
        while (value.get() == null) {
            worker.progress();
        }
    }

    public static void await(Worker worker, AtomicReference<?> value) {
        while (value.get() == null) {
            if (worker.progress() == WorkerProgress.IDLE) {
                worker.await();
            };
        }
    }

    public static void poll(Worker worker, Request request) {
        while (request.state() != Request.State.COMPLETE) {
            worker.progress();
        }

        request.release();
    }

    public static void await(Worker worker, Request request) {
        while (request.state() != Request.State.COMPLETE) {
            if (worker.progress() == WorkerProgress.IDLE) {
                worker.await();
            }
        }

        request.release();
    }

    public static void sendOpCode(Worker worker, Endpoint endpoint, BenchmarkInstruction.OpCode opCode) {
        try (var instruction = new BenchmarkInstruction(opCode)) {
            Requests.await(
                    worker, endpoint.sendTagged(instruction, Constants.TAG_BENCHMARK_OPCODE)
            );
        }
    }

    public static BenchmarkInstruction.OpCode receiveOpCode(Worker worker) {
        try (var instruction = new BenchmarkInstruction()) {
            Requests.await(
                    worker, worker.receiveTagged(instruction, Constants.TAG_BENCHMARK_OPCODE)
            );

            return instruction.opCode();
        }
    }

    public static void sendDetails(Worker worker, Endpoint endpoint, BenchmarkDetails details) {
        Requests.await(
                worker, endpoint.sendTagged(details, Constants.TAG_BENCHMARK_DETAILS)
        );
    }

    public static BenchmarkDetails receiveDetails(Worker worker) {
        var details = new BenchmarkDetails();
        Requests.await(
                worker, worker.receiveTagged(details, Constants.TAG_BENCHMARK_DETAILS)
        );

        return details;
    }

    public static void sendDescriptor(Worker worker, Endpoint endpoint, MemoryDescriptor descriptor) {
        Requests.await(
                worker, endpoint.sendTagged(descriptor, Constants.TAG_BENCHMARK_DESCRIPTOR)
        );
    }

    public static MemoryDescriptor receiveDescriptor(Worker worker) {
        var descriptor = new MemoryDescriptor();

        Requests.await(
                worker, worker.receiveTagged(descriptor, Constants.TAG_BENCHMARK_DESCRIPTOR)
        );

        return descriptor;
    }

    public static void blockingGet(Worker worker, Endpoint endpoint, MemorySegment buffer,
                                   MemoryAddress remoteAddress, RemoteKey remoteKey) {
        Requests.poll(
                worker, endpoint.get(buffer, remoteAddress, remoteKey)
        );
    }

    public static void blockingPut(Worker worker, Endpoint endpoint, MemorySegment buffer,
                                   MemoryAddress remoteAddress, RemoteKey remoteKey) {
        Requests.poll(
                worker, endpoint.put(buffer, remoteAddress, remoteKey)
        );
    }

    public static void blockingSendTagged(Worker worker, Endpoint endpoint, MemorySegment buffer) {
        Requests.poll(
                worker, endpoint.sendTagged(buffer, Constants.TAG_BENCHMARK_MESSAGE)
        );
    }

    public static void blockingReceiveTagged(Worker worker, Endpoint endpoint, MemorySegment buffer) {
        Requests.poll(
                worker, worker.receiveTagged(buffer, Constants.TAG_BENCHMARK_MESSAGE)
        );
    }

    public static void blockingAtomicAdd(Worker worker, Endpoint endpoint, NativeInteger nativeInteger,
                                         MemoryAddress remoteAddress, RemoteKey remoteKey) {
        Requests.poll(
                worker, endpoint.atomic(AtomicOperation.ADD, nativeInteger, remoteAddress, remoteKey, REQUEST_PARAMETERS_ATOMIC_32)
        );
    }

    public static void blockingAtomicAdd(Worker worker, Endpoint endpoint, NativeLong nativeLong,
                                         MemoryAddress remoteAddress, RemoteKey remoteKey) {
        Requests.poll(
                worker, endpoint.atomic(AtomicOperation.ADD, nativeLong, remoteAddress, remoteKey, REQUEST_PARAMETERS_ATOMIC_64)
        );
    }
}
