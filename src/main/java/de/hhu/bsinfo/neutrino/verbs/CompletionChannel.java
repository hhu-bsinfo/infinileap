package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LinkNative("ibv_comp_channel")
public class CompletionChannel extends Struct implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompletionChannel.class);

    private final Context context = referenceField("context", Context::new);

    CompletionChannel(final long handle) {
        super(handle);
    }

    public Context getContext() {
        return context;
    }

    @Nullable
    public CompletionQueue getCompletionEvent() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.getCompletionEvent(getHandle(), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Polling completion event from completion channel failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        return result.getAndRelease(CompletionQueue::new);
    }

    @Override
    public void close() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.destroyCompletionChannel(getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Destroying completion channel failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        result.releaseInstance();
    }
}
