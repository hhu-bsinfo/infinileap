package de.hhu.bsinfo.infinileap.example.command;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.binding.ContextParameters.Feature;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;
import org.openucx.ucx_h;
import picocli.CommandLine;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

@Slf4j
@CommandLine.Command(
        name = "memory",
        description = "Allocates memory and prints its information"
)
public class Memory implements Runnable {

    private static final long DEFAULT_BUFFER_SIZE = 1024;

    @CommandLine.Option(
            names = {"-s", "--size"},
            description = "The buffer's size in bytes.")
    private long bufferSize = DEFAULT_BUFFER_SIZE;

    @Override
    public void run() {

        // Create context parameters
        final var contextParameters = new ContextParameters()
                .setFeatures(Feature.RMA);

        // Initialize UCP context
        final var context = Context.initialize(contextParameters);

        final var handle = context.allocateMemory(bufferSize);

        final var remoteKey = context.getRemoteKey(handle);
        remoteKey.hexDump();
    }
}
