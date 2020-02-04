package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.NativeObjectRegistry;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

@LinkNative("ibv_comp_channel")
public class CompletionChannel extends Struct implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompletionChannel.class);

    private final Context context = referenceField("context");
    private NativeInteger fd = integerField("fd");

    CompletionChannel(final long handle) {
        super(handle);
    }

    public Context getContext() {
        return context;
    }

    public int getFileDescriptor() {
        return fd.get();
    }

    @Nullable
    public CompletionQueue getCompletionEvent() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        var then = System.currentTimeMillis();
        Verbs.getCompletionEvent(getHandle(), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Polling completion event from completion channel failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        if (System.currentTimeMillis() - then > Duration.ofSeconds(1).toMillis()) {
            LOGGER.warn("Waited {} seconds for completion event", (System.currentTimeMillis() - then) / 1000);
        }

        return result.getAndRelease(NativeObjectRegistry::getObject);
    }

    @Override
    public void close() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.destroyCompletionChannel(getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Destroying completion channel failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        } else {
            NativeObjectRegistry.deregisterObject(this);
        }

        result.releaseInstance();
    }
}
