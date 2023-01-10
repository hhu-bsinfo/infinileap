package de.hhu.bsinfo.infinileap.benchmark.connection;

import de.hhu.bsinfo.infinileap.common.memory.MemoryAlignment;
import de.hhu.bsinfo.infinileap.common.memory.MemoryUtil;
import de.hhu.bsinfo.infinileap.common.util.NativeObject;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class BenchmarkCoordinator implements AutoCloseable {

    private final static int HEADER_SIZE = 16;

    private final static int BODY_SIZE = 4096;

    private final AsynchronousServerSocketChannel serverSocketChannel;

    private final AsynchronousSocketChannel socketChannel;

    private static final int SIGNAL_CLEARED = 0;

    private static final int SIGNAL_SET = 1;

    private final AtomicInteger signal = new AtomicInteger(SIGNAL_CLEARED);

    private final MemorySegment headerSegment = MemoryUtil.allocate(HEADER_SIZE, MemoryAlignment.PAGE, MemorySession.openImplicit());

    private final MemorySegment bodySegment = MemoryUtil.allocate(BODY_SIZE, MemoryAlignment.PAGE, MemorySession.openImplicit());

    private final CompletionHandler<Integer, Object> headerCallback = new CompletionHandler<>() {

        @Override
        public void completed(Integer length, Object attachment) {
            log.info("Received {} bytes", length);
            var lengthField = headerSegment.get(ValueLayout.JAVA_INT, 0);
            socketChannel.read(bodySegment.asByteBuffer(), null, bodyCallback);
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            throw new RuntimeException("Receiving header failed", exc);
        }
    };

    private final CompletionHandler<Integer, Object> bodyCallback = new CompletionHandler<>() {

        @Override
        public void completed(Integer length, Object attachment) {
            log.info("Received {} body bytes", length);
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            throw new RuntimeException("Receiving body failed", exc);
        }
    };

    private BenchmarkCoordinator(
            AsynchronousServerSocketChannel serverSocketChannel,
            AsynchronousSocketChannel socketChannel
    ) {
        this.serverSocketChannel = serverSocketChannel;
        this.socketChannel = socketChannel;
    }

    public static BenchmarkCoordinator bind(InetSocketAddress address)  {
        try {
            // Bind server socket to specified address
            var serverSocketChannel = AsynchronousServerSocketChannel.open()
                    .bind(address);

            // Wait until client tries to establish connection with server
            var socketChannel = serverSocketChannel.accept().get();

            return new BenchmarkCoordinator(serverSocketChannel, socketChannel);
        } catch (InterruptedException | ExecutionException | IOException e) {
            log.error("Creating BenchmarkCoordinator failed", e);
            throw new RuntimeException(e);
        }
    }

    public static BenchmarkCoordinator connect(InetSocketAddress address)  {
        try {
            // Create new client socket
            var socketChannel = AsynchronousSocketChannel.open();

            // Wait until client has established connection with server
            socketChannel.connect(address).get();

            return new BenchmarkCoordinator(null, socketChannel);
        } catch (InterruptedException | ExecutionException | IOException e) {
            log.error("Creating BenchmarkCoordinator failed", e);
            throw new RuntimeException(e);
        }
    }

    public void start() {
        socketChannel.read(headerSegment.asByteBuffer(), null, headerCallback);
    }

    public void send(NativeObject object) {
        try {
            var written = socketChannel.write(object.segment().asByteBuffer()).get();
            if (written != object.byteSize()) {
                log.warn("Object size and written bytes differ");
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        socketChannel.close();
        if (serverSocketChannel != null) {
            serverSocketChannel.close();
        }
    }
}
