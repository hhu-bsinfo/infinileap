package de.hhu.infinileap.daemon.command;

import com.google.protobuf.Empty;
import de.hhu.bsinfo.infinileap.util.MemoryUtil;
import de.hhu.infinileap.daemon.grpc.DaemonGrpc;
import de.hhu.infinileap.daemon.grpc.ReadRequest;
import de.hhu.infinileap.daemon.grpc.TransferRequest;
import de.hhu.infinileap.daemon.util.Constants;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollDomainSocketChannel;
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollEventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.grpc.netty.shaded.io.netty.channel.unix.DomainSocketAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Slf4j
@CommandLine.Command(
        name = "demo",
        description = "connects to the infinileap daemon and executes various operations"
)
public class Demo implements Runnable {

    private static final byte FILL_VALUE = 0;

    @CommandLine.Option(
            names = "--socket",
            description = "The local unix domain socket used for communication",
            defaultValue = Constants.DEFAULT_SOCKET_PATH
    )
    private File socketFile;

    @Override
    public void run() {

        // Initialize connection resources
        final var unixSocket = new DomainSocketAddress(socketFile);
        final var epollGroup = new EpollEventLoopGroup();

        log.info("Initializing gRPC channel on unix domain socket {}", socketFile);

        // Initialize channel
        final var channel = NettyChannelBuilder.forAddress(unixSocket)
                .channelType(EpollDomainSocketChannel.class)
                .eventLoopGroup(epollGroup)
                .usePlaintext()
                .build();

        // Initialize stub used for method invocations
        final var blockingStub = DaemonGrpc.newBlockingStub(channel);

        log.info("Retrieving memory information from daemon");

        // Retrieve memory mapped file info
        final var info = blockingStub.memoryInfo(Empty.getDefaultInstance());

        try {

            log.info("Mapping file {} into memory", info.getFilePath());
            final var segment = MemorySegment.mapFile(
                    Path.of(info.getFilePath()), 0L, info.getSize(),
                    FileChannel.MapMode.READ_WRITE, ResourceScope.globalScope());

            // Clear segment
            segment.fill(FILL_VALUE);

            // Dump first 32 bytes before request invocation
            System.out.println();
            MemoryUtil.dump(segment.asSlice(0L, 32L));
            System.out.println();

            System.out.println();
            System.out.println("<<--| METHOD INVOCATION |-->>");
            System.out.println();

            // Invoke a read request
            blockingStub.read(TransferRequest.newBuilder()
                    .setSourceOffset(0L)
                    .setRemoteOffset(0L)
                    .setLength(16L)
                    .build());

            // Dump first 32 bytes after request invocation
            System.out.println();
            MemoryUtil.dump(segment.asSlice(0L, 32L));
            System.out.println();
        } catch (IOException e) {
            log.error("Unexpected error", e);
        }

        try {
            channel.shutdown();
            channel.awaitTermination(5L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("Terminating connection failed", e);
        }
    }
}
