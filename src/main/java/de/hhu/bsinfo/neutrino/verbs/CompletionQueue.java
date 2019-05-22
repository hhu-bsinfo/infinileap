package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompletionQueue extends Struct {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompletionQueue.class);

    private final NativeLong contextHandle = longField("context");
    private final NativeLong channelHandle = longField("channel");
    private final NativeLong userContextHandle = longField("cq_context");
    private final NativeInteger queueHandle = integerField("handle");
    private final NativeInteger maxElements = integerField("cqe");

    public CompletionQueue() {
        super("ibv_cq");
    }

    public CompletionQueue(long handle) {
        super("ibv_cq", handle);
    }

    public long getContextHandle() {
        return contextHandle.get();
    }

    public void setContextHandle(long contextHandle) {
        this.contextHandle.set(contextHandle);
    }

    public long getChannelHandle() {
        return channelHandle.get();
    }

    public void setChannelHandle(long channelHandle) {
        this.channelHandle.set(channelHandle);
    }

    public long getUserContextHandle() {
        return userContextHandle.get();
    }

    public void setUserContextHandle(long userContextHandle) {
        this.userContextHandle.set(userContextHandle);
    }

    public int getQueueHandle() {
        return queueHandle.get();
    }

    public void setQueueHandle(int queueHandle) {
        this.queueHandle.set(queueHandle);
    }

    public int getMaxElements() {
        return maxElements.get();
    }

    public void setMaxElements(int maxElements) {
        this.maxElements.set(maxElements);
    }

    public boolean destroy() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.destroyCompletionQueue(getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Could not destroy completion queue [{}]", result.getStatus());
            result.releaseInstance();
            return false;
        }

        result.releaseInstance();
        return true;
    }

    public void poll() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        // TODO(krakowski)
        //  Implement native object array and pass it to pollCompletionQueue

        Verbs.pollCompletionQueue(getHandle(), 0, 0, result.getHandle());
        if (result.isError()) {
            LOGGER.error("Polling completion queue failed");
            result.releaseInstance();
            return;
        }

        result.releaseInstance();
    }

    @Override
    public String toString() {
        return "CompletionQueue {" +
            "\n\tcontextHandle=" + contextHandle +
            ",\n\tchannelHandle=" + channelHandle +
            ",\n\tuserContextHandle=" + userContextHandle +
            ",\n\tqueueHandle=" + queueHandle +
            ",\n\tmaxElements=" + maxElements +
            "\n}";
    }
}