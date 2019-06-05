package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.NativeBitMask;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.data.NativeShort;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.Flag;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.verbs.QueuePair.TypeFlag;
import java.util.function.Consumer;

@LinkNative("ibv_device_attr_ex")
public class ExtendedDeviceAttributes extends Struct {

    private final NativeInteger compatibilityMask = integerField("comp_mask");
    private final NativeLong completionTimestampMask = longField("completion_timestamp_mask");
    private final NativeLong coreClockFrequency = longField("hca_core_clock");
    private final NativeLong extendedDeviceCapabilities = longField("device_cap_flags_ex");
    private final NativeInteger maxReceiveQueueCount = integerField("max_wq_type_rq");
    private final NativeInteger rawPacketCapabilities = integerField("raw_packet_caps");
    private final NativeLong maxDeviceMemorySize = longField("max_dm_size");
    private final NativeInteger onDemandPagingTransportCapabilities = integerField("xrc_odp_caps");

    public final DeviceAttributes deviceAttributes = valueField("orig_attr", DeviceAttributes::new);
    public final OnDemandPagingCapabilities onDemandPagingCapabilities = valueField("odp_caps", OnDemandPagingCapabilities::new);
    public final TcpSegmentOffloadingCapabilities tcpSegmentationOffloadCapabilities = valueField("tso_caps", TcpSegmentOffloadingCapabilities::new);
    public final RssCapabilities rssCapabilities = valueField("rss_caps", RssCapabilities::new);
    public final PacketPacingCapabilities packetPacingCapabilities = valueField("packet_pacing_caps", PacketPacingCapabilities::new);
    public final TagMatchingCapabilities tagMatchingCapabilities = valueField("tm_caps", TagMatchingCapabilities::new);
    private final CompletionQueueModerationCapabilities completionQueueModerationCapabilities = valueField("cq_mod_caps", CompletionQueueModerationCapabilities::new);
    private final PciAtomicCapabilities pciAtomicCapabilities = valueField("pci_atomic_caps", PciAtomicCapabilities::new);

    ExtendedDeviceAttributes() {
        super();
    }

    public int getCompatibilityMask() {
        return compatibilityMask.get();
    }

    public long getCompletionTimestampMask() {
        return completionTimestampMask.get();
    }

    public long getCoreClockFrequency() {
        return coreClockFrequency.get();
    }

    public long getExtendedDeviceCapabilities() {
        return extendedDeviceCapabilities.get();
    }

    public int getMaxReceiveQueueCount() {
        return maxReceiveQueueCount.get();
    }

    public int getRawPacketCapabilities() {
        return rawPacketCapabilities.get();
    }

    public long getMaxDeviceMemorySize() {
        return maxDeviceMemorySize.get();
    }

    public int getOnDemandPagingTransportCapabilities() {
        return onDemandPagingTransportCapabilities.get();
    }

    @Override
    public String toString() {
        return "ExtendedDeviceAttributes {\n" +
            "\tcompatibilityMask=" + compatibilityMask +
            ",\n\tcompletionTimestampMask=" + completionTimestampMask +
            ",\n\tcoreClockFrequency=" + coreClockFrequency +
            ",\n\textendedDeviceCapabilities=" + extendedDeviceCapabilities +
            ",\n\tmaxReceiveQueueCount=" + maxReceiveQueueCount +
            ",\n\trawPacketCapabilities=" + rawPacketCapabilities +
            ",\n\tmaxDeviceMemorySize=" + maxDeviceMemorySize +
            ",\n\tonDemandPagingTransportCapabilities=" + onDemandPagingTransportCapabilities +
            ",\n\tdevice=" + deviceAttributes +
            ",\n\tonDemandPagingCapabilities=" + onDemandPagingCapabilities +
            ",\n\ttcpSegmentationOffloadCapabilities=" + tcpSegmentationOffloadCapabilities +
            ",\n\trssCapabilities=" + rssCapabilities +
            ",\n\tpacketPacingCapabilities=" + packetPacingCapabilities +
            ",\n\ttagMatchingCapabilities=" + tagMatchingCapabilities +
            ",\n\tcompletionQueueModerationCapabilities=" + completionQueueModerationCapabilities +
            ",\n\tpciAtomicCapabilities=" + pciAtomicCapabilities +
            "\n}";
    }

