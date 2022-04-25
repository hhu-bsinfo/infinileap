package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.*;
import org.jetbrains.annotations.Nullable;

import static org.openucx.OpenUcx.*;
import static org.unix.Linux.stdout$get;

public class Configuration extends NativeObject implements AutoCloseable{

    /* package-private */ Configuration(MemoryAddress address) {
        super(address, ValueLayout.ADDRESS);
    }

    public static Configuration read() throws ControlException {
        return read(null, null);
    }

    /**
     * Reads in the configuration form the environment.
     */
    public static Configuration read(@Nullable String prefix, @Nullable String filename) throws ControlException {
        try (var scope = ResourceScope.newConfinedScope()) {
            var pointer = MemorySegment.allocateNative(ValueLayout.ADDRESS, scope);
            var status = ucp_config_read(
                    Parameter.ofNullable(prefix, scope),
                    Parameter.ofNullable(filename, scope),
                    pointer.address()
            );

            if (Status.isNot(status, Status.OK)) {
                throw new ControlException(status);
            }

            return new Configuration(pointer.get(ValueLayout.ADDRESS, 0L));
        }
    }

    public void print() {
        ucp_config_print(address(), stdout$get(), MemoryAddress.NULL, UCS_CONFIG_PRINT_CONFIG());
    }

    @Override
    public void close() {
        ucp_config_release(address());
    }
}
