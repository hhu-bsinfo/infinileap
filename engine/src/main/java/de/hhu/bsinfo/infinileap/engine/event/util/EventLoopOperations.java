package de.hhu.bsinfo.infinileap.engine.event.util;

import de.hhu.bsinfo.infinileap.binding.Worker;
import de.hhu.bsinfo.infinileap.binding.WorkerProgress;

public class EventLoopOperations {

    public static WorkerProgress progressWorker(Worker worker) {
        if (worker.progress() == WorkerProgress.IDLE) {
            return WorkerProgress.IDLE;
        }

        while (worker.progress() == WorkerProgress.ACTIVE) {
            // Busy spin until worker is IDLE
        }

        return WorkerProgress.ACTIVE;
    }

    public static WorkerProgress progressWorker(Worker worker, long duration) {
        if (worker.progress() == WorkerProgress.IDLE) {
            return WorkerProgress.IDLE;
        }

        var start = System.currentTimeMillis();
        while (worker.progress() == WorkerProgress.ACTIVE && System.currentTimeMillis() - start < duration) {
            // Busy spin until worker is IDLE
        }

        return WorkerProgress.ACTIVE;
    }
}