    @LinkNative("ibv_odp_caps")
    public static final class OnDemandPagingCapabilities extends Struct {

        private final NativeLong generalCapabilities = longField("general_caps");

        final PerTransportCapabilities perTransportCapabilities = anonymousField(PerTransportCapabilities::new);

        OnDemandPagingCapabilities(final LocalBuffer buffer, long offset) {
            super(buffer, offset);
        }

        public long getGeneralCapabilities() {
            return generalCapabilities.get();
        }

        @Override
        public String toString() {
            return "OnDemandPagingCapabilities {\n" +
                "\tgeneralCapabilities=" + generalCapabilities +
                ",\n\tperTransportCapabilities=" + perTransportCapabilities +
                "\n}";
        }

        @LinkNative("ibv_odp_caps")
        public static final class PerTransportCapabilities extends Struct {

            private final NativeInteger rcOnDemandPagingCapabilities = integerField("rc_odp_caps");
            private final NativeInteger ucOnDemandPagingCapabilities = integerField("uc_odp_caps");
            private final NativeInteger udOnDemandPagingCapabilities = integerField("ud_odp_caps");

            PerTransportCapabilities(LocalBuffer buffer) {
                super(buffer, "per_transport_caps");
            }

            public int getRcOnDemandPagingCapabilities() {
                return rcOnDemandPagingCapabilities.get();
            }

            public int getUcOnDemandPagingCapabilities() {
                return ucOnDemandPagingCapabilities.get();
            }

            public int getUdOnDemandPagingCapabilities() {
                return udOnDemandPagingCapabilities.get();
            }

            @Override
            public String toString() {
                return "PerTransportCapabilities {\n" +
                    "\trcOnDemandPagingCapabilities=" + rcOnDemandPagingCapabilities +
                    ",\n\tucOnDemandPagingCapabilities=" + ucOnDemandPagingCapabilities +
                    ",\n\tudOnDemandPagingCapabilities=" + udOnDemandPagingCapabilities +
                    "\n}";
            }
        }
    }

    @LinkNative("ibv_tso_caps")
    public static final class TcpSegmentOffloadingCapabilities extends Struct {

        private final NativeInteger maxTcpSegmentOffloading = integerField("max_tso");
        private final NativeBitMask<TypeFlag> supportedQueuePairTypes = bitField("supported_qpts");

        TcpSegmentOffloadingCapabilities(LocalBuffer buffer, long offset) {
            super(buffer, offset);
        }

        public int getMaxTcpSegmentOffloading() {
            return maxTcpSegmentOffloading.get();
        }

        public int getSupportedQueuePairTypes() {
            return supportedQueuePairTypes.get();
        }

        @Override
        public String toString() {
            return "TcpSegmentOffloadingCapabilities {\n" +
                "\tmaxTcpSegmentOffloading=" + maxTcpSegmentOffloading +
                ",\n\tsupportedQueuePairTypes=" + supportedQueuePairTypes +
                "\n}";
        }
    }

    @LinkNative("ibv_rss_caps")
    private final static class RssCapabilities extends Struct {

        public enum RxHashField implements Flag {
            SRC_IPV4(1), DST_IPV4(1 << 1),
            SRC_IPV6(1 << 2), DST_IPV6(1 << 3),
            SRC_PORT_TCP(1 << 4), DST_PORT_TCP(1 << 5),
            SRC_PORT_UDP(1 << 6), DST_PORT_UDP(1 << 7),
            IPSEC_SPI(1 << 8), INNER(1 << 31);

            private final int value;

            RxHashField(int value) {
                this.value = value;
            }

            @Override
            public int getValue() {
                return value;
            }
        }

