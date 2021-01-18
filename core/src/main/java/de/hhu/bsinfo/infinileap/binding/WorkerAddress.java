package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.NativeObject;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public final class WorkerAddress extends NativeObject {

    WorkerAddress(MemoryAddress address, long byteSize) {
        super(address, byteSize);
    }

    WorkerAddress(MemorySegment segment) {
        super(segment);
    }

    public WorkerAddress exchange(Socket socket) throws IOException {

        var localBytes = toByteArray();
        var localLength = localBytes.length;

        // Write local address to output stream
        socket.getOutputStream().write(ByteBuffer.allocate(Integer.BYTES).putInt(localLength).array());
        socket.getOutputStream().write(localBytes);

        // Receive remote address
        var remoteLength = ByteBuffer.wrap(socket.getInputStream().readNBytes(Integer.BYTES)).getInt();
        var remoteBytes = socket.getInputStream().readNBytes(remoteLength);

        var source = MemorySegment.ofArray(remoteBytes);
        var target = MemorySegment.allocateNative(source.byteSize());
        target.copyFrom(source);

        return new WorkerAddress(target);
    }
}