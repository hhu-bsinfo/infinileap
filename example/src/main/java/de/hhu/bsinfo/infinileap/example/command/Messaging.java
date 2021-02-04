package de.hhu.bsinfo.infinileap.example.command;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.example.base.ClientServerDemo;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

@Slf4j
@CommandLine.Command(
        name = "message",
        description = "Exchanges a message between two nodes."
)
public class Messaging extends ClientServerDemo {

    private static final String MESSAGE = "Hello Infinileap!";

    private static final byte[] MESSAGE_BYTES = MESSAGE.getBytes();
    private static final int MESSAGE_SIZE = MESSAGE_BYTES.length;

    @Override
    protected void onClientReady() {

        // Get initialized endpoint and the corresponding worker
        final var endpoint = endpoint();
        final var worker = worker();

        // Allocate a buffer and write the message
        final var source = MemorySegment.ofArray(MESSAGE_BYTES);
        final var buffer = MemorySegment.allocateNative(MESSAGE_SIZE);
        buffer.copyFrom(source);

        // Send the buffer to the server
        log.info("Sending buffer");
        var messageSent = new AtomicBoolean();
        endpoint.sendTagged(buffer, Tag.of(0L), new RequestParameters()
                    .setUserData(0L)
                    .setSendCallback((request, status, data) -> messageSent.set(true)));

        waitFor(messageSent);

        endpoint.close();
    }

    @Override
    protected void onServerReady() {

        // Get initialized worker instance
        final var worker = worker();

        // Allocate a buffer for receiving the remote's message
        var buffer = MemorySegment.allocateNative(MESSAGE_SIZE);

        // Receive the message
        log.info("Receiving message");
        var messageReceived = new AtomicBoolean();
        var requestParameters = new RequestParameters()
                .setReceiveCallback((request, status, tagInfo, data) -> messageReceived.set(true));

        worker.receiveTagged(buffer, Tag.of(0L), requestParameters);
        waitFor(messageReceived);

        log.info("Received \"{}\"", new String(buffer.toByteArray()));
    }
}
