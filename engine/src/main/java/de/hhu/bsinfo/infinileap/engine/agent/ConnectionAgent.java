package de.hhu.bsinfo.infinileap.engine.agent;

import de.hhu.bsinfo.infinileap.binding.Worker;
import de.hhu.bsinfo.infinileap.multiplex.SelectionKey;

import java.io.IOException;

public class ConnectionAgent extends EpollAgent<Worker> {


    protected ConnectionAgent() {
        super();
    }

    @Override
    protected void process(SelectionKey<Worker> selectionKey) throws IOException {

    }

    @Override
    public String roleName() {
        return null;
    }
}
