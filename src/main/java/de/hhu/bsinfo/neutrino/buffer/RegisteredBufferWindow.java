package de.hhu.bsinfo.neutrino.buffer;

import de.hhu.bsinfo.neutrino.verbs.AccessFlag;
import de.hhu.bsinfo.neutrino.verbs.MemoryWindow;
import de.hhu.bsinfo.neutrino.verbs.QueuePair;
import de.hhu.bsinfo.neutrino.verbs.SendWorkRequest;

public class RegisteredBufferWindow extends RegisteredBuffer {

    private MemoryWindow memoryWindow;

    public RegisteredBufferWindow(RegisteredBuffer parent, MemoryWindow memoryWindow, long windowOffset, long windowCapacity) {
        super(parent.getMemoryRegion(), parent.getHandle() + windowOffset, windowCapacity, FAKE_PARENT);

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

    public boolean rebind(RegisteredBuffer parent, QueuePair queuePair, long windowOffset, long windowCapacity, AccessFlag... flags) {
        var attributes = new MemoryWindow.BindAttributes.Builder(parent.getMemoryRegion(), parent.getHandle() + windowOffset, windowCapacity, flags)
                .withSendFlags(SendWorkRequest.SendFlag.SIGNALED).build();

        if(memoryWindow.bind(queuePair, attributes)) {
            reWrap(parent.getHandle() + windowOffset, windowCapacity);
            return true;
        }

        return false;
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
