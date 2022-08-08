package de.hhu.bsinfo.infinileap.example.demo;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.example.base.CommunicationDemo;
import de.hhu.bsinfo.infinileap.util.Requests;
import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@CommandLine.Command(
        name = "active",
        description = "Sends and receives active messages."
)
public class ActiveMessage extends CommunicationDemo {

    private static final Identifier IDENTIFIER = Identifier.of(0x01);

    private final AtomicBoolean lastMessageReceived = new AtomicBoolean(false);

    @CommandLine.Option(
            names = {"-n", "--count"},
            description = "The number of messages to send.")
    private int count = 1;

    @Override
    protected void onClientReady(Context context, Worker worker, Endpoint endpoint) throws ControlException, InterruptedException {

        // Allocate
        var message = MemorySegment.ofArray(
                "Hello World".getBytes(StandardCharsets.US_ASCII)
        );

        // Create header and data segments
        final var header = MemorySegment.allocateNative(4, session);
        final var data = MemorySegment.allocateNative(message.byteSize(), session);

        // Copy message into native segment
        MemorySegment.copy(message, 0L, data, 0L, message.byteSize());

        // Set data within segments
        header.set(ValueLayout.JAVA_INT, 0L, 42);

        // Invoke remote handler
        for (var i = 0; i < count; i++) {
            Requests.await(worker, endpoint.sendActive(IDENTIFIER, header, data, new RequestParameters()
                    .setDataType(DataType.CONTIGUOUS_8_BIT)));
        }

        // Send last message
        header.set(ValueLayout.JAVA_INT, 0L, 0xDEAD);
        Requests.await(worker, endpoint.sendActive(IDENTIFIER, header, data, new RequestParameters()
                .setDataType(DataType.CONTIGUOUS_8_BIT)));
    }

    @Override
    protected void onServerReady(Context context, Worker worker, Endpoint endpoint) throws ControlException, InterruptedException {

        // Register local handler
        var params = new HandlerParameters()
                .setId(IDENTIFIER)
                .setCallback(callback)
                .setFlags(HandlerParameters.Flag.WHOLE_MESSAGE);

        worker.setHandler(params);

        while (!lastMessageReceived.get()) {
            worker.progress();
        }
    }

    private final ActiveMessageCallback callback = new ActiveMessageCallback() {

        @Override
        protected Status onActiveMessage(MemoryAddress argument, MemorySegment header, MemorySegment data, MemoryAddress parameters) {
            log.info("Received integer value {} in header", header.get(ValueLayout.JAVA_INT, 0L));
            log.info("Received long value {} in body", data.get(ValueLayout.JAVA_INT, 0L));
            if (header.get(ValueLayout.JAVA_INT, 0L) == 0xDEAD) {
                lastMessageReceived.set(true);
            }

            return Status.OK;
        }
    };
}
