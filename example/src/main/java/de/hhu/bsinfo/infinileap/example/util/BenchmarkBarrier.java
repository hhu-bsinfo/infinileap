package de.hhu.bsinfo.infinileap.example.util;

import de.hhu.bsinfo.infinileap.binding.Endpoint;
import de.hhu.bsinfo.infinileap.binding.Worker;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkInstruction;

public class BenchmarkBarrier {

    public static void signal(Worker worker, Endpoint endpoint) throws InterruptedException {
        Requests.sendOpCode(worker, endpoint, BenchmarkInstruction.OpCode.RELEASE_BARRIER);
    }

    public static void await(Worker worker) throws InterruptedException {
        var opCode = Requests.receiveOpCode(worker);
        if (opCode != BenchmarkInstruction.OpCode.RELEASE_BARRIER) {
            throw new IllegalStateException("Received unexpected op code " + opCode.name());
        };
    }

}
