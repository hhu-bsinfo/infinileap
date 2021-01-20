package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

import static org.openucx.ucx_h.*;

public class Context extends NativeObject {

    private static final int UCP_MAJOR_VERSION = UCP_API_MAJOR();
    private static final int UCP_MINOR_VERSION = UCP_API_MINOR();

    /* package-private */ Context(MemoryAddress address) {
        super(address, CLinker.C_POINTER);
    }

    public static Context initialize(ContextParameters parameters) {
        try (var configuration = Configuration.read()) {
            return initialize(parameters, configuration);
        }
    }

    public static Context initialize(ContextParameters parameters, Configuration configuration) {
        try (var pointer = MemorySegment.allocateNative(CLinker.C_POINTER)) {

            /*
             * We have to use ucp_init_version at this point since ucp_init
             * is declared inline inside the header file. This makes it impossible
             * to lookup the symbol within the shared library.
             */

            var status = ucp_init_version(
                    UCP_MAJOR_VERSION,
                    UCP_MINOR_VERSION,
                    parameters.address(),
                    configuration.address(),
                    pointer.address()
            );

            if (!Status.OK.is(status)) {
                // TODO(krakowski):
                //  Error handling using Exception or other appropriate mechanism
                return null;
            }

            return new Context(MemoryAccess.getAddress(pointer));
        }
    }

    public Worker createWorker(WorkerParameters parameters) {
        try (var pointer = MemorySegment.allocateNative(CLinker.C_POINTER)) {
            var status = ucp_worker_create(
                    this.address(),
                    parameters.address(),
                    pointer.address()
            );

            if (!Status.OK.is(status)) {
                // TODO(krakowski):
                //  Error handling using Exception or other appropriate mechanism
                return null;
            }

            return new Worker(MemoryAccess.getAddress(pointer));
        }
    }

    public MemoryRegion allocateMemory(long size) {
        return mapMemory(MemorySegment.allocateNative(size));

    }

    public MemoryRegion mapMemory(MemorySegment segment) {
        try (var pointer = MemorySegment.allocateNative(CLinker.C_POINTER);
             var parameters = new MappingParameters().setSegment(segment);
             var size = MemorySegment.allocateNative(CLinker.C_LONG)) {

            var status = ucp_mem_map(
                    Parameter.of(this),
                    Parameter.of(parameters),
                    pointer.address()
            );

            if (!Status.OK.is(status)) {
                // TODO(krakowski):
                //  Error handling using Exception or other appropriate mechanism
                return null;
            }

            final var handle = new MemoryHandle(MemoryAccess.getAddress(pointer));
            final var descriptor = getDescriptor(segment, handle);
            return new MemoryRegion(handle, segment, descriptor);
        }
    }

    private MemoryDescriptor getDescriptor(MemorySegment segment, MemoryHandle handle) {
        try (var pointer = MemorySegment.allocateNative(CLinker.C_POINTER);
             var size = MemorySegment.allocateNative(CLinker.C_LONG)) {

            var status = ucp_rkey_pack(
                    Parameter.of(this),
                    Parameter.of(handle),
                    pointer.address(),
                    size.address()
            );

            if (!Status.OK.is(status)) {
                // TODO(krakowski):
                //  Error handling using Exception or other appropriate mechanism
                return null;
            }

            var remoteKey = MemoryAccess.getAddress(pointer)
                    .asSegmentRestricted(MemoryAccess.getLong(size));

            var descriptor = new MemoryDescriptor(segment, remoteKey);
            ucp_rkey_buffer_release(remoteKey);
            return descriptor;
        }
    }
}
