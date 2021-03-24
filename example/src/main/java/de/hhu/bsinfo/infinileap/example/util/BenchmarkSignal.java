package de.hhu.bsinfo.infinileap.example.util;

import de.hhu.bsinfo.infinileap.binding.Endpoint;
import de.hhu.bsinfo.infinileap.binding.Request;
import de.hhu.bsinfo.infinileap.binding.Worker;
import de.hhu.bsinfo.infinileap.primitive.NativeInteger;

public final class BenchmarkSignal implements AutoCloseable {

    private static final int SIGNAL_CLEARED = 0;
    private static final int SIGNAL_SET = 1;

    private final long request;
    private final NativeInteger data;

    public BenchmarkSignal(long request, NativeInteger data) {
        this.request = request;
        this.data = data;
    }

    @Override
    public void close() throws Exception {
        Requests.release(request);
        data.close();
    }

    public boolean isSet() {
        return data.get() == SIGNAL_SET;
    }

    public boolean isCleared() {
        return data.get() == SIGNAL_CLEARED;
    }

    public static BenchmarkSignal listen(Worker worker) {
        var data = new NativeInteger(SIGNAL_CLEARED);
        return new BenchmarkSignal(worker.receiveTagged(data, Constants.TAG_BENCHMARK_SIGNAL), data);
    }

    public static void send(Worker worker, Endpoint endpoint) {
        try (var data = new NativeInteger(SIGNAL_SET)) {
            Requests.poll(worker, endpoint.sendTagged(data, Constants.TAG_BENCHMARK_SIGNAL));
        }
    }
}
