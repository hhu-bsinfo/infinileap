package de.hhu.bsinfo.infinileap.benchmark.suite.buffer;

import de.hhu.bsinfo.infinileap.benchmark.context.buffer.AgronaBufferContext;
import de.hhu.bsinfo.infinileap.benchmark.context.buffer.AgronaSegmentContext;
import de.hhu.bsinfo.infinileap.benchmark.context.buffer.InfinileapBufferContext;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Control;

import java.util.concurrent.TimeUnit;

@Slf4j
@Fork(1)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class RingBufferBenchmark {

    @Benchmark
    @Group("infinileap")
    @GroupThreads(1)
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    @OperationsPerInvocation(65536)
    public void infinileapRead(InfinileapBufferContext context, Control control) {
        context.read(control);
    }

    @Benchmark
    @Group("infinileap")
    @GroupThreads(1)
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    @OperationsPerInvocation(65536)
    public void infinileapWrite(InfinileapBufferContext context, Control control) {
        context.write(control);
    }

    @Benchmark
    @Group("agrona")
    @GroupThreads(1)
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    @OperationsPerInvocation(65536)
    public void agronaRead(AgronaBufferContext context, Control control) {
        context.read(control);
    }

    @Benchmark
    @Group("agrona")
    @GroupThreads(1)
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    @OperationsPerInvocation(65536)
    public void agronaWrite(AgronaBufferContext context, Control control) {
        context.write(control);
    }

    @Benchmark
    @Group("agronaSegment")
    @GroupThreads(1)
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    @OperationsPerInvocation(65536)
    public void agronaSegmentRead(AgronaSegmentContext context, Control control) {
        context.read(control);
    }

    @Benchmark
    @Group("agronaSegment")
    @GroupThreads(3)
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    @OperationsPerInvocation(65536)
    public void agronaSegmentWrite(AgronaSegmentContext context, Control control) {
        context.write(control);
    }
}
