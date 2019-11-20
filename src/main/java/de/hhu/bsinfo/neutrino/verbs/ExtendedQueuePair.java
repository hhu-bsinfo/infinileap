package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.*;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.Flag;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.NativeObjectRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@LinkNative("ibv_qp_ex")
public class ExtendedQueuePair extends Struct {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedQueuePair.class);

    private final NativeLong compatibilityMask = longField("comp_mask");
    private final NativeLong workRequestId = longField("wr_id");
    private final NativeIntegerBitMask<SendWorkRequest.SendFlag> workRequestFlags = integerBitField("wr_flags");

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

    public void sendTcpSegmentOffload(long headerHandle, short headerSize, short maxSegmentSize) {
        Verbs.sendTcpSegmentOffload(getHandle(), headerHandle, headerSize, maxSegmentSize);
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

    public void setWorkRequestId(long id) {
        workRequestId.set(id);
    }

    public void setWorkRequestFlags(SendWorkRequest.SendFlag... flags) {
        workRequestFlags.set(flags);
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
        public long getValue() {
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
        public long getValue() {
            return value;
        }
    }

    public enum SendOperationFlag implements Flag {
        WITH_RDMA_WRITE(1), WITH_RDMA_WRITE_WITH_IMM(1 << 1), WITH_SEND(1 << 2), WITH_SEND_WITH_IMM(1 << 3),
        WITH_RDMA_READ(1 << 4), WITH_ATOMIC_CMP_AND_SWP(1 << 5), WITH_ATOMIC_FETCH_AND_ADD(1 << 6),WITH_LOCAL_INV(1 << 7),
        WITH_BIND_MW(1 << 8), WITH_SEND_WITH_INV(1 << 9), WITH_TSO(1 << 10);

        private final long value;

        SendOperationFlag(long value) {
            this.value = value;
        }

        @Override
        public long getValue() {
            return value;
        }
    }

    public enum HashFunctionFlag implements Flag {
        TOEPLITZ((byte) 1);

        private final byte value;

        HashFunctionFlag(byte value) {
            this.value = value;
        }

        @Override
        public long getValue() {
            return value;
        }
    }

    public enum HashFieldFlag implements Flag {
        SRC_IPV4(1), DST_IPV4(1 << 1), SRC_IPV6(1 << 2), DST_IPV6(1 << 3),
        SRC_PORT_TCP(1 << 4), DST_PORT_TCP(1 << 5), SRC_PORT_UDP(1 << 6), DST_PORT_UDP(1 << 7),
        IPSEC_SPI(1 << 8), INNER(1 << 31);

        private final long value;

        HashFieldFlag(long value) {
            this.value = value;
        }

        @Override
        public long getValue() {
            return value;
        }
    }

    @LinkNative("ibv_data_buf")
    public static final class InlineData extends Struct {

        private final NativeLong address = longField("addr");
        private final NativeLong length = longField("length");

        public InlineData(final int address, final int length) {
            this.address.set(address);
            this.length.set(length);
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
        private final NativeLongBitMask<HashFieldFlag> fieldMask = longBitField("rx_hash_fields_mask");
        
        ReceiveHashConfiguration(final LocalBuffer buffer, final long offset) {
            super(buffer, offset);
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

        public long getFieldMask() {
            return fieldMask.get();
        }

        void setFunction(final HashFunctionFlag function) {
            this.function.set(function);
        }

        void setKeyLength(final byte value) {
            keyLength.set(value);
        }

        void setKey(final LocalBuffer key) {
            this.key.set(key.getHandle());
        }

        void setFieldMask(final HashFieldFlag... flags) {
            fieldMask.set(flags);
        }

        @Override
        public String toString() {
            return "ReceiveHashConfiguration {" +
                    "\n\tfunction=" + function +
                    ",\n\tkeyLength=" + keyLength +
                    ",\n\tkey=" + key +
                    ",\n\tfieldsMask=" + fieldMask +
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
        private final NativeIntegerBitMask<AttributeFlag> attributeMask = integerBitField("comp_mask");
        private final NativeLong protectionDomain = longField("pd");
        private final NativeLong extendedConnectionDomain = longField("xrcd");
        private final NativeIntegerBitMask<CreationFlag> creationFlags = integerBitField("create_flags");
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

        public CompletionQueue getSendCompletionQueue() {
            return NativeObjectRegistry.getObject(sendCompletionQueue.get());
        }

        public CompletionQueue getReceiveCompletionQueue() {
            return NativeObjectRegistry.getObject(receiveCompletionQueue.get());
        }

        public SharedReceiveQueue getSharedReceiveQueue() {
            return NativeObjectRegistry.getObject(sharedReceiveQueue.get());
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

        public ProtectionDomain getProtectionDomain() {
            return NativeObjectRegistry.getObject(protectionDomain.get());
        }

        public ExtendedConnectionDomain getExtendedConnectionDomain() {
            return NativeObjectRegistry.getObject(extendedConnectionDomain.get());
        }

        public int getCreationFlags() {
            return creationFlags.get();
        }

        public short getMaxTcpSegmentationOffloadHeader() {
            return maxTcpSegmentationOffloadHeader.get();
        }

        public ReceiveWorkQueueIndirectionTable getReceiveWorkQueueIndirectionTable() {
            return NativeObjectRegistry.getObject(receiveWorkQueueIndirectionTable.get());
        }

        public int getSourceQueuePairNumber() {
            return sourceQueuePairNumber.get();
        }

        public long getSendOperationFlags() {
            return sendOperationFlags.get();
        }

        void setUserContext(final long value) {
            userContext.set(value);
        }

        void setSendCompletionQueue(final CompletionQueue sendCompletionQueue) {
            this.sendCompletionQueue.set(sendCompletionQueue.getHandle());
        }

        void setReceiveCompletionQueue(final CompletionQueue receiveCompletionQueue) {
            this.receiveCompletionQueue.set(receiveCompletionQueue.getHandle());
        }

        void setSharedReceiveQueue(final SharedReceiveQueue sharedReceiveQueue) {
            this.sharedReceiveQueue.set(sharedReceiveQueue.getHandle());
        }

        void setType(final QueuePair.Type value) {
            type.set(value);
        }

        void setSignalLevel(final int value) {
            signalLevel.set(value);
        }

        void setAttributeMask(final AttributeFlag... flags) {
            attributeMask.set(flags);
        }

        void setProtectionDomain(final ProtectionDomain protectionDomain) {
            this.protectionDomain.set(protectionDomain.getHandle());
        }

        void setExtendedConnectionDomain(final ExtendedConnectionDomain extendedConnectionDomain) {
            this.extendedConnectionDomain.set(extendedConnectionDomain.getHandle());
        }

        void setCreationFlags(final CreationFlag... flags) {
            creationFlags.set(flags);
        }

        void setMaxTcpSegmentationOffloadHeader(final short value) {
            maxTcpSegmentationOffloadHeader.set(value);
        }

        void setReceiveWorkQueueIndirectionTable(final ReceiveWorkQueueIndirectionTable indirectionTable) {
            receiveWorkQueueIndirectionTable.set(indirectionTable.getHandle());
        }

        void setSourceQueuePairNumber(final int value) {
            sourceQueuePairNumber.set(value);
        }

        void setSendOperationFlags(final SendOperationFlag... flags) {
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
        
        public static final class Builder {

            // Traditional Queue Pair attributes
            private long userContext;
            private CompletionQueue sendCompletionQueue;
            private CompletionQueue receiveCompletionQueue;
            private SharedReceiveQueue sharedReceiveQueue;
            private QueuePair.Type type;
            private int signalLevel;

            // Extended Queue Pair attributes
            private final Set<AttributeFlag> attributeMask = new HashSet<>();
            private ProtectionDomain protectionDomain;
            private ExtendedConnectionDomain extendedConnectionDomain;
            private CreationFlag[] creationFlags;
            private short maxTcpSegmentationOffloadHeader;
            private ReceiveWorkQueueIndirectionTable receiveWorkQueueIndirectionTable;
            private int sourceQueuePairNumber;
            private SendOperationFlag[] sendOperationFlags;

            // Capabilities
            private int maxSendWorkRequests;
            private int maxReceiveWorkRequests;
            private int maxSendScatterGatherElements;
            private int maxReceiveScatterGatherElements;
            private int maxInlineData;

            // Receive Hash Configuration
            private HashFunctionFlag hashFunction;
            private byte keyLength;
            private LocalBuffer key;
            private HashFieldFlag[] hashFieldFlags;

            public Builder(QueuePair.Type type, ProtectionDomain protectionDomain) {
                this.type = type;
                this.protectionDomain = protectionDomain;
                attributeMask.add(AttributeFlag.PD);
            }

            public Builder withUserContext(final long userContext) {
                this.userContext = userContext;
                return this;
            }

            public Builder withSendCompletionQueue(final CompletionQueue sendCompletionQueue) {
                this.sendCompletionQueue = sendCompletionQueue;
                return this;
            }

            public Builder withReceiveCompletionQueue(final CompletionQueue receiveCompletionQueue) {
                this.receiveCompletionQueue = receiveCompletionQueue;
                return this;
            }

            public Builder withSharedReceiveQueue(final SharedReceiveQueue sharedReceiveQueue) {
                this.sharedReceiveQueue = sharedReceiveQueue;
                return this;
            }

            public Builder withSignalLevel(final int signalLevel) {
                this.signalLevel = signalLevel;
                return this;
            }

            public Builder withExtendedConnectionDomain(final ExtendedConnectionDomain extendedConnectionDomain) {
                this.extendedConnectionDomain = extendedConnectionDomain;
                attributeMask.add(AttributeFlag.XRCD);
                return this;
            }

            public Builder withCreationFlags(final CreationFlag... flags) {
                creationFlags = flags;
                attributeMask.add(AttributeFlag.CREATE_FLAGS);
                return this;
            }

            public Builder withMaxTcpSegmentationOffloadHeader(final short maxTcpSegmentationOffloadHeader) {
                this.maxTcpSegmentationOffloadHeader = maxTcpSegmentationOffloadHeader;
                attributeMask.add(AttributeFlag.MAX_TSO_HEADER);
                return this;
            }

            public Builder withReceiveWorkQueueIndirectionTable(final ReceiveWorkQueueIndirectionTable indirectionTable) {
                receiveWorkQueueIndirectionTable = indirectionTable;
                attributeMask.add(AttributeFlag.IND_TABLE);
                attributeMask.add(AttributeFlag.RX_HASH);
                return this;
            }

            public Builder withSourceQueuePairNumber(final int sourceQueuePairNumber) {
                this.sourceQueuePairNumber = sourceQueuePairNumber;
                return this;
            }

            public Builder withSendOperationFlags(final SendOperationFlag... flags) {
                sendOperationFlags = flags;
                attributeMask.add(AttributeFlag.SEND_OPS_FLAGS);
                return this;
            }

            public Builder withMaxSendWorkRequests(final int maxSendWorkRequests) {
                this.maxSendWorkRequests = maxSendWorkRequests;
                return this;
            }

            public Builder withMaxReceiveWorkRequests(final int maxReceiveWorkRequests) {
                this.maxReceiveWorkRequests = maxReceiveWorkRequests;
                return this;
            }

            public Builder withMaxSendScatterGatherElements(final int maxSendScatterGatherElements) {
                this.maxSendScatterGatherElements = maxSendScatterGatherElements;
                return this;
            }

            public Builder withMaxReceiveScatterGatherElements(final int maxReceiveScatterGatherElements) {
                this.maxReceiveScatterGatherElements = maxReceiveScatterGatherElements;
                return this;
            }

            public Builder withMaxInlineData(final int maxInlineData) {
                this.maxInlineData = maxInlineData;
                return this;
            }

            public Builder withReceiveHashFunction(HashFunctionFlag hashFunction) {
                this.hashFunction = hashFunction;
                return this;
            }

            public Builder withReceiveHashKeyLength(byte keyLength) {
                this.keyLength = keyLength;
                return this;
            }

            public Builder withReceiveHashKey(LocalBuffer key) {
                this.key = key;
                return this;
            }

            public Builder withReceiveHashFieldFlags(HashFieldFlag... flags) {
                hashFieldFlags = flags;
                return this;
            }

            public InitialAttributes build() {
                var ret = new ExtendedQueuePair.InitialAttributes();

                ret.setType(type);
                ret.setUserContext(userContext);
                ret.setSignalLevel(signalLevel);
                ret.setMaxTcpSegmentationOffloadHeader(maxTcpSegmentationOffloadHeader);
                ret.setSourceQueuePairNumber(sourceQueuePairNumber);
                ret.setProtectionDomain(protectionDomain);
                ret.setAttributeMask(attributeMask.toArray(new AttributeFlag[0]));

                if(sendCompletionQueue != null) ret.setSendCompletionQueue(sendCompletionQueue);
                if(receiveCompletionQueue != null) ret.setReceiveCompletionQueue(receiveCompletionQueue);
                if(sharedReceiveQueue != null) ret.setSharedReceiveQueue(sharedReceiveQueue);
                if(extendedConnectionDomain != null) ret.setExtendedConnectionDomain(extendedConnectionDomain);
                if(creationFlags != null) ret.setCreationFlags(creationFlags);
                if(receiveWorkQueueIndirectionTable != null) ret.setReceiveWorkQueueIndirectionTable(receiveWorkQueueIndirectionTable);
                if(sendOperationFlags != null) ret.setSendOperationFlags(sendOperationFlags);

                ret.capabilities.setMaxSendWorkRequests(maxSendWorkRequests);
                ret.capabilities.setMaxReceiveWorkRequests(maxReceiveWorkRequests);
                ret.capabilities.setMaxSendScatterGatherElements(maxSendScatterGatherElements);
                ret.capabilities.setMaxReceiveScatterGatherElements(maxReceiveScatterGatherElements);
                ret.capabilities.setMaxInlineData(maxInlineData);

                ret.receiveHashConfiguration.setKeyLength(keyLength);
                if(hashFunction != null) ret.receiveHashConfiguration.setFunction(hashFunction);
                if(key != null) ret.receiveHashConfiguration.setKey(key);
                if(hashFieldFlags != null) ret.receiveHashConfiguration.setFieldMask(hashFieldFlags);

                return ret;
            }
        }
    }
}
