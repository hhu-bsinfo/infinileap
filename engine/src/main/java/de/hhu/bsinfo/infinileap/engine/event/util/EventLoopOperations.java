package de.hhu.bsinfo.infinileap.engine.event.util;

import de.hhu.bsinfo.infinileap.binding.Worker;
import de.hhu.bsinfo.infinileap.binding.WorkerProgress;

public class EventLoopOperations {

    public static void progressWorker(Worker worker) {
        while (worker.progress() == WorkerProgress.ACTIVE) {
            // Busy spin until worker is IDLE
        }
    }
}
