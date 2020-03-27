package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.NativeError;
import de.hhu.bsinfo.neutrino.util.NativeObjectRegistry;
import de.hhu.bsinfo.neutrino.util.SystemUtil;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

@LinkNative("ibv_comp_channel")
public class CompletionChannel extends Struct implements AutoCloseable {

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

    public CompletionQueue getCompletionEvent() {
        var result = Result.localInstance();

        Verbs.getCompletionEvent(getHandle(), result.getHandle());
        if(result.isError()) {
            throw new NativeError(SystemUtil.getErrorMessage());
        }

        return result.get(NativeObjectRegistry::getObject);
    }

    public void discardCompletionEvent() {
        var result = Result.localInstance();

        Verbs.getCompletionEvent(getHandle(), result.getHandle());
        if (result.isError()) {
            throw new NativeError(SystemUtil.getErrorMessage());
        }
    }

    @Override
    public void close() {
        var result = Result.localInstance();

        Verbs.destroyCompletionChannel(getHandle(), result.getHandle());
        if (result.isError()) {
            throw new NativeError(SystemUtil.getErrorMessage());
        }

        NativeObjectRegistry.deregisterObject(this);
    }
}
