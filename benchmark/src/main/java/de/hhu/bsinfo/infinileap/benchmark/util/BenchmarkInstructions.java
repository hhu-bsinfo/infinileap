package de.hhu.bsinfo.infinileap.benchmark.util;

import de.hhu.bsinfo.infinileap.binding.Endpoint;
import de.hhu.bsinfo.infinileap.binding.MemoryDescriptor;
import de.hhu.bsinfo.infinileap.binding.Worker;
import de.hhu.bsinfo.infinileap.util.Requests;
import de.hhu.bsinfo.infinileap.benchmark.message.BenchmarkDetails;
import de.hhu.bsinfo.infinileap.benchmark.message.BenchmarkInstruction;
import jdk.incubator.foreign.MemorySegment;

public class BenchmarkInstructions {

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

    public static void sendOpCode(Worker worker, Endpoint endpoint, BenchmarkInstruction.OpCode opCode) throws InterruptedException {
        var instruction = new BenchmarkInstruction(opCode);
        Requests.await(
                worker, endpoint.sendTagged(instruction, Constants.TAG_BENCHMARK_OPCODE)
        );
    }

    public static BenchmarkInstruction.OpCode receiveOpCode(Worker worker) throws InterruptedException {
        var instruction = new BenchmarkInstruction();
        Requests.await(
                worker, worker.receiveTagged(instruction, Constants.TAG_BENCHMARK_OPCODE)
        );

        return instruction.opCode();
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
}
