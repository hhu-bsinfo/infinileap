package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.Nullable;

import static org.openucx.ucx_h.*;

public class Configuration extends NativeObject {

    /* package-private */ Configuration(MemoryAddress address) {
        super(address, CLinker.C_POINTER);
    }

    public static Configuration read() throws ControlException {
        return read(null, null);
    }

    /**
     * Reads in the configuration form the environment.
     */
    public static Configuration read(@Nullable String prefix, @Nullable String filename) throws ControlException {
        try (var pointer = MemorySegment.allocateNative(CLinker.C_POINTER)) {
            var status = ucp_config_read(
                    Parameter.ofNullable(prefix),
                    Parameter.ofNullable(filename),
                    pointer.address()
            );

            if (Status.isNot(status, Status.OK)) {
                throw new ControlException(status);
            }

            return new Configuration(MemoryAccess.getAddress(pointer));
        }
    }

    public void print() {
        ucp_config_print(address(), stdout$get(), MemoryAddress.NULL, UCS_CONFIG_PRINT_CONFIG());
    }

    @Override
    public void close() {
        ucp_config_release(address());
        super.close();
    }
}
