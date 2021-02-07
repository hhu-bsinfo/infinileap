package de.hhu.bsinfo.infinileap.example.demo;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.example.base.ClientServerDemo;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@CommandLine.Command(
        name = "memory",
        description = "Reads memory from a remote machine."
)
public class Memory extends ClientServerDemo {

    private static final byte[] BUFFER_CONTENT = "Hello Infinileap!".getBytes();
    private static final int BUFFER_SIZE = BUFFER_CONTENT.length;

    private final AtomicBoolean barrier = new AtomicBoolean();

    @Override
    protected void onClientReady() {

        // Get initialized components
        final var context = context();
        final var endpoint = endpoint();
        final var worker = worker();

        // Create memory segment and fill it with data
        final var source = MemorySegment.ofArray(BUFFER_CONTENT);
        final var memoryRegion = context.allocateMemory(BUFFER_SIZE);
        memoryRegion.segment().copyFrom(source);

        // Send remote key to server
        log.info("Sending remote key");
        final var descriptor = memoryRegion.descriptor();
        endpoint.sendTagged(descriptor, Tag.of(0L), new RequestParameters()
                .setSendCallback((request, status, data) -> barrier.set(true)));

        waitForAndReset(barrier);

        // Wait until remote signals completion
        final var completion = MemorySegment.allocateNative(Byte.BYTES);
        worker.receiveTagged(completion, Tag.of(0L), new RequestParameters()
                .setReceiveCallback((request, status, tagInfo, data) -> barrier.set(true)));

        waitForAndReset(barrier);
    }

    @Override
    protected void onServerReady() {

        // Get initialized components
        final var context = context();
        final var endpoint = endpoint();
        final var worker = worker();

        // Allocate a memory descriptor
        var descriptor = new MemoryDescriptor();

        // Receive the message
        log.info("Receiving Remote Key");
        worker.receiveTagged(descriptor, Tag.of(0L), new RequestParameters()
                .setReceiveCallback((request, status, tagInfo, data) -> barrier.set(true)));

        waitForAndReset(barrier);

        // Read remote memory
        var remoteKey = endpoint.unpack(descriptor);
        var targetBuffer = MemorySegment.allocateNative(descriptor.remoteSize());
        endpoint.get(targetBuffer, descriptor.remoteAddress(), remoteKey, new RequestParameters()
                .setReceiveCallback((request, status, tagInfo, data) -> barrier.set(true)));

        waitForAndReset(barrier);
        log.info("Read \"{}\" from remote buffer", new String(targetBuffer.toByteArray()));

        // Signal completion
        final var completion = MemorySegment.allocateNative(Byte.BYTES);
        endpoint.sendTagged(completion, Tag.of(0L));
    }
}