        public enum RxHashFunction implements Flag {
            TOEPLITZ(1);

            private final int value;

            RxHashFunction(int value) {
                this.value = value;
            }

            @Override
            public int getValue() {
                return value;
            }
        }

        private final NativeBitMask<TypeFlag> supportedQueuePairTypes = bitField("supported_qpts");
        private final NativeInteger maxIndirectionTables = integerField("max_rwq_indirection_tables");
        private final NativeInteger maxIndirectionTableSize = integerField("max_rwq_indirection_table_size");
        private final NativeBitMask<RxHashField> rxHashFields = bitField("rx_hash_fields_mask");
        private final NativeBitMask<RxHashFunction> rxHashFunction = bitField("rx_hash_function");

        RssCapabilities(final LocalBuffer buffer, long offset) {
            super(buffer, offset);
        }

        public int getSupportedQueuePairTypes() {
            return supportedQueuePairTypes.get();
        }

        public int getMaxIndirectionTables() {
            return maxIndirectionTables.get();
        }

        public int getMaxIndirectionTableSize() {
            return maxIndirectionTableSize.get();
        }

        public int getRxHashFields() {
            return rxHashFields.get();
        }

        public int getRxHashFunction() {
            return rxHashFunction.get();
        }

        @Override
        public String toString() {
            return "RssCapabilities {\n" +
                "\tsupportedQueuePairTypes=" + supportedQueuePairTypes +
                ",\n\tmaxIndirectionTables=" + maxIndirectionTables +
                ",\n\tmaxIndirectionTableSize=" + maxIndirectionTableSize +
                ",\n\trxHashFields=" + rxHashFields +
                ",\n\trxHashFunction=" + rxHashFunction +
                "\n}";
        }
    }

    @LinkNative("ibv_packet_pacing_caps")
    public static final class PacketPacingCapabilities extends Struct {

        private final NativeInteger minQueuePairRateLimit = integerField("qp_rate_limit_min");
        private final NativeInteger maxQueuePairRateLimit = integerField("qp_rate_limit_max");
        private final NativeBitMask<TypeFlag> supportedQueuePairTypes = bitField("supported_qpts");

        PacketPacingCapabilities(final LocalBuffer buffer, long offset) {
            super(buffer, offset);
        }

        public int getMinQueuePairRateLimit() {
            return minQueuePairRateLimit.get();
        }

        public int getMaxQueuePairRateLimit() {
            return maxQueuePairRateLimit.get();
        }

        public int getSupportedQueuePairTypes() {
            return supportedQueuePairTypes.get();
        }

        @Override
        public String toString() {
            return "PacketPacingCapabilities {\n" +
                "\tminQueuePairRateLimit=" + minQueuePairRateLimit +
                ",\n\tmaxQueuePairRateLimit=" + maxQueuePairRateLimit +
                ",\n\tsupportedQueuePairTypes=" + supportedQueuePairTypes +
                "\n}";
        }
    }

    @LinkNative("ibv_tm_caps")
    public static final class TagMatchingCapabilities extends Struct {

        public enum TagMatchingCapabilityFlag implements Flag {
            RC(1);

            private final int value;

            TagMatchingCapabilityFlag(int value) {
                this.value = value;
            }

            @Override
            public int getValue() {
                return value;
            }
        }

        private final NativeInteger maxRendezvousRequestHeaderSize = integerField("max_rndv_hdr_size");
        private final NativeInteger maxTaggedBufferCount = integerField("max_num_tags");
        private final NativeBitMask<TagMatchingCapabilityFlag> flags = bitField("flags");
        private final NativeInteger maxOutstandingOperations = integerField("max_ops");
        private final NativeInteger maxScatterGatherElements = integerField("max_sge");

        TagMatchingCapabilities(final LocalBuffer buffer, long offset) {
            super(buffer, offset);
        }

        public int getMaxRendezvousRequestHeaderSize() {
            return maxRendezvousRequestHeaderSize.get();
        }

