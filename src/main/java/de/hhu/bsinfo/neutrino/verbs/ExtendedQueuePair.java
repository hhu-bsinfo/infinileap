package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.*;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.Flag;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

@LinkNative("ibv_qp_ex")
public class ExtendedQueuePair extends Struct {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedQueuePair.class);

    private final NativeLong compatibilityMask = longField("comp_mask");
    private final NativeLong workRequestId = longField("wr_id");
    private final NativeInteger workRequestFlags = integerField("wr_flags");

    public final QueuePair baseQueuePair = valueField("qp_base", QueuePair::new);

    ExtendedQueuePair(final long handle) {
        super(handle);
    }

    public void startWorkRequest() {
        Verbs.startWorkRequest(getHandle());
    }

    public void completeWorkRequest() {
        Verbs.completeWorkRequest(getHandle());
    }

    public void abortWorkRequest() {
        Verbs.abortWorkRequest(getHandle());
    }

    public void atomicCompareAndSwap(int remoteKey, long remoteAddress, long compare, long swap) {
        Verbs.atomicCompareAndSwap(getHandle(), remoteKey, remoteAddress, compare, swap);
    }

    public void atomicFetchAndAdd(int remoteKey, long remoteAddress, long add) {
        Verbs.atomicFetchAndAdd(getHandle(), remoteKey, remoteAddress, add);
    }

    public void bindMemoryWindow(MemoryWindow memoryWindow, int remoteKey, MemoryWindow.BindInformation bindInformation) {
        Verbs.bindMemoryWindow(getHandle(), memoryWindow.getHandle(), remoteKey, bindInformation.getHandle());
    }

    public void invalidRemoteKey(int remoteKey) {
        Verbs.invalidateRemoteKey(getHandle(), remoteKey);
    }

    public void rdmaRead(int remoteKey, long remoteAddress) {
        Verbs.rdmaRead(getHandle(), remoteKey, remoteAddress);
    }

    public void rdmaWrite(int remoteKey, long remoteAddress) {
        Verbs.rdmaWrite(getHandle(), remoteKey, remoteAddress);
    }

    public void rdmaWriteWithImmediateData(int remoteKey, long remoteAddress, int immediateData) {
        Verbs.rdmaWriteImm(getHandle(), remoteKey, remoteAddress, immediateData);
    }

    public void send() {
        Verbs.send(getHandle());
    }

    public void sendWithImmediateData(int immediateData) {
        Verbs.sendImm(getHandle(), immediateData);
    }

    public void sendInvalidateRemoteKey(int remoteKey) {
        Verbs.sendInvalidateRemoteKey(getHandle(), remoteKey);
    }

    public void sendTcpSegmentOffload(long hdrHandle, short hdrSize, short mss) {
        Verbs.sendTcpSegmentOffload(getHandle(), hdrHandle, hdrSize, mss);
    }

    public void setUnreliableAddress(AddressHandle addressHandle, int remoteQueuePairNumber, int remoteQKey) {
        Verbs.setUnreliableAddress(getHandle(), addressHandle.getHandle(), remoteQueuePairNumber, remoteQKey);
    }

    public void setExtendedSharedReceiveQueueNumber(int sharedReceiveQueueNumber) {
        Verbs.setExtendedSharedReceiveQueueNumber(getHandle(), sharedReceiveQueueNumber);
    }

    public void setInlineData(InlineData data) {
        Verbs.setInlineData(getHandle(), data.getAddress(), data.getLength());
    }

    public void setInlineDataList(NativeArray<InlineData> bufferList) {
        Verbs.setInlineDataList(getHandle(), bufferList.getCapacity(), bufferList.getHandle());
    }

    public void setScatterGatherElement(ScatterGatherElement element) {
        Verbs.setScatterGatherElement(getHandle(), element.getLocalKey(), element.getAddress(), element.getLength());
    }

    public void setScatterGatherElementList(NativeArray<ScatterGatherElement> elementList) {
        Verbs.setScatterGatherElementList(getHandle(), elementList.getCapacity(), elementList.getHandle());
    }

    public long getCompatibilityMask() {
        return compatibilityMask.get();
    }

    public long getWorkRequestId() {
        return workRequestId.get();
    }

    public int getWorkRequestFlags() {
        return workRequestFlags.get();
    }

    @Override
    public String toString() {
        return "ExtendedQueuePair {" +
                "\n\tcompatibilityMask=" + compatibilityMask +
                ",\n\tworkRequestId=" + workRequestId +
                ",\n\tworkRequestFlags=" + workRequestFlags +
                ",\n\tbaseQueuePair=" + baseQueuePair +
                "\n}";
    }

    public enum AttributeFlag implements Flag {
        PD(1), XRCD(1 << 1), CREATE_FLAGS(1 << 2), MAX_TSO_HEADER(1 << 3),
        IND_TABLE(1 << 4), RX_HASH(1 << 5), SEND_OPS_FLAGS(1 << 6);

        private final int value;

        AttributeFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    public enum CreationFlag implements Flag {
        BLOCK_SELF_MCAST_LB(1 << 1), SCATTER_FCS(1 << 8), CVLAN_STRIPPING(1 << 9),
        SOURCE_QPN(1 << 10), PCI_WRITE_END_PADDING(1 << 11);

        private final int value;

        CreationFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    public enum SendOperationFlag implements Flag {
        WITH_RDMA_WRITE(1), WITH_RDMA_WRITE_WITH_IMM(1 << 1), WITH_SEND(1 << 2), WITH_SEND_WITH_IMM(1 << 3),
        WITH_RDMA_READ(1 << 4), WITH_ATOMIC_CMP_AND_SWP(1 << 5), WITH_ATOMIC_FETCH_AND_ADD(1 << 6),WITH_LOCAL_INV(1 << 7),
        WITH_BIND_MW(1 << 8), WITH_SEND_WITH_INV(1 << 9), WITH_TSO(1 << 10);

        private final int value;

        SendOperationFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    public enum HashFunctionFlag implements Flag {
        TOEPLITZ(1);

        private final int value;

        HashFunctionFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    public enum HashFieldFlag implements Flag {
        SRC_IPV4(1), DST_IPV4(1 << 1), SRC_IPV6(1 << 2), DST_IPV6(1 << 3),
        SRC_PORT_TCP(1 << 4), DST_PORT_TCP(1 << 5), SRC_PORT_UDP(1 << 6), DST_PORT_UDP(1 << 7),
        IPSEC_SPI(1 << 8), INNER(1 << 31);

        private final int value;

        HashFieldFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    @LinkNative("ibv_data_buf")
    public static final class InlineData extends Struct {

        private final NativeLong address = longField("addr");
        private final NativeLong length = longField("length");

        public InlineData() {}

        public InlineData(final Consumer<InlineData> configurator) {
            configurator.accept(this);
        }

        public long getAddress() {
            return address.get();
        }

        public long getLength() {
            return length.get();
        }

        public void setAddress(final long value) {
            address.set(value);
        }

        public void setLength(final long value) {
            length.set(value);
        }

        @Override
        public String toString() {
            return "InlineData {" +
                    "\n\taddress=" + address +
                    ",\n\tlength=" + length +
                    "\n}";
        }
    }

    @LinkNative("ibv_rx_hash_conf")
    public static final class ReceiveHashConfiguration extends Struct {

        private final NativeByteBitMask<HashFunctionFlag> function = byteBitField("rx_hash_function");
        private final NativeByte keyLength = byteField("rx_hash_key_len");
        private final NativeLong key = longField("rx_hash_key");
        private final NativeLongBitMask<HashFieldFlag> fieldsMask = longBitField("rx_hash_fields_mask");

        public ReceiveHashConfiguration() {}

        public ReceiveHashConfiguration(LocalBuffer byteBuffer, long offset) {
            super(byteBuffer, offset);
        }

        public byte getFunction() {
            return function.get();
        }

        public byte getKeyLength() {
            return keyLength.get();
        }

        public long getKey() {
            return key.get();
        }

        public long getFieldsMask() {
            return fieldsMask.get();
        }

        public void setFunction(final HashFunctionFlag... flags) {
            function.set(flags);
        }

        public void setKeyLength(final byte value) {
            keyLength.set(value);
        }

        public void setKey(final long value) {
            key.set(value);
        }

        public void setFieldsMask(final HashFieldFlag... flags) {
            fieldsMask.set(flags);
        }

        @Override
        public String toString() {
            return "ReceiveHashConfiguration {" +
                    "\n\tfunction=" + function +
                    ",\n\tkeyLength=" + keyLength +
                    ",\n\tkey=" + key +
                    ",\n\tfieldsMask=" + fieldsMask +
                    "\n}";
        }
    }

    @LinkNative("ibv_qp_init_attr_ex")
    public static final class InitialAttributes extends Struct {

        // Traditional Queue Pair attributes
        private final NativeLong userContext = longField("qp_context");
        private final NativeLong sendCompletionQueue = longField("send_cq");
        private final NativeLong receiveCompletionQueue = longField("recv_cq");
        private final NativeLong sharedReceiveQueue = longField("srq");
        private final NativeEnum<QueuePair.Type> type = enumField("qp_type", QueuePair.Type.CONVERTER);
        private final NativeInteger signalLevel = integerField("sq_sig_all");

        public final QueuePair.Capabilities capabilities = valueField("cap", QueuePair.Capabilities::new);

        // Extended Queue Pair attributes
        private final NativeIntegerBitMask<AttributeFlag> attributeMask = intBitField("comp_mask");
        private final NativeLong protectionDomain = longField("pd");
        private final NativeLong extendedConnectionDomain = longField("xrcd");
        private final NativeIntegerBitMask<CreationFlag> creationFlags = intBitField("create_flags");
        private final NativeShort maxTcpSegmentationOffloadHeader = shortField("max_tso_header");
        private final NativeLong receiveWorkQueueIndirectionTable = longField("rwq_ind_tbl");
        private final NativeInteger sourceQueuePairNumber = integerField("source_qpn");
        private final NativeLongBitMask<SendOperationFlag> sendOperationFlags = longBitField("send_ops_flags");

        public final ReceiveHashConfiguration receiveHashConfiguration = valueField("rx_hash_conf", ReceiveHashConfiguration::new);

        public InitialAttributes() {}

        public InitialAttributes(final Consumer<InitialAttributes> configurator) {
            configurator.accept(this);
        }

        public long getUserContext() {
            return userContext.get();
        }

        public long getSendCompletionQueue() {
            return sendCompletionQueue.get();
        }

        public long getReceiveCompletionQueue() {
            return receiveCompletionQueue.get();
        }

        public long getSharedReceiveQueue() {
            return sharedReceiveQueue.get();
        }

        public QueuePair.Type getType() {
            return type.get();
        }

        public int getSignalLevel() {
            return signalLevel.get();
        }

        public int getAttributeMask() {
            return attributeMask.get();
        }

        public long getProtectionDomain() {
            return protectionDomain.get();
        }

        public long getExtendedConnectionDomain() {
            return extendedConnectionDomain.get();
        }

        public int getCreationFlags() {
            return creationFlags.get();
        }

        public short getMaxTcpSegmentationOffloadHeader() {
            return maxTcpSegmentationOffloadHeader.get();
        }

        public long getReceiveWorkQueueIndirectionTable() {
            return receiveWorkQueueIndirectionTable.get();
        }

        public int getSourceQueuePairNumber() {
            return sourceQueuePairNumber.get();
        }

        public long getSendOperationFlags() {
            return sendOperationFlags.get();
        }

        public void setUserContext(final long value) {
            userContext.set(value);
        }

        public void setSendCompletionQueue(final CompletionQueue sendCompletionQueue) {
            this.sendCompletionQueue.set(sendCompletionQueue.getHandle());
        }

        public void setReceiveCompletionQueue(final CompletionQueue receiveCompletionQueue) {
            this.receiveCompletionQueue.set(receiveCompletionQueue.getHandle());
        }

        public void setSharedReceiveQueue(final SharedReceiveQueue sharedReceiveQueue) {
            this.sharedReceiveQueue.set(sharedReceiveQueue.getHandle());
        }

        public void setType(final QueuePair.Type value) {
            type.set(value);
        }

        public void setSignalLevel(final int value) {
            signalLevel.set(value);
        }

        public void setAttributeMask(final AttributeFlag... flags) {
            attributeMask.set(flags);
        }

        public void setProtectionDomain(final ProtectionDomain protectionDomain) {
            this.protectionDomain.set(protectionDomain.getHandle());
        }

        public void setExtendedConnectionDomain(final ExtendedConnectionDomain extendedConnectionDomain) {
            this.extendedConnectionDomain.set(extendedConnectionDomain.getHandle());
        }

        public void setCreationFlags(final CreationFlag... flags) {
            creationFlags.set(flags);
        }

        public void setMaxTcpSegmentationOffloadHeader(final short value) {
            maxTcpSegmentationOffloadHeader.set(value);
        }

        public void setReceiveWorkQueueIndirectionTable(final ReceiveWorkQueueIndirectionTable indirectionTable) {
            receiveWorkQueueIndirectionTable.set(indirectionTable.getHandle());
        }

        public void setSourceQueuePairNumber(final int value) {
            sourceQueuePairNumber.set(value);
        }

        public void setSendOperationFlags(final SendOperationFlag... flags) {
            sendOperationFlags.set(flags);
        }

        @Override
        public String toString() {
            return "InitialAttributes {" +
                    "\n\tuserContext=" + userContext +
                    ",\n\tsendCompletionQueue=" + sendCompletionQueue +
                    ",\n\treceiveCompletionQueue=" + receiveCompletionQueue +
                    ",\n\tsharedReceiveQueue=" + sharedReceiveQueue +
                    ",\n\tcapabilities=" + capabilities +
                    ",\n\ttype=" + type +
                    ",\n\tsignalLevel=" + signalLevel +
                    ",\n\tcompatibilityMask=" + attributeMask +
                    ",\n\tprotectionDomain=" + protectionDomain +
                    ",\n\textendedConnectionDomain=" + extendedConnectionDomain +
                    ",\n\tcreationFlags=" + creationFlags +
                    ",\n\tmaxTcpSegmentationOffloadHeader=" + maxTcpSegmentationOffloadHeader +
                    ",\n\treceiveWorkQueueIndirectionTable=" + receiveWorkQueueIndirectionTable +
                    ",\n\treceiveHashConfiguration=" + receiveHashConfiguration +
                    ",\n\tsourceQueuePairNumber=" + sourceQueuePairNumber +
                    ",\n\tsendOperationFlags=" + sendOperationFlags +
                    "\n}";
        }
    }
}
