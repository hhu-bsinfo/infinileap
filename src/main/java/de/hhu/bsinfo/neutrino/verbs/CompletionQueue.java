package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.EnumConverter;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.struct.StructInformation;
import de.hhu.bsinfo.neutrino.util.StructUtil;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompletionQueue extends Struct {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompletionQueue.class);

    private static final StructInformation INFO = StructUtil.getInfo("ibv_cq");
    public static final int SIZE = INFO.structSize.get();

    private final NativeLong contextHandle = new NativeLong(getByteBuffer(), INFO.getOffset("context"));
    private final NativeLong channelHandle = new NativeLong(getByteBuffer(), INFO.getOffset("channel"));
    private final NativeLong userContextHandle = new NativeLong(getByteBuffer(), INFO.getOffset("cq_context"));
    private final NativeInteger queueHandle = new NativeInteger(getByteBuffer(), INFO.getOffset("handle"));
    private final NativeInteger maxElements = new NativeInteger(getByteBuffer(), INFO.getOffset("cqe"));

    public CompletionQueue() {
        super(SIZE);
    }

    public CompletionQueue(long handle) {
        super(handle, SIZE);
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
        var result = new Result();
        Verbs.destroyCompletionQueue(getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Could not destroy completion queue [{}]", result.getStatus());
            return false;
        }

        return true;
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