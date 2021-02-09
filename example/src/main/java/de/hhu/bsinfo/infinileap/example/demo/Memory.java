package de.hhu.bsinfo.infinileap.example.demo;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.example.base.CommunicationDemo;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@CommandLine.Command(
        name = "memory",
        description = "Reads memory from a remote machine."
)
public class Memory extends CommunicationDemo {

    private static final byte[] BUFFER_CONTENT = "Hello Infinileap!".getBytes();
    private static final int BUFFER_SIZE = BUFFER_CONTENT.length;

    @Override
    protected void onClientReady(Context context, Worker worker, Endpoint endpoint) throws ControlException {

        // Create memory segment and fill it with data
        final var source = MemorySegment.ofArray(BUFFER_CONTENT);
        final var memoryRegion = context.allocateMemory(BUFFER_SIZE);
        memoryRegion.segment().copyFrom(source);

        // Send remote key to server
        log.info("Sending remote key");
        final var descriptor = memoryRegion.descriptor();
        endpoint.sendTagged(descriptor, Tag.of(0L), new RequestParameters()
                .setSendCallback(this::releaseBarrier));

        barrier();

        // Wait until remote signals completion
        final var completion = MemorySegment.allocateNative(Byte.BYTES);
        worker.receiveTagged(completion, Tag.of(0L), new RequestParameters()
                .setReceiveCallback(this::releaseBarrier));

        barrier();
    }

    @Override
    protected void onServerReady(Context context, Worker worker, Endpoint endpoint) throws ControlException {

        // Allocate a memory descriptor
        var descriptor = new MemoryDescriptor();

        // Receive the message
        log.info("Receiving Remote Key");
        worker.receiveTagged(descriptor, Tag.of(0L), new RequestParameters()
                .setReceiveCallback(this::releaseBarrier));

        barrier();

        // Read remote memory
        var remoteKey = endpoint.unpack(descriptor);
        var targetBuffer = MemorySegment.allocateNative(descriptor.remoteSize());
        endpoint.get(targetBuffer, descriptor.remoteAddress(), remoteKey, new RequestParameters()
                .setReceiveCallback(this::releaseBarrier));

        barrier();
        log.info("Read \"{}\" from remote buffer", new String(targetBuffer.toByteArray()));

        // Signal completion
        final var completion = MemorySegment.allocateNative(Byte.BYTES);
        endpoint.sendTagged(completion, Tag.of(0L));
    }
}
