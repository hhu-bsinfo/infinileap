package de.hhu.bsinfo.infinileap.benchmark.connection;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.binding.ContextParameters.Feature;

public class ConnectionResources {

    private static final long DEFAULT_REQUEST_SIZE = 1024;

    private static final Feature[] FEATURE_SET = {
            Feature.TAG, Feature.RMA, Feature.WAKEUP,
            Feature.ATOMIC_32, Feature.ATOMIC_64, Feature.STREAM
    };

    /**
     * This node's context.
     */
    private final Context context;

    /**
     * This node's worker instance.
     */
    private final Worker worker;

    ConnectionResources(Context context, Worker worker) {
        this.context = context;
        this.worker = worker;
    }

    public Context context() {
        return context;
    }

    public Worker worker() {
        return worker;
    }

    public static ConnectionResources create() throws ControlException {
        // Create context parameters
        var contextParameters = new ContextParameters()
                .setFeatures(FEATURE_SET)
                .setRequestSize(0L);

        // Initialize UCP context
        var context = Context.initialize(contextParameters);

        var workerParameters = new WorkerParameters()
                .setThreadMode(ThreadMode.SINGLE);

        // Create a worker
        var worker = context.createWorker(workerParameters);

        return new ConnectionResources(context, worker);
    }
}
