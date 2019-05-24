package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeArray;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.ReferenceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LinkNative("ibv_cq")
public class CompletionQueue extends Struct {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompletionQueue.class);

    private final NativeLong contextHandle = longField("context");
    private final NativeLong channelHandle = longField("channel");
    private final NativeLong userContextHandle = longField("cq_context");
    private final NativeInteger queueHandle = integerField("handle");
    private final NativeInteger maxElements = integerField("cqe");

    public CompletionQueue() {}

    public CompletionQueue(long handle) {
        super(handle);
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
        boolean isError = result.isError();
        if (isError) {
            LOGGER.error("Destroying completion queue failed [{}]", result.getStatus());
        }

        result.releaseInstance();

        return !isError;
    }

    public boolean poll(WorkCompletionArray results) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.pollCompletionQueue(getHandle(), results.getCapacity(), result.getHandle(), result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            LOGGER.error("Modifying queue pair failed [{}]", result.getStatus());
            results.setLength(0);
        } else {
            results.setLength(result.intValue());
        }

        result.releaseInstance();

        return !isError;
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

    public static class WorkCompletionArray extends NativeArray<WorkCompletion> {

        private int length;

        public WorkCompletionArray(long handle, int capacity) {
            super(WorkCompletion::new, WorkCompletion.class, handle, capacity);
        }

        public WorkCompletionArray(int capacity) {
            super(WorkCompletion::new, WorkCompletion.class, capacity);
        }

        @Override
        public WorkCompletion get(int index) {
            if (index >= length) {
                throw new IndexOutOfBoundsException(String.format("Index %d is outside array content with length %d", index, length));
            }

            return super.get(index);
        }

        public int getLength() {
            return length;
        }

        void setLength(int length) {
            this.length = length;
        }
    }
}