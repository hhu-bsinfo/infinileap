package de.hhu.bsinfo.infinileap.example.demo;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.example.base.CommunicationDemo;
import de.hhu.bsinfo.infinileap.example.util.CommunicationBarrier;
import de.hhu.bsinfo.infinileap.example.util.Requests;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

@Slf4j
@CommandLine.Command(
        name = "memory",
        description = "Reads memory from a remote machine."
)
public class Memory extends CommunicationDemo {

    private static final byte[] BUFFER_CONTENT = "Hello Infinileap!".getBytes();
    private static final int BUFFER_SIZE = BUFFER_CONTENT.length;

    private final CommunicationBarrier barrier = new CommunicationBarrier();

    @Override
    protected void onClientReady(Context context, Worker worker, Endpoint endpoint) throws ControlException {

        // Create memory segment and fill it with data
        final var source = MemorySegment.ofArray(BUFFER_CONTENT);
        final var memoryRegion = context.allocateMemory(BUFFER_SIZE);
        memoryRegion.segment().copyFrom(source);

        // Send remote key to server
        log.info("Sending remote key");
        final var descriptor = memoryRegion.descriptor();

        pushResource(
            endpoint.sendTagged(descriptor, Tag.of(0L), new RequestParameters()
                    .setSendCallback(barrier::release))
        );

        Requests.await(worker, barrier);

        // Wait until remote signals completion
        final var completion = MemorySegment.allocateNative(Byte.BYTES);

        pushResource(
            worker.receiveTagged(completion, Tag.of(0L), new RequestParameters()
                    .setReceiveCallback(barrier::release))
        );

        Requests.await(worker, barrier);
    }

    @Override
    protected void onServerReady(Context context, Worker worker, Endpoint endpoint) throws ControlException {

        // Allocate a memory descriptor
        var descriptor = new MemoryDescriptor();

        // Receive the message
        log.info("Receiving Remote Key");

        pushResource(
            worker.receiveTagged(descriptor, Tag.of(0L), new RequestParameters()
                .setReceiveCallback(barrier::release))
        );

        Requests.await(worker, barrier);

        // Read remote memory
        var remoteKey = endpoint.unpack(descriptor);
        var targetBuffer = MemorySegment.allocateNative(descriptor.remoteSize());
        pushResource(remoteKey);

        pushResource(
            endpoint.get(targetBuffer, descriptor.remoteAddress(), remoteKey, new RequestParameters()
                .setReceiveCallback(barrier::release))
        );

        Requests.await(worker, barrier);

        log.info("Read \"{}\" from remote buffer", new String(targetBuffer.toByteArray()));

        // Signal completion
        final var completion = MemorySegment.allocateNative(Byte.BYTES);
        pushResource(endpoint.sendTagged(completion, Tag.of(0L)));
    }
}
