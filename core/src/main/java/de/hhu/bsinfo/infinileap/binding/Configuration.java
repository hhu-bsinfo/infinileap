package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.binding.util.Status;
import de.hhu.bsinfo.infinileap.util.NativeObject;
import de.hhu.bsinfo.infinileap.util.Parameter;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import static org.openucx.ucx_h.ucp_config_read;
import static org.openucx.ucx_h.ucp_config_release;

@Slf4j
public class Configuration extends NativeObject {

    protected Configuration(MemoryAddress address) {
        super(address, CLinker.C_POINTER);
    }

    public static Configuration read() {
        return read(null, null);
    }

    /**
     * Reads in the configuration form the environment
     * @param prefix
     * @param filename
     * @return
     */
    public static Configuration read(@Nullable String prefix, @Nullable String filename) {
        try (var pointer = MemorySegment.allocateNative(CLinker.C_POINTER)) {
            var status = ucp_config_read(
                    Parameter.ofNullable(prefix),
                    Parameter.ofNullable(filename),
                    pointer
            );

            if (!Status.OK.is(status)) {
                // TODO(krakowski):
                //  Error handling using Exception or other appropriate mechanism
                return null;
            }

            return new Configuration(MemoryAccess.getAddress(pointer));
        }
    }

    @Override
    public void close() {
        ucp_config_release(address());
        super.close();
    }
}
