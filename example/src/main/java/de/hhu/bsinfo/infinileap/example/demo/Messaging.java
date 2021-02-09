package de.hhu.bsinfo.infinileap.example.demo;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.example.base.CommunicationDemo;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@CommandLine.Command(
        name = "messaging",
        description = "Exchanges a message between two nodes."
)
public class Messaging extends CommunicationDemo {

    private static final String MESSAGE = "Hello Infinileap!";

    private static final byte[] MESSAGE_BYTES = MESSAGE.getBytes();
    private static final int MESSAGE_SIZE = MESSAGE_BYTES.length;

    @Override
    protected void onClientReady(Context context, Worker worker, Endpoint endpoint) {

        // Allocate a buffer and write the message
        final var source = pushResource(MemorySegment.ofArray(MESSAGE_BYTES));
        final var buffer = pushResource(MemorySegment.allocateNative(MESSAGE_SIZE));
        buffer.copyFrom(source);

        // Send the buffer to the server
        log.info("Sending buffer");

        var request = endpoint.sendTagged(buffer, Tag.of(0L), new RequestParameters()
                    .setSendCallback(this::releaseBarrier));

        pushResource(request);
        barrier();
    }

    @Override
    protected void onServerReady(Context context, Worker worker, Endpoint endpoint) {

        // Allocate a buffer for receiving the remote's message
        var buffer = pushResource(MemorySegment.allocateNative(MESSAGE_SIZE));

        // Receive the message
        log.info("Receiving message");

        var request = worker.receiveTagged(buffer, Tag.of(0L), new RequestParameters()
                .setReceiveCallback(this::releaseBarrier));

        pushResource(request);
        barrier();

        log.info("Received \"{}\"", new String(buffer.toByteArray()));
    }
}
