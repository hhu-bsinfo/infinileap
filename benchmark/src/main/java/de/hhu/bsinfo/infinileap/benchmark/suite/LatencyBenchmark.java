package de.hhu.bsinfo.infinileap.benchmark.suite;

import de.hhu.bsinfo.infinileap.benchmark.context.latency.*;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@Slf4j
@Fork(1)
@BenchmarkMode({Mode.SampleTime, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class LatencyBenchmark {

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    public void read(ReadContext context) {
        context.get();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    public void write(WriteContext context) {
        context.write();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    public void pingPong(PingPongContext context) {
        context.pingPong();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    public void add32(AtomicIntegerContext context) {
        context.atomicAdd();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    public void swap32(AtomicIntegerContext context) {
        context.atomicSwap();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    public void cswap32(AtomicIntegerContext context) {
        context.atomicCompareAndSwap();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    public void and32(AtomicIntegerContext context) {
        context.atomicAnd();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    public void or32(AtomicIntegerContext context) {
        context.atomicOr();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    public void xor32(AtomicIntegerContext context) {
        context.atomicXor();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    public void add64(AtomicLongContext context) {
        context.atomicAdd();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    public void swap64(AtomicLongContext context) {
        context.atomicSwap();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    public void cswap64(AtomicLongContext context) {
        context.atomicCompareAndSwap();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    public void and64(AtomicLongContext context) {
        context.atomicAnd();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    public void or64(AtomicLongContext context) {
        context.atomicOr();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    public void xor64(AtomicLongContext context) {
        context.atomicXor();
    }
}
