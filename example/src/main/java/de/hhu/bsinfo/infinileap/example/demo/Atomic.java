package de.hhu.bsinfo.infinileap.example.demo;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.example.base.CommunicationDemo;
import de.hhu.bsinfo.infinileap.example.util.CommunicationBarrier;
import de.hhu.bsinfo.infinileap.example.util.Requests;
import de.hhu.bsinfo.infinileap.primitive.NativeInteger;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

@Slf4j
@CommandLine.Command(
        name = "atomic",
        description = "Atomically adds a value at a remote address."
)
public class Atomic extends CommunicationDemo {

    private final CommunicationBarrier barrier = new CommunicationBarrier();

    @Override
    protected void onClientReady(Context context, Worker worker, Endpoint endpoint) throws ControlException, InterruptedException {

        // Create memory segment and write a number into it
        final var memoryRegion = context.allocateMemory(Integer.BYTES);
        final var integer = NativeInteger.map(memoryRegion.segment());
        integer.set(10);

        // Send remote key to server
        log.info("Value before remote access is {}", integer.get());
        final var descriptor = memoryRegion.descriptor();
        var request = endpoint.sendTagged(descriptor, Tag.of(0L), new RequestParameters()
                .setSendCallback(barrier::release));

        Requests.await(worker, barrier);
        Requests.release(request);

        log.info("Waiting for remote access");

        // Wait until remote signals completion
        final var completion = MemorySegment.allocateNative(Byte.BYTES, scope);
        request = worker.receiveTagged(completion, Tag.of(0L), new RequestParameters()
                .setReceiveCallback(barrier::release));

        Requests.await(worker, barrier);
        Requests.release(request);

        log.info("Value after remote access is {}", integer.get());
    }

    @Override
    protected void onServerReady(Context context, Worker worker, Endpoint endpoint) throws ControlException, InterruptedException {

        // Allocate a memory descriptor
        var descriptor = new MemoryDescriptor();

        // Receive the message
        log.info("Receiving Remote Key");
        var request = worker.receiveTagged(descriptor, Tag.of(0L), new RequestParameters()
                .setReceiveCallback(barrier::release));

        Requests.await(worker, barrier);
        Requests.release(request);

        // Create a memory segment for atomic operations
        var memorySegment = context.allocateMemory(Integer.BYTES);
        var integer = NativeInteger.map(memorySegment.segment());
        integer.set(32);

        // Unpack remote key
        var remoteKey = pushResource(endpoint.unpack(descriptor));

        // Atomically add value at remote address
        log.info("Adding {} to remote address", integer.get());
        request = endpoint.atomic(AtomicOperation.ADD, integer, descriptor.remoteAddress(), remoteKey, new RequestParameters()
                .setDataType(integer.dataType()));

        Requests.await(worker, request);

        // Signal completion
        final var completion = MemorySegment.allocateNative(Byte.BYTES, scope);
        endpoint.sendTagged(completion, Tag.of(0L));
    }
}
