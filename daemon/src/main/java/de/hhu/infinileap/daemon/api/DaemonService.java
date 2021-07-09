package de.hhu.infinileap.daemon.api;

import com.google.protobuf.Empty;
import de.hhu.infinileap.daemon.grpc.DaemonGrpc;
import de.hhu.infinileap.daemon.grpc.MemoryDescriptor;
import de.hhu.infinileap.daemon.grpc.TransferRequest;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jdk.incubator.foreign.MemorySegment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@RequiredArgsConstructor
public class DaemonService extends DaemonGrpc.DaemonImplBase {

    private final Path memoryPath;

    private final MemorySegment memory;

    private static final byte FILL_VALUE = 1;

    @Override
    public void memoryInfo(Empty request, StreamObserver<MemoryDescriptor> responseObserver) {
        try {
            responseObserver.onNext(MemoryDescriptor.newBuilder()
                    .setFilePath(memoryPath.toString())
                    .setSize(Files.size(memoryPath))
                    .build());

            responseObserver.onCompleted();
        } catch (IOException e) {
            responseObserver.onError(Status.INTERNAL.asException());
        }
    }

    @Override
    public void read(TransferRequest request, StreamObserver<Empty> responseObserver) {
        memory.asSlice(request.getSourceOffset(), request.getLength())
                .fill(FILL_VALUE);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void write(TransferRequest request, StreamObserver<Empty> responseObserver) {
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
