package de.hhu.bsinfo.infinileap.example.demo;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.example.base.CommunicationDemo;
import de.hhu.bsinfo.infinileap.example.util.Requests;
import de.hhu.bsinfo.infinileap.util.MemoryUtil;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.time.Duration;

@Slf4j
@CommandLine.Command(
        name = "active",
        description = "Sends and receives active messages."
)
public class ActiveMessage extends CommunicationDemo {

    private static final Identifier IDENTIFIER = new Identifier(0x01);


    @Override
    protected void onClientReady(Context context, Worker worker, Endpoint endpoint) throws ControlException, InterruptedException {

        // Wait one second so that the server has registered its handler
        Thread.sleep(Duration.ofSeconds(1).toMillis());

        // Create header and data segments
        final var header = MemorySegment.allocateNative(4);
        final var data = MemorySegment.allocateNative(16);

        // Set data within segments
        MemoryAccess.setInt(header, 42);
        MemoryAccess.setLong(data, 42L);

        // Invoke remote handler
        Requests.await(worker, endpoint.sendActive(IDENTIFIER, header, data, new RequestParameters()
                        .setDataType(DataType.CONTIGUOUS_8_BIT)));
    }

    @Override
    protected void onServerReady(Context context, Worker worker, Endpoint endpoint) throws ControlException, InterruptedException {

        // Register local handler
        try (var params = new HandlerParameters()
                .setId(IDENTIFIER)
                .setCallback(this::onActiveMessage)
                .setFlags(HandlerParameters.Flag.WHOLE_MESSAGE)) {
            worker.setHandler(params);
        }

        // Wait on active message
        for (var i = 0; i < 5; i++) {
            worker.progress();
            Thread.sleep(Duration.ofSeconds(1).toMillis());
        }
    }

    private Status onActiveMessage(MemoryAddress argument, MemorySegment header, MemorySegment data, MemoryAddress params) {
        MemoryUtil.dump(header);
        return Status.OK;
    }
}
