package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.EnumConverter;
import de.hhu.bsinfo.neutrino.data.NativeEnum;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueuePair implements NativeObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueuePair.class);

    private final long handle;

    protected QueuePair(long handle) {
        this.handle = handle;
    }

    @Override
    public long getHandle() {
        return handle;
    }

    public void post(final SendWorkRequest sendWorkRequest) {
        var result = (Result) Verbs.getNativeObjectPool(Result.class).newInstance();

        Verbs.postSendWorkRequest(handle, sendWorkRequest.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Posting send work request failed");
        }

        result.free();
    }

    public void post(final ReceiveWorkRequest receiveWorkRequest) {
        var result = (Result) Verbs.getNativeObjectPool(Result.class).newInstance();

        Verbs.postReceiveWorkRequest(handle, receiveWorkRequest.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Posting send work request failed");
        }

        result.free();
    }

    public enum Type {
        RC(2), UC(3), UD(4), RAW_PACKET(8), XRC_SEND(9), XRC_RECV(10), DRIVER(0xFF);

        private static final Type[] VALUES;

        static {
            int arrayLength = Arrays.stream(values()).mapToInt(element -> element.value).max().orElseThrow() + 1;

            VALUES = new Type[arrayLength];

            for (Type element : Type.values()) {
                VALUES[element.value] = element;
            }
        }

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static final EnumConverter<Type> CONVERTER = new EnumConverter<>() {

            @Override
            public int toInt(Type enumeration) {
                return enumeration.value;
            }

            @Override
            public Type toEnum(int integer) {
                if (integer < RC.value || integer > UD.value && integer < RAW_PACKET.value
                                       || integer > XRC_RECV.value && integer < DRIVER.value
                                       || integer > DRIVER.value) {
                    throw new IllegalArgumentException(String.format("Unkown operation code provided %d", integer));
                }

                return VALUES[integer];
            }
        };
    }

    public static final class Attributes extends Struct {

        private final NativeLong userContext = longField("qp_context");
        private final NativeLong sendCompletionQueue = longField("send_cq");
        private final NativeLong receiveCompletionQueue = longField("recv_cq");
        private final NativeLong sharedReceiveQueue = longField("srq");
        private final NativeLong capabilities = longField("cap");
        private final NativeEnum<Type> type = enumField("qp_type", Type.CONVERTER);
        private final NativeInteger signalLevel = integerField("sq_sig_all");

        public Attributes() {
            super("ibv_qp_init_attr");
        }

        public long getUserContext() {
            return userContext.get();
        }

        public void setUserContext(long userContext) {
            this.userContext.set(userContext);
        }

        public long getSendCompletionQueue() {
            return sendCompletionQueue.get();
        }

        public void setSendCompletionQueue(long sendCompletionQueue) {
            this.sendCompletionQueue.set(sendCompletionQueue);
        }

        public long getReceiveCompletionQueue() {
            return receiveCompletionQueue.get();
        }

        public void setReceiveCompletionQueue(long receiveCompletionQueue) {
            this.receiveCompletionQueue.set(receiveCompletionQueue);
        }

        public long getSharedReceiveQueue() {
            return sharedReceiveQueue.get();
        }

        public void setSharedReceiveQueue(long sharedReceiveQueue) {
            this.sharedReceiveQueue.set(sharedReceiveQueue);
        }

        public long getCapabilities() {
            return capabilities.get();
        }

        public void setCapabilities(long capabilities) {
            this.capabilities.set(capabilities);
        }

        public Type getType() {
            return type.get();
        }

        public void setType(Type type) {
            this.type.set(type);
        }

        public int getSignalLevel() {
            return signalLevel.get();
        }

        public void setSignalLevel(int signalLevel) {
            this.signalLevel.set(signalLevel);
        }
    }

    public static final class Capabilities extends Struct {

        private final NativeInteger maxSendWorkRequests = integerField("max_send_wr");
        private final NativeInteger maxReceiveWorkRequests = integerField("max_recv_wr");
        private final NativeInteger maxSendScatterGatherElements = integerField("max_send_sge");
        private final NativeInteger maxReceiveScatterGatherElements = integerField("max_recv_sge");
        private final NativeInteger maxInlineData = integerField("max_inline_data");

        public Capabilities() {
            super("ibv_qp_cap");
        }

        public int getMaxSendWorkRequests() {
            return maxSendWorkRequests.get();
        }

        public void setMaxSendWorkRequests(int maxSendWorkRequests) {
            this.maxSendWorkRequests.set(maxSendWorkRequests);
        }

        public int getMaxReceiveWorkRequests() {
            return maxReceiveWorkRequests.get();
        }

        public void setMaxReceiveWorkRequests(int maxReceiveWorkRequests) {
            this.maxReceiveWorkRequests.set(maxReceiveWorkRequests);
        }

        public int getMaxSendScatterGatherElements() {
            return maxSendScatterGatherElements.get();
        }

        public void setMaxSendScatterGatherElements(int maxSendScatterGatherElements) {
            this.maxSendScatterGatherElements.set(maxSendScatterGatherElements);
        }

        public int getMaxReceiveScatterGatherElements() {
            return maxReceiveScatterGatherElements.get();
        }

        public void setMaxReceiveScatterGatherElements(int maxReceiveScatterGatherElements) {
            this.maxReceiveScatterGatherElements.set(maxReceiveScatterGatherElements);
        }

        public int getMaxInlineData() {
            return maxInlineData.get();
        }

        public void setMaxInlineData(int maxInlineData) {
            this.maxInlineData.set(maxInlineData);
        }
    }
}
