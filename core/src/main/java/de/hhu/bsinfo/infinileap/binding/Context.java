package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.NativeObject;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.openucx.ucx_h;

import static org.openucx.ucx_h.ucp_worker_create;
import static org.openucx.ucx_h.ucp_init_version;

public class Context extends NativeObject {

    private static final int UCP_MAJOR_VERSION = ucx_h.UCP_API_MAJOR();
    private static final int UCP_MINOR_VERSION = ucx_h.UCP_API_MINOR();

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
}