        public int getMaxTaggedBufferCount() {
            return maxTaggedBufferCount.get();
        }

        public int getFlags() {
            return flags.get();
        }

        public int getMaxOutstandingOperations() {
            return maxOutstandingOperations.get();
        }

        public int getMaxScatterGatherElements() {
            return maxScatterGatherElements.get();
        }

        @Override
        public String toString() {
            return "TagMatchingCapabilities {\n" +
                "\tmaxRendezvousRequestHeaderSize=" + maxRendezvousRequestHeaderSize +
                ",\n\tmaxTaggedBufferCount=" + maxTaggedBufferCount +
                ",\n\tflags=" + flags +
                ",\n\tmaxOutstandingOperations=" + maxOutstandingOperations +
                ",\n\tmaxScatterGatherElements=" + maxScatterGatherElements +
                "\n}";
        }
    }

    @LinkNative("ibv_cq_moderation_caps")
    public static final class CompletionQueueModerationCapabilities extends Struct {

        private final NativeShort maxCompletionQueueCount = shortField("max_cq_count");
        private final NativeShort maxCompletionQueuePeriod = shortField("max_cq_period");

        CompletionQueueModerationCapabilities(final LocalBuffer buffer, long offset) {
            super(buffer, offset);
        }

        public short getMaxCompletionQueueCount() {
            return maxCompletionQueueCount.get();
        }

        public short getMaxCompletionQueuePeriod() {
            return maxCompletionQueuePeriod.get();
        }

        @Override
        public String toString() {
            return "CompletionQueueModerationCapabilities {\n" +
                "\tmaxCompletionQueueCount=" + maxCompletionQueueCount +
                ",\n\tmaxCompletionQueuePeriod=" + maxCompletionQueuePeriod +
                "\n}";
        }
    }

    @LinkNative("ibv_pci_atomic_caps")
    public static final class PciAtomicCapabilities extends Struct {

        public enum PciAtomicOperationSize implements Flag {
            SUPPORTS_4_BYTE_SIZE(1),
            SUPPORTS_8_BYTE_SIZE(1 << 1),
            SUPPORTS_16_BYTE_SIZE(1 << 2);

            private final int value;

            PciAtomicOperationSize(int value) {
                this.value = value;
            }

            @Override
            public int getValue() {
                return value;
            }
        }

        private final NativeBitMask<PciAtomicOperationSize> fetchAndAdd = bitField("fetch_add");
        private final NativeBitMask<PciAtomicOperationSize> swap = bitField("swap");
        private final NativeBitMask<PciAtomicOperationSize> compareAndSwap = bitField("compare_swap");

        PciAtomicCapabilities(final LocalBuffer buffer, long offset) {
            super(buffer, offset);
        }

        public int getFetchAndAdd() {
            return fetchAndAdd.get();
        }

        public int getSwap() {
            return swap.get();
        }

        public int getCompareAndSwap() {
            return compareAndSwap.get();
        }

        @Override
        public String toString() {
            return "PciAtomicCapabilities {\n" +
                "\tfetchAndAdd=" + fetchAndAdd +
                ",\n\tswap=" + swap +
                ",\n\tcompareAndSwap=" + compareAndSwap +
                "\n}";
        }
    }

    @LinkNative("ibv_query_device_ex_input")
    public static final class QueryExtendedDeviceInput extends Struct {

        private final NativeInteger compatibilityMask = integerField("comp_mask");

        public QueryExtendedDeviceInput() {}

        public QueryExtendedDeviceInput(final Consumer<QueryExtendedDeviceInput> configurator) {
            configurator.accept(this);
        }

        public int getCompatibilityMask() {
            return compatibilityMask.get();
        }

        public void setCompatibilityMask(final int value) {
            compatibilityMask.set(value);
        }

        @Override
        public String toString() {
            return "QueryExtendedDeviceInput {\n" +
                "\tcompatibilityMask=" + compatibilityMask +
                "\n}";
        }
    }
}
