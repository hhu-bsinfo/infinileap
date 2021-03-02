package de.hhu.bsinfo.infinileap.example.benchmark;

import de.hhu.bsinfo.infinileap.example.benchmark.context.BenchmarkContext;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@Slf4j
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class MemoryLatencyBenchmark {

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 10, timeUnit = TimeUnit.SECONDS)
    public void get(BenchmarkContext context) {
        context.blockingGet();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 10, timeUnit = TimeUnit.SECONDS)
    public void put(BenchmarkContext context) {
        context.blockingPut();
    }
}
