package de.hhu.bsinfo.neutrino.buffer;

import de.hhu.bsinfo.neutrino.verbs.AccessFlag;
import de.hhu.bsinfo.neutrino.verbs.MemoryWindow;
import de.hhu.bsinfo.neutrino.verbs.QueuePair;

public class RegisteredBufferWindow extends RegisteredBuffer {

    private MemoryWindow memoryWindow;

    public RegisteredBufferWindow(RegisteredBuffer parent, MemoryWindow memoryWindow, long windowOffset, long windowCapacity) {
        super(parent.getMemoryRegion(), parent.getHandle() + windowOffset, windowCapacity, parent);

        this.memoryWindow = memoryWindow;
    }

    @Override
    public int getRemoteKey() {
        return memoryWindow.getRemoteKey();
    }

    @Override
    public RegisteredBufferWindow bindMemoryWindow(MemoryWindow memoryWindow, QueuePair queuePair, long offset, long length, AccessFlag... flags) {
        throw new UnsupportedOperationException("You cannot bind a memory window to another memory window! Use the window's parent memory region instead.");
    }

    public boolean rebind(RegisteredBuffer parent, long windowOffset, long windowCapacity) {
        // TODO: Implement rebind, once setHandle() and setCapacity() are implemented in LocalBuffer
        throw new UnsupportedOperationException("This function is not yet implemented!");
    }

    @Override
    public void close() {
        memoryWindow.close();
    }

    @Override
    public String toString() {
        return "RegisteredBufferWindow {" +
            "\n\tparent=" + super.toString() +
            ",\n\tmemoryWindow=" + memoryWindow +
            "\n}";
    }
}
