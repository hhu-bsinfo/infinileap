package de.hhu.bsinfo.infinileap;

import static org.linux.rdma.infinileap_h.*;

import de.hhu.bsinfo.infinileap.util.Struct;
import jdk.incubator.foreign.MemoryAddress;

public final class WorkCompletion extends Struct {

    public WorkCompletion() {
        super(ibv_wc.allocate());
    }

    public WorkCompletion(MemoryAddress address) {
        super(address, ibv_wc.$LAYOUT());
    }

    public long getWorkRequestId() {
        return ibv_wc.wr_id$get(segment());
    }

    public int getStatus() {
        return ibv_wc.status$get(segment());
    }

    public int getOpcode() {
        return ibv_wc.opcode$get(segment());
    }

    public int getVendorError() {
        return ibv_wc.vendor_err$get(segment());
    }

    public int getByteLength() {
        return ibv_wc.byte_len$get(segment());
    }

    public int getImmediateData() {
        return ibv_wc.imm_data$get(segment());
    }

    public int getInvalidatedRemoteKey() {
        return ibv_wc.invalidated_rkey$get(segment());
    }

    public int getQueuePairNumber() {
        return ibv_wc.qp_num$get(segment());
    }

    public int getSourceQueuePair() {
        return ibv_wc.src_qp$get(segment());
    }

    public int getFlags() {
        return ibv_wc.wc_flags$get(segment());
    }

    public short getPartitionKeyIndex() {
        return ibv_wc.pkey_index$get(segment());
    }

    public short getSourceLocalId() {
        return ibv_wc.slid$get(segment());
    }

    public byte getServiceLevel() {
        return ibv_wc.sl$get(segment());
    }

    public byte getDesinationLocalIdPathBits() {
        return ibv_wc.dlid_path_bits$get(segment());
    }

    public void setWorkRequestId(final long value) {
        ibv_wc.wr_id$set(segment(), value);
    }

    public void setStatus(final int value) {
        ibv_wc.status$set(segment(), value);
    }

    public void setOpcode(final int value) {
        ibv_wc.opcode$set(segment(), value);
    }

    public void setVendorError(final int value) {
        ibv_wc.vendor_err$set(segment(), value);
    }

    public void setByteLength(final int value) {
        ibv_wc.byte_len$set(segment(), value);
    }

    public void setImmediateData(final int value) {
        ibv_wc.imm_data$set(segment(), value);
    }

    public void setInvalidatedRemoteKey(final int value) {
        ibv_wc.invalidated_rkey$set(segment(), value);
    }

    public void setQueuePairNumber(final int value) {
        ibv_wc.qp_num$set(segment(), value);
    }

    public void setSourceQueuePair(final int value) {
        ibv_wc.src_qp$set(segment(), value);
    }

    public void setFlags(final int value) {
        ibv_wc.wc_flags$set(segment(), value);
    }

    public void setPartitionKeyIndex(final short value) {
        ibv_wc.pkey_index$set(segment(), value);
    }

    public void setSourceLocalId(final short value) {
        ibv_wc.slid$set(segment(), value);
    }

    public void setServiceLevel(final byte value) {
        ibv_wc.sl$set(segment(), value);
    }

    public void setDestinationLocalIdPathBits(final byte value) {
        ibv_wc.dlid_path_bits$set(segment(), value);
    }
}
