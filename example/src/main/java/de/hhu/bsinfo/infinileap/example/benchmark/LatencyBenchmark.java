package de.hhu.bsinfo.infinileap.example.benchmark;

import de.hhu.bsinfo.infinileap.example.benchmark.context.AtomicIntegerContext;
import de.hhu.bsinfo.infinileap.example.benchmark.context.MemoryContext;
import de.hhu.bsinfo.infinileap.example.benchmark.context.MessagingContext;
import de.hhu.bsinfo.infinileap.example.benchmark.context.PingPongContext;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@Slf4j
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class LatencyBenchmark {

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 10, timeUnit = TimeUnit.SECONDS)
    public void get(MemoryContext context) {
        context.blockingGet();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 10, timeUnit = TimeUnit.SECONDS)
    public void put(MemoryContext context) {
        context.blockingPut();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 10, timeUnit = TimeUnit.SECONDS)
    public void sendTagged(MessagingContext context) {
        context.blockingSendTagged();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 10, timeUnit = TimeUnit.SECONDS)
    public void pingPongTagged(PingPongContext context) {
        context.blockingPingPongTagged();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 10, timeUnit = TimeUnit.SECONDS)
    public void atomicAdd32(AtomicIntegerContext context) {
        context.blockingAtomicAdd();
    }
}
