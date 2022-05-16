package de.hhu.bsinfo.infinileap.engine.agent.util;

import de.hhu.bsinfo.infinileap.common.io.FileDescriptor;
import de.hhu.bsinfo.infinileap.common.multiplex.EventFileDescriptor;
import de.hhu.bsinfo.infinileap.common.multiplex.Watchable;
import de.hhu.bsinfo.infinileap.engine.agent.command.AgentCommand;
import org.agrona.concurrent.ManyToOneConcurrentArrayQueue;

import java.io.IOException;

public class CommandQueue extends ManyToOneConcurrentArrayQueue<AgentCommand<?>> implements Watchable {

    private final EventFileDescriptor eventFileDescriptor;

    public CommandQueue(int requestedCapacity) {
        super(requestedCapacity);

        try {
            this.eventFileDescriptor = EventFileDescriptor.create(EventFileDescriptor.OpenMode.NONBLOCK);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileDescriptor descriptor() {
        return eventFileDescriptor;
    }

    public void fire() {
        try {
            eventFileDescriptor.fire();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void disarm() {
        try {
            eventFileDescriptor.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String name() {
        return CommandQueue.class.getSimpleName();
    }
}
