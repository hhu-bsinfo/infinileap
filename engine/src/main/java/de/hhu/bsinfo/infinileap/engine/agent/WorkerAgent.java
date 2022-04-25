package de.hhu.bsinfo.infinileap.engine.agent;

import de.hhu.bsinfo.infinileap.binding.Worker;
import de.hhu.bsinfo.infinileap.binding.WorkerProgress;
import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.Agent;

@Slf4j
public class WorkerAgent implements Agent {

    private final Worker worker;

    public WorkerAgent(Worker worker) {
        this.worker = worker;
    }

    public Worker getWorker() {
        return worker;
    }

    @Override
    public int doWork() throws Exception {
        if (worker.progress() == WorkerProgress.IDLE) {
            worker.await();
        }

        return 1;
    }

    @Override
    public String roleName() {
        return null;
    }
}
