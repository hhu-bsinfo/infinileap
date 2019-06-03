package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import java.util.function.Consumer;

@LinkNative("ibv_td")
public class ThreadDomain extends Struct {

    private final Context context = referenceField("context", Context::new);

    ThreadDomain(final long handle) {
        super(handle);
    }

    public Context getContext() {
        return context;
    }

    @LinkNative("ibv_td_init_attr")
    public static final class InitialAttributes extends Struct {

        private final NativeInteger compatibilityMask = integerField("comp_mask");

        public InitialAttributes() {}

        public InitialAttributes(Consumer<InitialAttributes> configurator) {
            configurator.accept(this);
        }

        public int getCompatibilityMask() {
            return compatibilityMask.get();
        }

        public void setCompatibilityMask(final int value) {
            compatibilityMask.set(value);
        }
    }
}
