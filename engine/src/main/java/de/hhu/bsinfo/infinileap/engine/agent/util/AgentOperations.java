package de.hhu.bsinfo.infinileap.engine.agent.util;

import de.hhu.bsinfo.infinileap.binding.Worker;
import de.hhu.bsinfo.infinileap.binding.WorkerProgress;

public class AgentOperations {

    public static void progressWorker(Worker worker) {
        while (worker.progress() == WorkerProgress.ACTIVE) {
            // Busy spin until worker is IDLE
        }
    }
}
