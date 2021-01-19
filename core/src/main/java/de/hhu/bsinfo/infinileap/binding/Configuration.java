package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import static org.openucx.ucx_h.*;

@Slf4j
public class Configuration extends NativeObject {

    /* package-private */ Configuration(MemoryAddress address) {
        super(address, CLinker.C_POINTER);
    }

    public static Configuration read() {
        return read(null, null);
    }

    /**
     * Reads in the configuration form the environment.
     */
    public static Configuration read(@Nullable String prefix, @Nullable String filename) {
        try (var pointer = MemorySegment.allocateNative(CLinker.C_POINTER)) {
            var status = ucp_config_read(
                    Parameter.ofNullable(prefix),
                    Parameter.ofNullable(filename),
                    pointer.address()
            );

            if (!Status.OK.is(status)) {
                // TODO(krakowski):
                //  Error handling using Exception or other appropriate mechanism
                return null;
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
