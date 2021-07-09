package de.hhu.infinileap.daemon.command;

import de.hhu.infinileap.daemon.api.DaemonService;
import de.hhu.infinileap.daemon.util.Constants;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
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
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@CommandLine.Command(
        name = "start",
        description = "starts the infinileap daemon"
)
public class Start implements Runnable {

    @CommandLine.Option(
            names = "--socket",
            description = "The local unix domain socket used for communication",
            defaultValue = Constants.DEFAULT_SOCKET_PATH
    )
    private File socketFile;

    @CommandLine.Parameters(
            index = "0",
            arity = "1",
            paramLabel = "FILE",
            description = "The memory-mapped file used for sharing memory with other processes"
    )
    private Path memoryPath;

    @Override
    public void run() {
        try {

            log.info("Mapping file {} into memory", memoryPath);

            // Map memory from specified file
            final var segment = MemorySegment.mapFile(
                    memoryPath, 0L, Files.size(memoryPath),
                    FileChannel.MapMode.READ_WRITE, ResourceScope.globalScope());

            // Initialize service resources
            final var service = new DaemonService(memoryPath, segment);
            final var epollGroup = new EpollEventLoopGroup();
            final var domainSocket = new DomainSocketAddress(socketFile);

            log.info("Starting gRPC service on unix domain socket {}", socketFile);

            // Initialize gRPC Server
            final var server = NettyServerBuilder.forAddress(domainSocket)
                    .addService(service)
                    .channelType(EpollServerDomainSocketChannel.class)
                    .bossEventLoopGroup(epollGroup)
                    .workerEventLoopGroup(epollGroup)
                    .build()
                    .start();

            log.info("Started gRPC service. Listening for connections...");

            // Block until server shuts down
            server.awaitTermination();
        } catch (IOException e) {
            log.error("The daemon could not start successfully", e);
        } catch (InterruptedException e) {
            log.error("The daemon was interrupted unexpectedly", e);
        }
    }
}
