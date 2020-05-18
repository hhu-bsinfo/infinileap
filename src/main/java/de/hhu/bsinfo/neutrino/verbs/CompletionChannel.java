package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.struct.field.NativeInteger;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.struct.LinkNative;
import de.hhu.bsinfo.neutrino.util.NativeError;
import de.hhu.bsinfo.neutrino.util.NativeObjectRegistry;
import de.hhu.bsinfo.neutrino.util.SystemUtil;

import java.io.IOException;

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

    public CompletionQueue getCompletionEvent() throws IOException {
        var result = Result.localInstance();

        Verbs.getCompletionEvent(getHandle(), result.getHandle());
        if(result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        return result.get(NativeObjectRegistry::getObject);
    }

    public void discardCompletionEvent() throws IOException {
        var result = Result.localInstance();

        Verbs.getCompletionEvent(getHandle(), result.getHandle());
        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }
    }

    @Override
    public void close() throws IOException {
        var result = Result.localInstance();

        Verbs.destroyCompletionChannel(getHandle(), result.getHandle());
        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        NativeObjectRegistry.deregisterObject(this);
    }
}
