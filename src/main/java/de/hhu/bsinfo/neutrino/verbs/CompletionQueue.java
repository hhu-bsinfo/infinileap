package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeArray;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.NativeObjectRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

@LinkNative("ibv_cq")
public class CompletionQueue extends Struct implements AutoCloseable {

    public static final boolean SOLICITED_EVENTS_ONLY = true;
    public static final boolean ALL_EVENTS = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(CompletionQueue.class);

    private final Context context = referenceField("context");
    private final CompletionChannel completionChannel = referenceField("channel");
    private final NativeLong userContextHandle = longField("cq_context");
    private final NativeInteger maxElements = integerField("cqe");

    CompletionQueue(long handle) {
        super(handle);
    }

    public Context getContext() {
        return context;
    }

    public CompletionChannel getCompletionChannel() {
        return completionChannel;
    }

    public long getUserContextHandle() {
        return userContextHandle.get();
    }

    public int getMaxElements() {
        return maxElements.get();
    }

    public boolean poll(WorkCompletionArray results) {
        return poll(results, results.getCapacity());
    }

    public boolean poll(WorkCompletionArray results, int entries) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.pollCompletionQueue(getHandle(), entries, results.getHandle(), result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            LOGGER.error("Polling completion queue failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
            results.setLength(0);
        } else {
            results.setLength(result.intValue());
        }

        result.releaseInstance();

        return !isError;
    }

    public boolean requestNotification() {
        return requestNotification(ALL_EVENTS);
    }

    public boolean requestNotification(boolean solicitedOnly) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.requestNotification(getHandle(), solicitedOnly ? 1 : 0, result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            LOGGER.error("Requesting notification from completion queue failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        result.releaseInstance();

        return !isError;
    }

    public void acknowledgeEvent() {
        Verbs.acknowledgeCompletionEvents(getHandle(), 1);
    }

    public void acknowledgeEvents(int count) {
        Verbs.acknowledgeCompletionEvents(getHandle(), count);
    }

    @Override
    public String toString() {
        return "CompletionQueue {" +
            ",\n\tcompletionChannel=" + completionChannel.getHandle() +
            ",\n\tuserContextHandle=" + userContextHandle +
            ",\n\tmaxElements=" + maxElements +
            "\n}";
    }

    @Override
    public void close() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.destroyCompletionQueue(getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Destroying completion queue failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        } else {
            NativeObjectRegistry.deregisterObject(this);
        }

        result.releaseInstance();
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


        @Override
        @SuppressWarnings("unchecked")
        public <S extends NativeArray<WorkCompletion>> S forEach(Consumer<WorkCompletion> operation) {
            for (int i = 0; i < length; i++) {
                operation.accept(getUnchecked(i));
            }

            return (S) this;
        }

        public boolean isEmpty() {
            return length == 0;
        }

        public int getLength() {
            return length;
        }

        void setLength(int length) {
            this.length = length;
        }
    }
}