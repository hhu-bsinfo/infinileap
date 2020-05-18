package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.struct.field.NativeArray;
import de.hhu.bsinfo.neutrino.struct.field.NativeInteger;
import de.hhu.bsinfo.neutrino.struct.field.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.struct.LinkNative;
import de.hhu.bsinfo.neutrino.util.NativeObjectRegistry;
import de.hhu.bsinfo.neutrino.util.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;

@LinkNative("ibv_cq")
public class CompletionQueue extends Struct implements AutoCloseable {

    public enum NotificationType {
        ALL(0x0),
        SOLICITED(0x1);

        private final int value;

        NotificationType(int value) {
            this.value = value;
        }

        public int get() {
            return value;
        }
    }

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

    public void poll(WorkCompletionArray results) throws IOException {
        poll(results, results.getCapacity());
    }

    public void poll(WorkCompletionArray results, int entries) throws IOException {
        var result = Result.localInstance();

        Verbs.pollCompletionQueue(getHandle(), entries, results.getHandle(), result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        results.setLength(result.intValue());
    }

    public void requestNotification() throws IOException {
        requestNotification(NotificationType.ALL);
    }

    public void requestNotification(NotificationType type) throws IOException {
        var result = Result.localInstance();

        Verbs.requestNotification(getHandle(), type.get(), result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            throw new IOException(SystemUtil.getErrorMessage());
        }
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
    public void close() throws IOException {
        var result = Result.localInstance();

        Verbs.destroyCompletionQueue(getHandle(), result.getHandle());
        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        NativeObjectRegistry.deregisterObject(this);
    }

    public static class WorkCompletionArray extends NativeArray<WorkCompletion> {

        private int length;

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