package de.hhu.bsinfo.infinileap.example.benchmark;

import de.hhu.bsinfo.infinileap.example.benchmark.context.*;
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
    public void read(ReadContext context) {
        context.blockingGet();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 10, timeUnit = TimeUnit.SECONDS)
    public void write(WriteContext context) {
        context.blockingPut();
    }

// This does not measure the actual network latency, because
// ucx immediately completes the send request regardless of it
// being received at the other side.
//
//    @Benchmark
//    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
//    @Measurement(iterations = 10, time = 10, timeUnit = TimeUnit.SECONDS)
//    public void sendTagged(MessagingContext context) {
//        context.blockingSendTagged();
//    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 10, timeUnit = TimeUnit.SECONDS)
    public void send(PingPongContext context) {
        context.blockingPingPongTagged();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 10, timeUnit = TimeUnit.SECONDS)
    public void add32(AtomicIntegerContext context) {
        context.blockingAtomicAdd();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 10, timeUnit = TimeUnit.SECONDS)
    public void add64(AtomicLongContext context) {
        context.blockingAtomicAdd();
    }
}
