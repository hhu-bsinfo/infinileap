package de.hhu.bsinfo.infinileap.example.demo;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.example.base.ClientServerDemo;
import de.hhu.bsinfo.infinileap.primitive.NativeInteger;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@CommandLine.Command(
        name = "atomic",
        description = "Atomically adds a value at a remote address."
)
public class Atomic extends ClientServerDemo {

    private final AtomicBoolean barrier = new AtomicBoolean();

    @Override
    protected void onClientReady() {

        // Get initialized components
        final var context = context();
        final var endpoint = endpoint();
        final var worker = worker();

        // Create memory segment
        final var memoryRegion = context.allocateMemory(Integer.BYTES);
        final var integer = NativeInteger.map(memoryRegion.segment());
        integer.set(10);

        // Send remote key to server
        log.info("Value before remote access is {}", integer.get());

        final var descriptor = memoryRegion.descriptor();
        endpoint.sendTagged(descriptor, Tag.of(0L), new RequestParameters()
                .setSendCallback((request, status, data) -> barrier.set(true)));

        waitForAndReset(barrier);

        log.info("Waiting for remote access");

        // Wait until remote signals completion
        final var completion = MemorySegment.allocateNative(Byte.BYTES);
        worker.receiveTagged(completion, Tag.of(0L), new RequestParameters()
                .setReceiveCallback((request, status, tagInfo, data) -> barrier.set(true)));

        waitForAndReset(barrier);

        log.info("Value after remote access is {}", integer.get());
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

        // Create a memory segment for atomic operations
        var memorySegment = context.allocateMemory(Integer.BYTES);
        var integer = NativeInteger.map(memorySegment.segment());
        integer.set(32);

        // Unpack remote key
        var remoteKey = endpoint.unpack(descriptor);

        // Atomically add value at remote address
        log.info("Adding {} to remote address", integer.get());
        var request = endpoint.atomic(AtomicOperation.ADD, integer, descriptor.remoteAddress(), remoteKey, new RequestParameters()
                .setDataType(integer.dataType()));

        // Wait for request to complete
        waitFor(request);

        // Signal completion
        final var completion = MemorySegment.allocateNative(Byte.BYTES);
        endpoint.sendTagged(completion, Tag.of(0L));
    }
}
