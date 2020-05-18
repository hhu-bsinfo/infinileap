package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.struct.field.NativeInteger;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.struct.LinkNative;
import de.hhu.bsinfo.neutrino.util.NativeObjectRegistry;
import de.hhu.bsinfo.neutrino.util.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@LinkNative("ibv_td")
public class ThreadDomain extends Struct implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadDomain.class);

    private final Context context = referenceField("context");

    ThreadDomain(final long handle) {
        super(handle);
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void close() throws IOException {
        var result = Result.localInstance();

        Verbs.deallocateThreadDomain(getHandle(), result.getHandle());
        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        NativeObjectRegistry.deregisterObject(this);
    }

    @LinkNative("ibv_td_init_attr")
    public static final class InitialAttributes extends Struct {

        private final NativeInteger compatibilityMask = integerField("comp_mask");

        public InitialAttributes() {}

        public InitialAttributes(int compatibilityMask) {
            this.compatibilityMask.set(compatibilityMask);
        }

        public int getCompatibilityMask() {
            return compatibilityMask.get();
        }

        public void setCompatibilityMask(final int value) {
            compatibilityMask.set(value);
        }

        @Override
        public String toString() {
            return "InitialAttributes {" +
                "\n\tcompatibilityMask=" + compatibilityMask +
                "\n}";
        }
    }
}
