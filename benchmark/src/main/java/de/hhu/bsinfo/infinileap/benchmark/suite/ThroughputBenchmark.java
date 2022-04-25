package de.hhu.bsinfo.infinileap.benchmark.suite;

import de.hhu.bsinfo.infinileap.benchmark.context.throughput.*;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@Slf4j
@Fork(1)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Timeout(time = 10, timeUnit = TimeUnit.MINUTES)
public class ThroughputBenchmark {

    public static final int DEFAULT_OPERATION_COUNT = 1024;
    public static final String DEFAULT_OPERATION_COUNT_PARAM = "1024";

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    @OperationsPerInvocation(DEFAULT_OPERATION_COUNT)
    public void read(ReadContext context) {
        context.read();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    @OperationsPerInvocation(DEFAULT_OPERATION_COUNT)
    public void write(WriteContext context) {
        context.write();
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    @OperationsPerInvocation(DEFAULT_OPERATION_COUNT)
    public void send(MessagingContext context) {
        context.send();
    }
}
