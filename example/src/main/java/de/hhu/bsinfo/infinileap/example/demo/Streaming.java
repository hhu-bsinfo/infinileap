package de.hhu.bsinfo.infinileap.example.demo;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.binding.RequestParameters.Flag;
import de.hhu.bsinfo.infinileap.example.base.CommunicationDemo;
import de.hhu.bsinfo.infinileap.example.util.Requests;
import de.hhu.bsinfo.infinileap.primitive.NativeInteger;
import de.hhu.bsinfo.infinileap.primitive.NativeLong;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

@Slf4j
@CommandLine.Command(
        name = "streaming",
        description = "Exchanges a stream of data between two nodes."
)
public class Streaming extends CommunicationDemo {

    private static final long BUFFER_SIZE = Integer.BYTES * 2;

    @Override
    protected void onClientReady(Context context, Worker worker, Endpoint endpoint) throws InterruptedException {

        // Allocate a buffer and write numbers into it
        final var buffer = MemorySegment.allocateNative(BUFFER_SIZE);
        final var first = NativeInteger.map(buffer, 0L);
        final var second = NativeInteger.map(buffer, 4L);

        first.set (0xDEAD);
        second.set(0xC0DE);

        // Send the buffer to the server
        log.info("Sending first chunk of stream");
        var request = endpoint.sendStream(first, new RequestParameters()
                .setDataType(first.dataType()));

        Requests.await(worker, request);

        log.info("Sending second chunk of stream");
        request = endpoint.sendStream(second, new RequestParameters()
            .setDataType(second.dataType()));

        Requests.await(worker, request);
    }

    @Override
    protected void onServerReady(Context context, Worker worker, Endpoint endpoint) throws InterruptedException {

        // Allocate a buffer for receiving the remote's message
        var buffer = MemorySegment.allocateNative(BUFFER_SIZE);
        var length = new NativeLong();

        log.info("Receiving stream");
        var request = endpoint.receiveStream(buffer, 2, length, new RequestParameters()
            .setDataType(DataType.CONTIGUOUS_32_BIT)
            .setFlags(Flag.STREAM_WAIT));

        Requests.await(worker, request);

        final var first = NativeInteger.map(buffer, 0L);
        final var second = NativeInteger.map(buffer, 4L);

        log.info("Received \"0x{}\"", Integer.toHexString(first.get()).toUpperCase());
        log.info("Received \"0x{}\"", Integer.toHexString(second.get()).toUpperCase());
    }
}
