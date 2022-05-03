package de.hhu.bsinfo.infinileap.engine.agent;

import de.hhu.bsinfo.infinileap.binding.Worker;
import de.hhu.bsinfo.infinileap.binding.WorkerProgress;
import de.hhu.bsinfo.infinileap.common.multiplex.SelectionKey;

import java.io.IOException;

public class ConnectionAgent extends EpollAgent<Worker> {

    private final Worker worker;

    public ConnectionAgent(Worker worker) {
        super();
        this.worker = worker;
    }

    @Override
    protected void process(SelectionKey<Worker> selectionKey) throws IOException {
        while (worker.progress() == WorkerProgress.ACTIVE) {
            // Busy Spin
        }
    }

    public Worker getWorker() {
        return worker;
    }

    @Override
    public String roleName() {
        return null;
    }
}
